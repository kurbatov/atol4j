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
 * Запрос наличных.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class CashRequest implements Command<CashRequest.Response>{

    /**
     * Экземпляр запроса наличных.
     */
    public static final CashRequest INSTANCE = new CashRequest();
    
    private static final byte COMMAND = 0x4D;
    
    @Override
    public CompletableFuture<Response> executeOn(CashRegister device) {
        return device.execute(new byte[] {COMMAND}).thenApply(r -> new Response(r));
    }
    
    /**
     * Ответ на запрос наличных в кассе.
     */
    public static class Response extends Result {

        private final long sum;
        
        public Response(byte[] buf) {
            super(buf[0], buf[0] == COMMAND ? 0 : buf[1], (byte) 0);
            if (hasError()) {
                sum = -1;
            } else {
                sum = decode(Arrays.copyOfRange(buf, 1, buf.length));
            }
        }

        /**
         * Получить сумму наличных в кассе.
         *
         * @return сумма наличных в кассе
         */
        public long getSum() {
            return sum;
        }
        
    }
    
}
