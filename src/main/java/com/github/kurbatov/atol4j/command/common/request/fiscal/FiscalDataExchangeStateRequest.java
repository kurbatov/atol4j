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
package com.github.kurbatov.atol4j.command.common.request.fiscal;

import com.github.kurbatov.atol4j.CashRegister;
import com.github.kurbatov.atol4j.command.Command;
import com.github.kurbatov.atol4j.command.Result;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Запрашивает состояние обмена данными с оператором фискальных данных (ОФД).
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class FiscalDataExchangeStateRequest implements Command<FiscalDataExchangeStateRequest.Response> {

    /**
     * Экземпляр запроса состояния обмена данными с оператором фискальных данных (ОФД).
     */
    public static final FiscalDataExchangeStateRequest INSTANCE = new FiscalDataExchangeStateRequest();
    
    private static final byte[] COMMAND = {(byte) 0xA4, 0x20};

    @Override
    public CompletableFuture<Response> executeOn(CashRegister device) {
        return device.execute(COMMAND).thenApply(r -> new Response(r));
    }

    public static class Response extends Result {

        private boolean connected;
        
        private boolean hasUnsentMessages;

        private boolean waitingForResponse;

        private boolean hasCommandFromFDO;
        
        private boolean connectionPropertiesChanged;
        
        private boolean waitingForCommandRespose;

        private boolean readingMessage;
        
        private long unsentMessages;
        
        private long firstUnsentMessageNumber;

        private LocalDateTime firstUnsentMessageTime;

        private Response(byte[] r) {
            super(r);
            if (r.length >= 15) {
                byte state = r[2];
                connected = (state & 1) > 0;
                hasUnsentMessages = (state & 2) > 0;
                waitingForResponse = (state & 4) > 0;
                hasCommandFromFDO = (state & 8) > 0;
                connectionPropertiesChanged = (state & 16) > 0;
                waitingForCommandRespose = (state & 32) > 0;
                readingMessage = r[3] > 0;
                unsentMessages = r[4] | r[5] << 8;
                firstUnsentMessageNumber = (long) (r[6] | r[7] << 8 | r[8] << 16 | r[9] << 24);
                firstUnsentMessageTime = LocalDateTime.of(r[10] + 2000, r[11], r[12], r[13], r[14]);
            }
        }

        /**
         * Установлено ли транспортное соединение с ОФД
         *
         * @return true - установлено, false - не установлено
         */
        public boolean isConnected() {
            return connected;
        }

        /**
         * Есть ли сообщения для передачи ОФД
         *
         * @return true - есть сообщения для передачи, false - нет сообщений для
         * передачи
         */
        public boolean hasUnsentMessages() {
            return hasUnsentMessages;
        }

        /**
         * Ожидается ли ответное сообщение от ОФД.
         *
         * @return true - ответное сообщение ожидается, false - ответное
         * сообщение уже получено
         */
        public boolean isWaitingForResponse() {
            return waitingForResponse;
        }

        /**
         * Есть ли команда от ОФД.
         *
         * @return true - есть команда от ОФД, false - нет команды от ОФД
         */
        public boolean hasCommandFromFDO() {
            return hasCommandFromFDO;
        }

        /**
         * Изменились ли настройки соединения с ОФД.
         *
         * @return true - настройки соединения изменились,
         * false - настройки соединения не изменились
         */
        public boolean isConnectionPropertiesChanged() {
            return connectionPropertiesChanged;
        }

        /**
         * Ожидается ли ответ на команду от ОФД.
         *
         * @return true - ожидается ответ на команду от ОФД, false - ответ на
         * команду от ОФД получен
         */
        public boolean isWaitingForCommandRespose() {
            return waitingForCommandRespose;
        }

        /**
         * Состояние чтения сообщения для ОФД.
         *
         * @return true - производится чтение сообщения для ОФД, false - чтение
         * сообщения для ОФД не производится
         */
        public boolean isReadingMessage() {
            return readingMessage;
        }

        /**
         * Количество неотправленных документов ОФД.
         *
         * @return количество сообщений для передачи ОФД
         */
        public long getUnsentMessages() {
            return unsentMessages;
        }

        /**
         * Номер первого в очереди документа для ОФД.
         *
         * Если возвращается 0, то в очереди для передачи ОФД нет документа.
         * Если документ уже передан, то это номер документа, ожидающего
         * ответного сообщения ОФД (квитанцию).
         *
         * @return номер документа для передачи ОФД
         */
        public long getFirstUnsentMessageNumber() {
            return firstUnsentMessageNumber;
        }

        /**
         * Дата-время первого в очереди документа для ОФД.
         *
         * @return дата-время первого в очереди документа для ОФД
         */
        public LocalDateTime getFirstUnsentMessageTime() {
            return firstUnsentMessageTime;
        }

    }

}
