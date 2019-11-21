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
import com.github.kurbatov.atol4j.command.Command;
import com.github.kurbatov.atol4j.command.Result;
import java.util.concurrent.CompletableFuture;

/**
 * Запрос кода состояния устройства.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class DeviceStateCodeRequest implements Command<DeviceStateCodeRequest.Response>{

    public static final DeviceStateCodeRequest INSTANCE = new DeviceStateCodeRequest();
    
    private static final byte[] COMMAND = {(byte) 0x45};
    
    @Override
    public CompletableFuture<Response> executeOn(CashRegister device) {
        return device.execute(COMMAND).thenApply(r -> new Response(r));
    }
    
    /**
     * Коды состояния устройства.
     */
    public static class Response extends Result {

        private final int mode;
        private final int subMode;
        private final boolean hasPaper;
        private final boolean connected;
        private final boolean mechanicalErr;
        private final boolean cutterErr;
        private final boolean printerErr;
        
        public Response(byte[] buffer) {
            super(buffer[0], buffer[2], (byte) 0);
            mode = buffer[1] & 0x0F;
            subMode = buffer[1] & 0xF0;
            byte flags = buffer[2];
            hasPaper = (flags & 1) == 0;
            connected = (flags & 2) == 0;
            mechanicalErr = (flags & 4) > 0;
            cutterErr = (flags & 8) > 0;
            printerErr = (flags & 16) > 0;
        }

        /**
         * Получить режим устройства.
         *
         * @return код режима устройства
         */
        public int getMode() {
            return mode;
        }

        /**
         * Получить подрежим устройства.
         *
         * @return код подрежима устройства
         */
        public int getSubMode() {
            return subMode;
        }

        /**
         * Узнать наличие бумаги в принтере.
         *
         * @return true - бумага в наличие, false - нет бумаги
         */
        public boolean hasPaper() {
            return hasPaper;
        }

        /**
         * Узнать состояние подключения устройства.
         *
         * @return true - устройство подключено, false - устройство не подключено
         */
        public boolean isConnected() {
            return connected;
        }

        /**
         * Определить наличие механических ошибок.
         *
         * @return true - присутствует механическая ошибка, false - механические
         * ошибки отсутствуют
         */
        public boolean isMechanicalErr() {
            return mechanicalErr;
        }

        /**
         * Определить наличие ошибок отрезного ножа.
         *
         * @return true - нож в состоянии ошибки, false - отсутствуют ошибки
         * ножа
         */
        public boolean isCutterErr() {
            return cutterErr;
        }

        /**
         * Определить наличие ошибок принтера.
         *
         * @return true - принтер в состоянии ошибки, false - отсутствуют ошибки
         * принтера
         */
        public boolean isPrinterErr() {
            return printerErr;
        }
        
    }
    
}
