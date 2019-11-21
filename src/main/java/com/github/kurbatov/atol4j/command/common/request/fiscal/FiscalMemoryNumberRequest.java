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
import com.github.kurbatov.atol4j.charset.ExternalCharset;
import com.github.kurbatov.atol4j.command.Command;
import com.github.kurbatov.atol4j.command.Result;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * Запрашивает номер фискального накопителя установленного в ККТ.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class FiscalMemoryNumberRequest implements Command<FiscalMemoryNumberRequest.Response> {

    /**
     * Экземпляр запроса номера фискального накопителя.
     */
    public static final FiscalMemoryNumberRequest INSTANCE = new FiscalMemoryNumberRequest();
    
    private static final byte[] COMMAND = {(byte) 0xA4, 0x31};

    @Override
    public CompletableFuture<Response> executeOn(CashRegister device) {
        return device.execute(COMMAND).thenApply(r -> new Response(r));
    }

    public static class Response extends Result {

        private String number;
        
        private Response(byte[] r) {
            super(r);
            if (r.length > 2) {
                byte[] data = Arrays.copyOfRange(r, 2, r.length);
                number = new String(data, ExternalCharset.INSTANCE);
            }
        }

        /**
         * Получить номер фискального накопителя.
         *
         * @return номер фискального накопителя
         */
        public String getNumber() {
            return number;
        }

    }

}
