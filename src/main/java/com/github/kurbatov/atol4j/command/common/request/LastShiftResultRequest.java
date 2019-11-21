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
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * Запрос последнего сменного итога.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class LastShiftResultRequest implements Command<LastShiftResultRequest.Response>{

    /**
     * Экземпляр запроса последнего сменного итога.
     */
    public static final LastShiftResultRequest INSTANCE = new LastShiftResultRequest();
    
    private static final byte[] COMMAND = {0x58};
    
    @Override
    public CompletableFuture<Response> executeOn(CashRegister device) {
        return device.execute(COMMAND).thenApply(r -> new Response(r));
    }
    
    /**
     * Последний сменный итог.
     */
    public static class Response extends Result {

        private final long income;
        private final long outcome;
        
        public Response(byte[] buf) {
            super(buf[0], buf[1], (byte) 0);
            income = decode(Arrays.copyOfRange(buf, 2, 9));
            outcome = decode(Arrays.copyOfRange(buf, 9, 16));
        }

        /**
         * Получить сменный итог прихода.
         *
         * @return сменный итог прихода в минимальных денежных единицах
         */
        public long getIncome() {
            return income;
        }

        /**
         * Получить сменный итог расхода.
         *
         * @return сменный итог расхода в минимальных денежных единицах
         */
        public long getOutcome() {
            return outcome;
        }
        
    }
    
}
