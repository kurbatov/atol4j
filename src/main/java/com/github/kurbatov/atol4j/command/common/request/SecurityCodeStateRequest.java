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
 * Запрос состояния активации кода защиты.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class SecurityCodeStateRequest implements Command<SecurityCodeStateRequest.Response>{
    
    private static final byte COMMAND = 0x74;
    
    private final byte codeNumber;

    /**
     * Создаёт запрос состояния активации указанного кода защиты.
     *
     * @param codeNumber номер кода защиты
     */
    public SecurityCodeStateRequest(byte codeNumber) {
        this.codeNumber = codeNumber;
    }
    
    @Override
    public CompletableFuture<Response> executeOn(CashRegister device) {
        return device.execute(COMMAND, codeNumber).thenApply(r -> new Response(r));
    }
    
    /**
     * Статус активации кода защиты.
     */
    public static class Response extends Result {

        private final boolean active;
        
        public Response(byte[] buf) {
            if (buf[0] == 0x4C) {
                active = buf[1] == 1;
            } else {
                active = false;
                setErrorCode(buf[1]);
                if (buf.length > 2) {
                    setErrorExt(buf[2]);
                }
            }
        }

        public boolean isActive() {
            return active;
        }
        
    }
    
}
