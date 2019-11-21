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
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

/**
 * Запрос срока действия фискального накопителя.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class FiscalMemoryExpirationRequest implements Command<FiscalMemoryExpirationRequest.Response> {

    /**
     * Экземпляр запроса срока действия фискального накопителя.
     */
    public static final FiscalMemoryExpirationRequest INSTANCE = new FiscalMemoryExpirationRequest();
    
    private static final byte[] COMMAND = {(byte) 0xA4, 0x32};

    @Override
    public CompletableFuture<Response> executeOn(CashRegister device) {
        return device.execute(COMMAND).thenApply(r -> new Response(r));
    }

    public static class Response extends Result {

        private LocalDate expirationDate;
        
        private int registrationsRemains;
        
        private int registrationsMade;

        private Response(byte[] r) {
            super(r);
            if (r.length >= 7) {
                expirationDate = LocalDate.of(r[2] + 2000, r[3], r[4]);
                registrationsRemains = (int) r[5];
                registrationsMade = (int) r[6];
            }
        }

        /**
         * Дата окончания срока действия фискального накопителя.
         *
         * @return дата окончания срока действия ФН
         */
        public LocalDate getExpirationDate() {
            return expirationDate;
        }

        /**
         * Возвращает количество оставшихся перерегистраций ФН.
         *
         * @return количество оставшихся перерегистраций
         */
        public int getRegistrationsRemains() {
            return registrationsRemains;
        }

        /**
         * Возвращает количество выполненных регистраций/перерегистраций.
         *
         * @return количество выполненных регистраций/перерегистраций ФН
         */
        public int getRegistrationsMade() {
            return registrationsMade;
        }

    }

}
