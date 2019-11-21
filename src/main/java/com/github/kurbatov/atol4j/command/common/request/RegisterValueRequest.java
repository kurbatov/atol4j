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
 * Запрос значения регистра (считать регистр).
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class RegisterValueRequest implements Command<RegisterValueRequest.Response>{
    
    private static final byte COMMAND = (byte) 0x91;
    
    private final byte register;
    
    private final byte[] params;

    public RegisterValueRequest(byte register, byte... params) {
        this.register = register;
        if (params.length == 0) {
            this.params = new byte[] {0, 0};
        } else if (params.length == 1) {
            this.params = new byte[] {params[0], 0};
        } else {
            this.params = params;
        }
    }
    
    @Override
    public CompletableFuture<Response> executeOn(CashRegister device) {
        return device.execute(COMMAND, register, params[0], params[1]).thenApply(r -> new Response(r));
    }
    
    /**
     * Значение регистра.
     */
    public static class Response extends Result {

        private final byte[] value;
        
        public Response(byte[] buf) {
            super(buf[0], buf[1], (byte) 0);
            value = Arrays.copyOfRange(buf, 2, buf.length);
        }

        /**
         * Значение регистра в бинарном представлении.
         *
         * @return значение регистра
         */
        public byte[] getValue() {
            return value;
        }
        
        /**
         * Получить интерпретацию бинарного значения регистра в виде дисятичного
         * числа.
         *
         * @return результат конвертации из бинарно-десяичного формата
         */
        public long interpretValueAsBCD() {
            return decode(value);
        }
        
    }
    
}
