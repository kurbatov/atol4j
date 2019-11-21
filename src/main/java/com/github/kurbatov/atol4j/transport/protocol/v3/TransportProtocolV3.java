/*
 * atol4j - клиентская библиотека для контрольной кассовой техники (ККТ) АТОЛ
 * Copyright (C) 2019 Олег Викторович Курбатов
 *
 * Это программа является свободным программным обеспечением. Вы можете
 * распространять и/или модифицировать её согласно условиям Стандартной
 * Общественной Лицензии GNU, опубликованной Фондом Свободного Программного
 * Обеспечения, версии 3 или, по Вашему желанию, любой более поздней версии.
 *
 * Эта программа распространяется в надежде, что она будет полезной, но БЕЗ
 * ВСЯКИХ ГАРАНТИЙ, в том числе подразумеваемых гарантий ТОВАРНОГО
 * СОСТОЯНИЯ ПРИ ПРОДАЖЕ и ГОДНОСТИ ДЛЯ ОПРЕДЕЛЁННОГО ПРИМЕНЕНИЯ. Смотрите
 * Стандартную Общественную Лицензию GNU для получения дополнительной
 * информации.
 *
 * Вы должны были получить копию Стандартной Общественной Лицензии GNU
 * вместе с программой. В случае её отсутствия, смотрите
 * http://www.gnu.org/licenses/.
 */
package com.github.kurbatov.atol4j.transport.protocol.v3;

import com.github.kurbatov.atol4j.transport.Transport;
import com.github.kurbatov.atol4j.transport.protocol.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.kurbatov.atol4j.transport.protocol.TransportProtocol;
import static com.github.kurbatov.atol4j.transport.protocol.v3.Token.*;

