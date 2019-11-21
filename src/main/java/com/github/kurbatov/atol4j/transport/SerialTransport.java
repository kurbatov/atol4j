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
package com.github.kurbatov.atol4j.transport;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import jssc.SerialNativeInterface;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Реализация транспортировки данных по последовательному интерфейсу.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class SerialTransport implements Transport {

    private final SerialPort port;
    
    private final Set<Consumer<byte[]>> subscribers = new CopyOnWriteArraySet<>();

    private static final List<Integer> BAUDRATES = Arrays.asList(
            SerialPort.BAUDRATE_256000,
            SerialPort.BAUDRATE_128000,
            SerialPort.BAUDRATE_115200,
            SerialPort.BAUDRATE_57600,
            SerialPort.BAUDRATE_38400,
            SerialPort.BAUDRATE_19200,
            SerialPort.BAUDRATE_9600
    );

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialTransport.class);

    /**
     * Получить список имён доступных последовательных портов.
     *
     * @return имена доступных последовательных портов
     */
    public static String[] getAvailablePortNames() {
        String[] portNames;
        if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_MAC_OS_X) {
            portNames = SerialPortList.getPortNames("/dev/", Pattern.compile("tty\\..*"));
        } else {
            portNames = SerialPortList.getPortNames();
        }
        return portNames;
    }

    /**
     * Создаёт объект, который передаёт команды ККТ по последовательному
     * интерфейсу.
     *
     * @param portName имя порта
     */
    public SerialTransport(String portName) {
        this.port = new SerialPort(portName);
    }

    @Override
    public void connect() {
        if (!port.isOpened()) {
            SerialPortException error = null;
            for (Integer baudrate : BAUDRATES) {
                try {
                    connect(baudrate);
                    port.addEventListener(event -> {
                        if (event.isRXCHAR() && event.getEventValue() > 0) {
                            try {
                                byte[] buffer = port.readBytes();
                                subscribers.forEach(consumer -> consumer.accept(buffer));
                            } catch (SerialPortException e) {
                                LOGGER.warn("Cannot read asynchronous response", e);
                            }
                        }
                    });
                    error = null;
                    break;
                } catch (SerialPortException ex) {
                    error = ex;
                }
            }
            if (error != null) {
                throw new RuntimeException("Невозможно подключиться к устройству", error);
            }
        }
    }

    @Override
    public void disconnect() {
        try {
            if (port.isOpened()) {
                port.purgePort(SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_TXCLEAR);
                port.closePort();
            }
        } catch (SerialPortException ex) {
            throw new RuntimeException("Невозможно корректно отключиться от устройства", ex);
        }
    }

    @Override
    public void write(byte[] b) {
        try {
            port.writeBytes(b);
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] read() {
        try {
            return port.readBytes();
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] read(int count) {
        try {
            return port.readBytes(count);
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] read(int count, int timeout) throws TimeoutException {
        try {
            return port.readBytes(count, timeout);
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        } catch (SerialPortTimeoutException ex) {
            throw new TimeoutException(ex.getMessage());
        }
    }
    
    @Override
    public void subscribe(Consumer<byte[]> consumer) {
        subscribers.add(consumer);
    }

    private void connect(int baudrate) throws SerialPortException {
        port.openPort();
        port.setParams(
                baudrate,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        port.setEventsMask(SerialPort.MASK_RXCHAR);
    }

}
