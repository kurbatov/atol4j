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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;

/**
 * Запрос состояния устройства.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class DeviceStateRequest implements Command<DeviceStateRequest.Response>{

    /**
     * Экземпляр запроса состояния устройства.
     */
    public static final DeviceStateRequest INSTANCE = new DeviceStateRequest();
    
    private static final byte[] COMMAND = {0x3F};
    
    @Override
    public CompletableFuture<Response> executeOn(CashRegister device) {
        return device.execute(COMMAND).thenApply(r -> new Response(r));
    }
    
    /**
     * Состояние устройства.
     */
    public static class Response extends Result {

        private final int cashierNumber;
        private final int numberInPlacement;
        private final LocalDate date;
        private final LocalTime time;
        private final int deviceSerialNumber;
        private final byte deviceModelId;
        private final int mode;
        private final int subMode;
        private final int billNumber;
        private final int shiftNumber;
        private final byte billState;
        private final int billSum;
        private final int scale;
        private final byte portType;
        private final boolean fiscalized;
        private final boolean shiftOpen;
        private final boolean cashBoxOpen;
        private final boolean hasPaper;
        private final boolean hoodOpen;
        private final boolean fiscalCounterActive;
        private final boolean batteryOk;
        
        public Response(byte[] buf) {
            super(buf[0], (byte) 0, (byte) 0);
            cashierNumber = (int) decode(buf[1]);
            numberInPlacement = buf[2] & 0xFF;
            date = LocalDate.of(2000 + (int) decode(buf[3]), (int) decode(buf[4]), (int) decode(buf[5]));
            time = LocalTime.of((int) decode(buf[6]), (int) decode(buf[7]), (int) decode(buf[8]));
            byte flags = buf[9];
            fiscalized = (flags & 1) > 0;
            shiftOpen = (flags & 2) > 0;
            cashBoxOpen = (flags & 4) > 0;
            hasPaper = (flags & 8) > 0;
            hoodOpen = (flags & 32) > 0;
            fiscalCounterActive = (flags & 64) > 0;
            batteryOk = (flags & 128) == 0;
            deviceSerialNumber = (int) decode(buf[10], buf[11], buf[12], buf[13]);
            deviceModelId = buf[14];
            mode = buf[17] & 0x0F;
            subMode = buf[17] & 0xF0;
            billNumber = (int) decode(buf[18], buf[19]);
            shiftNumber = (int) decode(buf[20], buf[21]);
            billState = buf[22];
            billSum = (int) decode(buf[23], buf[24], buf[25], buf[26], buf[27]);
            scale = buf[28];
            portType = buf[29];
        }

        /**
         * Получить номер активного кассира.
         *
         * @return номер кассира
         */
        public int getCashierNumber() {
            return cashierNumber;
        }

        /**
         * Получить номер в месте размещения.
         *
         * @return номер в месте размещения
         */
        public int getNumberInPlacement() {
            return numberInPlacement;
        }

        /**
         * Получить дату.
         *
         * @return дата на устройстве
         */
        public LocalDate getDate() {
            return date;
        }

        /**
         * Получить время.
         *
         * @return время на устройстве
         */
        public LocalTime getTime() {
            return time;
        }

        /**
         * Получить серийный номер устройства.
         *
         * @return серийный номер устройства
         */
        public int getDeviceSerialNumber() {
            return deviceSerialNumber;
        }

        /**
         * Получить идентификатор модели устройства.
         *
         * @return идентификатор модели устройства
         */
        public byte getDeviceModelId() {
            return deviceModelId;
        }

        /**
         * Получить код режима устройства.
         *
         * @return код режима
         */
        public int getMode() {
            return mode;
        }

        /**
         * Получить код подрежима устройства.
         *
         * @return код подрежима
         */
        public int getSubMode() {
            return subMode;
        }

        /**
         * Получить номер чека.
         *
         * @return номер чека
         */
        public int getBillNumber() {
            return billNumber;
        }

        /**
         * Получить номер смены.
         *
         * @return номер смены
         */
        public int getShiftNumber() {
            return shiftNumber;
        }

        /**
         * Получить состояние чека.
         * <ol start=0>
         *   <li value=0>чек закрыт</li>
         *   <li value=1>чек прихода</li>
         *   <li value=2>чек возврата прихода</li>
         *   <li value=4>чек расхода</li>
         *   <li value=5>чек возврата расхода</li>
         *   <li value=7>чек коррекции: приход</li>
         *   <li value=9>чек коррекции: расход</li>
         * </ol>
         *
         * @return код состояния чека
         */
        public byte getBillState() {
            return billState;
        }

        /**
         * Получить сумму чека.
         *
         * @return сумма чека
         */
        public int getBillSum() {
            return billSum;
        }

        public int getScale() {
            return scale;
        }

        /**
         * Получить тип порта, по которому подключено устройство.
         * <ol>
         * <li value=1>RS-232</li>
         * <li value=4>USB</li>
         * <li value=5>Bluetooth</li>
         * <li value=6>Ethernet</li>
         * <li value=7>WiFi</li>
         * </ol>
         *
         * @return код типа порта
         */
        public byte getPortType() {
            return portType;
        }

        /**
         * Определить статус фискализации устройства.
         *
         * @return true - устройство фискализировано, false - устройство не
         * фискализировано
         */
        public boolean isFiscalized() {
            return fiscalized;
        }

        /**
         * Определить состояние смены.
         *
         * @return true - смена открыта, false - смена закрыта
         */
        public boolean isShiftOpen() {
            return shiftOpen;
        }

        /**
         * Определить состояние денежного ящика.
         *
         * @return true - денежный ящик открыт, false - денежный ящик закрыт
         */
        public boolean isCashBoxOpen() {
            return cashBoxOpen;
        }

        /**
         * Определить наличие бумаги в принтере.
         *
         * @return true - бумага в наличие, false - нет бумаги
         */
        public boolean hasPaper() {
            return hasPaper;
        }

        /**
         * Определить состояние сервисной крышки устройства.
         *
         * @return true - крышка открыта, false - крышка закрыта
         */
        public boolean isHoodOpen() {
            return hoodOpen;
        }

        /**
         * Определить статус фискального накопителя.
         *
         * @return true - фискальный накопитель активен, false - фискальный
         * накопитель не активен
         */
        public boolean isFiscalCounterActive() {
            return fiscalCounterActive;
        }

        /**
         * Определить состояние батареи устройства.
         *
         * @return true - батарея в порядке, false - батарея неисправна или
         * отсутствует
         */
        public boolean isBatteryOk() {
            return batteryOk;
        }
        
    }
    
}
