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
package com.github.kurbatov.atol4j.command.common.request;

import com.github.kurbatov.atol4j.CashRegister;
import com.github.kurbatov.atol4j.charset.ExternalCharset;
import com.github.kurbatov.atol4j.command.Command;
import com.github.kurbatov.atol4j.command.Result;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

/**
 * Запрос сведений о типе устройства.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class DeviceTypeRequest implements Command<DeviceTypeRequest.Response> {

    /**
     * Экземпляр запроса сведений о типе устройства.
     */
    public static final DeviceTypeRequest INSTANCE = new DeviceTypeRequest();
    
    private static final byte[] COMMAND = {(byte) 0xA5};

    @Override
    public CompletableFuture<DeviceTypeRequest.Response> executeOn(CashRegister device) {
        return device.execute(COMMAND).thenApply(r -> new Response(r));
    }

    /**
     * Сведения о типе устройства.
     */
    public static class Response extends Result {
        
        private byte protocolVersion;
        private byte type;
        private byte model;
        private byte[] mode = new byte[2];
        private byte[] deviceVersion = new byte[5];
        private String deviceName;
        
        public Response(byte[] buffer) {
            super(buffer[0], (byte) 0);
            if (buffer.length >= 11) {
                protocolVersion = buffer[1];
                type = buffer[2];
                model = buffer[3];
                System.arraycopy(buffer, 4, mode, 0, 2);
                System.arraycopy(buffer, 6, deviceVersion, 0, 5);
                int nameLen = buffer.length - 11;
                if (nameLen > 0) {
                    byte[] name = new byte[nameLen];
                    System.arraycopy(buffer, 11, name, 0, nameLen);
                    deviceName = ExternalCharset.INSTANCE.decode(ByteBuffer.wrap(name)).toString();
                }
            }
        }

        /**
         * Получить версию протокола.
         *
         * @return версия протокола
         */
        public byte getProtocolVersion() {
            return protocolVersion;
        }

        /**
         * Получить код типа устройства.
         *
         * @return код типа устройства
         */
        public byte getType() {
            return type;
        }

        /**
         * Получить код модели устройства.
         *
         * @return код модели устройства
         */
        public byte getModel() {
            return model;
        }

        /**
         * Получить текущий режим устройства.
         *
         * @return [режим, подрежим]
         */
        public byte[] getMode() {
            return mode;
        }

        /**
         * Получить версию устройства.
         *
         * @return [версия, ревизия]
         */
        public byte[] getDeviceVersion() {
            return deviceVersion;
        }

        /**
         * Получить человеко-читаемое название устройства.
         *
         * @return название устройства
         */
        public String getDeviceName() {
            return deviceName;
        }
        
    }
    
}
