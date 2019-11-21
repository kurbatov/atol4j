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
package com.github.kurbatov.atol4j.command.registration;

import com.github.kurbatov.atol4j.command.Command;
import com.github.kurbatov.atol4j.command.Result;
import com.github.kurbatov.atol4j.command.common.CommonCommandBuilder;

/**
 * Цепочка команд в режиме регистрации, которая позволяет добавить только
 * финализирующие команды.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public interface LastRegistrationCommandBuilder extends CommonCommandBuilder<LastRegistrationCommandBuilder> {
    
    /**
     * Аннулировать открытый чек.
     *
     * @return объект для формирования цепочки команд
     */
    RegistrationCommandBuilder cancelBill();
    
    /**
     * Закрыть чек со сдачей.
     *
     * @param sum сумма в минимальных денежных единицах
     * @param paymentType тип оплаты
     * @param test выполнить команду в тестовом режиме
     * @return объект для формирования цепочки команд
     */
    RegistrationCommandBuilder closeBill(long sum, byte paymentType, boolean test);
    
    /**
     * Закрыть чек со сдачей (оплата наличными).
     *
     * @param sum сумма наличными в минимальных денежных единицах
     * @return объект для формирования цепочки команд
     */
    default RegistrationCommandBuilder closeBillCash(long sum) {
        return closeBill(sum, CloseBillCommand.CASH, false);
    }
    
    /**
     * Закрыть чек с безналичной оплатой.
     *
     * @param sum сумма в минимальных денежных единицах
     * @return объект для формирования цепочки команд
     */
    default RegistrationCommandBuilder closeBillCashless(long sum) {
        return closeBill(sum, CloseBillCommand.CASHLESS, false);
    }
    
    /**
     * Зарегистрировать налог на весь чек.
     *
     * @param sum сумма в минимальных денежных единицах
     * @param type тип налога от 1 до 6 (индекс ставки налога)
     * @param test выполнить команду в тестовом режиме
     * @return объект для формирования цепочки команд
     */
    default LastRegistrationCommandBuilder registerTaxes(long sum, byte type, boolean test) {
        return append(new TaxRegistrationCommand(sum, type, test));
    }
    
    /**
     * Рассчёт по чеку.
     * 
     * @param sum сумма в минимальных денежных единицах
     * @param paymentType тип оплаты
     * @param test выполнить команду в тестовом режиме
     * @return объект для формирования цепочки команд
     */
    default LastRegistrationCommandBuilder remitBill(long sum, byte paymentType, boolean test) {
        return append(new RemittanceCommand(sum, paymentType, test));
    }
    
    @Override
    public LastRegistrationCommandBuilder append(Command<? extends Result> command);

}
