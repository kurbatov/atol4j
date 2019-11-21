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
import java.util.concurrent.CompletableFuture;

/**
 * Запрашивает состояние смены.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class ShiftStateRequest implements Command<ShiftStateRequest.Response> {

    /**
     * Экземпляр запроса состояния смены.
     */
    public static final ShiftStateRequest INSTANCE = new ShiftStateRequest();
    
    private static final byte[] COMMAND = {(byte) 0xA4, 0x10};

    @Override
    public CompletableFuture<Response> executeOn(CashRegister device) {
        return device.execute(COMMAND).thenApply(r -> new Response(r));
    }

    public static class Response extends Result {

        private final boolean closed;
        
        private int shiftNumber;
        
        private int billNumber;
        
        private Response(byte[] r) {
            super(r);
            closed = r[2] == 0;
            if (r.length > 3) {
                shiftNumber = (int) decode(r[4], r[3]);
                billNumber = (int) decode(r[6], r[5]);
            }
        }

        public boolean isClosed() {
            return closed;
        }

        public int getShiftNumber() {
            return shiftNumber;
        }

        public int getTicketNumber() {
            return billNumber;
        }

    }

}