/**
 * Имплементация протокола нижнего уровня версии 3.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class TransportProtocolV3 implements TransportProtocol {

    private final Transport transport;

    private final AtomicInteger packageId = new AtomicInteger();

    private final AtomicInteger taskId = new AtomicInteger();

    private final Map<Byte, byte[]> sentPackages = new ConcurrentHashMap<>();
    
    private final Map<Byte, CompletableFuture<byte[]>> pendingCommands = new ConcurrentHashMap<>();
    
    private final ByteBuffer buffer = new ByteBuffer(1024);
    
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "atol4j-TransportEvents"));
    
    private static final byte[] EMPTY = new byte[] {};

    private static final int MAX_ID = 0xDF;

    // флаги
    private static final byte NEED_RESULT = 1;
    private static final byte IGNORE_ERR = 2;
    private static final byte ASYNC = 4;
    
    // параметры рассчёта контрольной суммы
    private static final int CRC8INIT = 0xFF;
    private static final int CRC8POLY = 0x31;// = X^8+X^5+X^4+X^0

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportProtocolV3.class);
    
    /**
     * Создаёт протокол нижнего уровня версии 3, который взаимодействует с
     * устройством через указанный транспорт.
     *
     * @param transport транспорт, который осуществляет доставку сообщений между
     * клиентом и устройством
     */
    public TransportProtocolV3(Transport transport) {
        this.transport = transport;
        transport.subscribe(this::processResponse);
    }

    @Override
    public void start() {
        transport.connect();
    }

    @Override
    public void stop() {
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        transport.disconnect();
    }

    @Override
    public CompletableFuture<byte[]> send(byte[] message) {
        byte[] command = wrap(addTask(message, NEED_RESULT));
        byte packId = command[3];
        sentPackages.put(packId, command);
        byte taskId = command[6];
        CompletableFuture<byte[]> result = new CompletableFuture<>();
        pendingCommands.put(taskId, result);
        transport.write(command);
        return result;
    }

    /**
     * Посылает устройству команду на очистку очереди задач.
     */
    public void abort() {
        byte[] command = wrap(Command.ABORT);
        sentPackages.put(command[3], command);
        transport.write(command);
    }
    
    /**
     * Обрабатывает ответ от устройства.
     *
     * @param msg ответ от устройства
     */
    public synchronized void processResponse(byte[] msg) {
        buffer.append(msg);
        for(int stxIndex = buffer.find(STX); !buffer.isEmpty() && stxIndex > -1; stxIndex = buffer.find(STX)) {
            buffer.skip(stxIndex);
            if (buffer.size() >= 5) {
                int len = ((buffer.get(1) & 0x7F) | ((buffer.get(2) & 0xFF) << 7)) + 5;
                for (int escIndex = buffer.find(ESC, 4); escIndex > -1 && escIndex < len; escIndex = buffer.find(ESC, escIndex + 1)) {
                    len++;
                }
                if (buffer.size() >= len) {
                    processMessage(buffer.take(len));
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }
    
    /**
     * Обрабатывает один пакет транспортного уровня.
     *
     * @param msg пакет транспортного уровня
     */
    void processMessage(byte[] msg) {
        byte packId = msg[3];
        try {
            byte[] payload = unwrap(msg);
            if (payload.length == 0) {
                if (sentPackages.containsKey(packId)) { // ККТ запрашивает повтор пакета
                    //удаляем пакет из истории, чтобы избежать циклических повторов передачи
                    transport.write(sentPackages.remove(packId));
                } else {
                    LOGGER.warn("ККТ запрашивает повтор несуществующего пакета: {}", packId);
                }
                return;
            }
            byte status = payload[0];
            byte id = -1;
            if (status == Status.ASYNC_RESULT || status == Status.ASYNC_ERROR) {
                id = payload[1];
                status = status == Status.ASYNC_RESULT ? Status.RESULT : Status.ERROR;
                payload = Arrays.copyOfRange(payload, 2, payload.length);
            } else {
                payload = Arrays.copyOfRange(payload, 1, payload.length);
            }
            processResult(id, status, payload);
            if (packId != (byte) 0xF0 && (status == Status.RESULT || status == Status.ERROR)) {
                byte[] ack = wrap(ack(id));
                sentPackages.put(ack[3], ack);
                transport.write(ack);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Обнаружена ошибка во входящем пакете.", e);
            transport.write(wrap(EMPTY, packId));
        }
    }

    byte[] wrap(byte... buf) {
        byte id = (byte) packageId.updateAndGet(operand -> operand == MAX_ID ? 0 : operand + 1);
        return wrap(buf, id);
    }
    
    byte[] wrap(byte[] buf, byte id) {
        int masking = 0;
        for (int i = 0; i < buf.length; i++) {
            byte b = buf[i];
            if (b == STX || b == ESC) {
                masking++;
            }
        }
        int len = buf.length;
        byte[] result = new byte[len + masking + 5];
        result[0] = STX;
        result[1] = (byte) (len & 0x7F);
        result[2] = (byte) (len >> 7);
        result[3] = id;
        int crc = CRC8INIT ^ (0xFF & id);
        for (int k = 0; k < 8; k++) {
            if ((crc & 0x80) == 128) {
                crc = (crc << 1) ^ CRC8POLY;
            } else {
                crc <<= 1;
            }
        }
        for (int i = 0, j = 4; i < buf.length; i++) {
            byte b = buf[i];
            if (b == STX) {
                result[j++] = ESC;
                result[j++] = TSTX;
            } else if (b == ESC) {
                result[j++] = ESC;
                result[j++] = TESC;
            } else {
                result[j++] = b;
            }
            crc ^= 0xFF & b;
            for (int k = 0; k < 8; k++) {
                if ((crc & 0x80) == 128) {
                    crc = (crc << 1) ^ CRC8POLY;
                } else {
                    crc <<= 1;
                }
            }
        }
        crc = 0xFF & crc;
        if (crc == STX) {
            byte[] temp = new byte[result.length + 1];
            System.arraycopy(result, 0, temp, 0, result.length);
            result = temp;
            result[result.length - 2] = ESC;
            result[result.length - 1] = TSTX;
        } else if (crc == ESC) {
            byte[] temp = new byte[result.length + 1];
            System.arraycopy(result, 0, temp, 0, result.length);
            result = temp;
            result[result.length - 2] = ESC;
            result[result.length - 1] = TESC;
        } else {
            result[result.length - 1] = (byte) crc;
        }
        return result;
    }

    static byte[] unwrap(byte[] r) {
        int len = (r[1] & 0x7F) | (r[2] << 7);
        int actualLen = r.length - 5;
        for (int i = 4; i < r.length - 2; i++) {
            if (r[i] == ESC) {
                actualLen--;
            }
        }
        if (len != actualLen) {
            throw new IllegalArgumentException(String.format("Неверная длинна данных. Указано: %d. Получено: %d.", len, actualLen));
        }
        byte[] result = new byte[len];
        byte id = r[3];
        int crc = CRC8INIT ^ (0xFF & id);
        if (len == 0) {
            LOGGER.warn("Получено пустое сообщение в ответ на пакет с идентификатором {}.", id);
        }
        for (int k = 0; k < 8; k++) {
            if ((crc & 0x80) == 128) {
                crc = (crc << 1) ^ CRC8POLY;
            } else {
                crc <<= 1;
            }
        }
        for (int i = 4, j = 0; j < result.length; j++) {
            byte b = r[i++];
            if (b == ESC) {
                b = r[i++];
                if (b == TSTX) {
                    b = STX;
                } else if (b == TESC) {
                    b = ESC;
                }
            }
            result[j] = b;
            crc ^= 0xFF & b;
            for (int k = 0; k < 8; k++) {
                if ((crc & 0x80) == 128) {
                    crc = (crc << 1) ^ CRC8POLY;
                } else {
                    crc <<= 1;
                }
            }
        }
        crc = 0xFF & crc;
        int expectedCRC = 0xFF & r[r.length - 1];
        if (crc != expectedCRC && (expectedCRC == TSTX ^ crc != STX) && (expectedCRC == TESC ^ crc != ESC)) {
            throw new IllegalArgumentException(String.format("Неверная контрольная сумма. Указано: %d. Получено: %d.", expectedCRC, crc));
        }
        return result;
    }

    /**
     * Формирует команду добавления задачи в очередь задач устройства.
     *
     * @param task задача
     * @param flags флаги
     * @return команда добавления задачи в очередь задач устройства
     */
    byte[] addTask(byte[] task, byte flags) {
        int id = taskId.updateAndGet(operand -> operand == MAX_ID ? 0 : operand + 1);
        byte[] result = new byte[task.length + 3];
        result[0] = Command.ADD;
        result[1] = flags;
        result[2] = (byte) (id & MAX_ID);
        System.arraycopy(task, 0, result, 3, task.length);
        return result;
    }
    
    byte[] ack(byte id) {
        return new byte[] {Command.ACK, id};
    }

    void processResult(byte id, byte status, byte[] msg) {
        if (id == -1) {
            return; // это ответ на подтверждение приёма результата
        }
        CompletableFuture<byte[]> future = pendingCommands.get(id);
        if (future == null) {
            LOGGER.warn("Получен результат для несуществующей команды {}. {}: {}", id, status, Arrays.toString(msg));
        } else if (!future.isDone()) {
            LOGGER.debug("Обновлён статус команды {}: {}", id, status);
            executor.execute(() -> future.complete(msg));
        }
    }

}
