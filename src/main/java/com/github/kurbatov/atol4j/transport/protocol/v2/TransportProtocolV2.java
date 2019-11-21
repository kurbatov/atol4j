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
package com.github.kurbatov.atol4j.transport.protocol.v2;

import com.github.kurbatov.atol4j.transport.Transport;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.kurbatov.atol4j.transport.protocol.TransportProtocol;
import static com.github.kurbatov.atol4j.transport.protocol.v2.Token.*;

/**
 * Строит сообщения для ККМ в соответствии с протоколом нижнего уровня v2.
 *
 * @deprecated Эта версия протокола не используется новыми устройствами
 * и сохранена в целях совместимости со старыми.
 * @see com.github.kurbatov.atol4j.transport.protocol.v3.TransportProtocolV3
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class TransportProtocolV2 implements TransportProtocol {

    private final Transport transport;

    private static final byte[] EMPTY = new byte[0];
    private static final byte[] ENQ_MSG = new byte[] {ENQ};
    private static final byte[] ACK_MSG = new byte[] {ACK};
    private static final byte[] NAK_MSG = new byte[] {NAK};
    private static final byte[] EOT_MSG = new byte[] {EOT};

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportProtocolV2.class);

    /**
     * Создаёт протокол нижнего уровня версии 2, который взаимодействует с
     * устройством через указанный транспорт.
     *
     * @param transport транспорт, который осуществляет доставку сообщений между
     * клиентом и устройством
     * @deprecated Эта версия протокола не используется новыми устройствами
     * и сохранена в целях совместимости со старыми.
     * @see com.github.kurbatov.atol4j.transport.protocol.v3.TransportProtocolV3
     */
    public TransportProtocolV2(Transport transport) {
        this.transport = transport;
    }

    @Override
    public void start() {
        transport.connect();
    }

    @Override
    public void stop() {
        transport.disconnect();
    }
    
    @Override
    public CompletableFuture<byte[]> send(byte[] message) {
        try {
            byte answer = 0;
            for (int i = 0; i < 5 && answer != ACK; i++) {
                transport.write(ENQ_MSG);
                answer = transport.read(1, Timeout.T1)[0];
                if (answer == ENQ) {
                    try {
                        Thread.sleep(Timeout.T7);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Выполнение прервано в момент паузы при конфликте", e);
                    }
                }
            }
            if (answer != ACK) {
                transport.write(EOT_MSG);
                throw new RuntimeException("Устройство не готово к приёму сообщений");
            }
            answer = NAK;
            byte[] frame = wrap(message);
            for (int i = 0; i < 10 && answer == NAK; i++) {
                transport.write(frame);
                answer = transport.read(1, Timeout.T4)[0];
            }
            if (answer != ACK) {
                throw new RuntimeException("Устройство не подтвердило приём сообщения: " + Arrays.toString(frame));
            }
            transport.write(EOT_MSG);
            for (int i = 0; i < 5 && answer != ENQ; i++) {
                try {
                    answer = transport.read(1, Timeout.T5(message[0]))[0];
                } catch (TimeoutException e) {
                    return CompletableFuture.completedFuture(EMPTY); // считаем, что нет ответа
                }
                if (answer != ENQ) {
                    transport.write(NAK_MSG);
                }
            }
            transport.write(ACK_MSG);
            byte[] response = EMPTY;
            for (int i = 0; i < 10; i++) {
                while (response == EMPTY || response[response.length - 1] != ETX) {
                    byte[] part = transport.read();
                    if (part != null) {
                        byte[] tmp = new byte[response.length + part.length];
                        if (response != EMPTY) {
                            System.arraycopy(response, 0, tmp, 0, response.length);
                        }
                        System.arraycopy(part, 0, tmp, response.length, part.length);
                        response = tmp;
                    }
                }
                if (!check(response)) {
                    transport.write(NAK_MSG);
                    response = EMPTY;
                }
            }
            if (response == EMPTY) {
                throw new RuntimeException("Не удалось прочитать ответ от устройства.");
            }
            transport.write(ACK_MSG);
            answer = transport.read(1)[0];
            if (answer != EOT) {
                LOGGER.warn("Неожиданный конец передачи данных: {}", answer);
            }
            return CompletableFuture.completedFuture(unwrap(response));
        } catch (TimeoutException ex) {
            throw new RuntimeException("Ошибка при попытке отправить сообщение устройству", ex);
        }
    }

    /**
     * Строит сообщение для передачи данных ККМ.
     *
     * Экранирует управляющие символы, подсчитывает контрольную сумму и
     * оборачивает данные в управляющие символы.
     *
     * @param data данные для передачи ККМ
     * @return сообщение для передачи ККМ
     */
    byte[] wrap(byte[] data) {
        int masking = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == ETX || data[i] == DLE) {
                masking++;
            }
        }
        byte[] result = new byte[data.length + masking + 3];
        int last = result.length - 1;
        result[0] = STX;
        result[last - 1] = ETX;
        int crc = ETX;
        int j = 1;
        for (int i = 0; i < data.length; i++) {
            byte value = data[i];
            if (value == ETX || value == DLE) {
                result[j++] = DLE;
                crc ^= DLE;
            }
            result[j++] = value;
            crc ^= value;
        }
        result[last] = (byte) crc;
        return result;
    }

    /**
     * Извлекает полезные данные из сообщения.
     *
     * @param frame сообщение
     * @return полезные данные
     */
    byte[] unwrap(byte[] frame) {
        int masking = 0;
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == ETX || frame[i] == DLE) {
                masking++;
            }
        }
        byte[] result = new byte[frame.length - masking - 3];
        int j = 1;
        for (int i = 0; i < result.length; i++) {
            byte value = frame[j++];
            if (value == DLE) {
                value = frame[j++];
            }
            result[i] = value;
        }
        return result;
    }

    /**
     * Проверяет корректность сообщения.
     *
     * Вычисляет контрольную сумму сообщения и сравнивает её с переданной в теле
     * сообщения.
     *
     * @param frame сообщений
     * @return true - сообщение корректно, false - сообщение некорректно
     */
    boolean check(byte[] frame) {
        int crc = 0;
        for (int i = 1; i < frame.length - 1; i++) {
            crc ^= frame[i];
        }
        return crc == frame[frame.length - 1];
    }

}
