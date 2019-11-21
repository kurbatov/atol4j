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
import com.github.kurbatov.atol4j.command.CommandBuilder;
import com.github.kurbatov.atol4j.command.Result;

/**
 * Цепочка команд, которая позволяет добавлять команды режима регистрации.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class RegistrationCommandBuilder implements LastRegistrationCommandBuilder {
    
    private final CommandBuilder parent;

    public RegistrationCommandBuilder(CommandBuilder parent) {
        this.parent = parent;
    }
    
    /**
     * Открыть смену.
     *
     * @param test выполнить команду в тестовом режиме
     * @return объект для формирования цепочки команд
     */
    public RegistrationCommandBuilder openShift(boolean test) {
        return append(new OpenShiftCommand(test));
    }
    
    /**
     * Открыть смену.
     *
     * @return объект для формирования цепочки команд
     */
    public RegistrationCommandBuilder openShift() {
        return openShift(false);
    }
    
    /**
     * Выполнить открытие смены в тестовом режиме. 
     *
     * @return объект для формирования цепочки команд
     */
    public RegistrationCommandBuilder openShiftTest() {
        return openShift(true);
    }
    
    /**
     * Открыть чек.
     *
     * @param type тип чека
     * @param print печатать чек
     * @param test выполнить команду в тестовом режиме
     * @return объект для формирования цепочки команд
     */
    public RegistrationCommandBuilder openBill(byte type, boolean print, boolean test) {
        return append(new OpenBillCommand(type, print, test));
    }
    
    /**
     * Открыть чек.
     *
     * @param type тип чека
     * @return объект для формирования цепочки команд
     */
    public RegistrationCommandBuilder openBill(byte type) {
        return openBill(type, true, false);
    }
    
    /**
     * Открыть чек прихода.
     *
     * @return объект для формирования цепочки команд
     */
    public RegistrationCommandBuilder openBillIncome() {
        return openBill(OpenBillCommand.INCOME);
    }
    
    /**
     * Открыть чек возврата прихода.
     *
     * @return объект для формирования цепочки команд
     */
    public RegistrationCommandBuilder openBillRefund() {
        return openBill(OpenBillCommand.INCOME_REFUND);
    }
    
    /**
     * Аннулировать открытый чек.
     *
     * @return объект для формирования цепочки команд 
     */
    @Override
    public RegistrationCommandBuilder cancelBill() {
        return append(CancelBillCommand.INSTANCE);
    }
    
    /**
     * Зарегестрировать позициию в чеке.
     *
     * @param name наименование
     * @param price цена
     * @param count количество (три знака после запятой)
     * @param discount true - скидка, false - надбавка
     * @param discountAmount размер скидки или надбавки
     * @param tax тип налога
     * @param section секция, в которой проходит регистрация
     * @param test выполнить команду в тестовом режиме
     * @return объект для формирования цепочки команд
     */
    public RegistrationCommandBuilder registerItem(String name, long price, long count, boolean discount, long discountAmount, byte tax, byte section, boolean test) {
        return append(new ItemRegistrationCommand(name, price, count, discount, discountAmount, tax, section, test));
    }
    
    /**
     * Зарегестрировать позициию в чеке.
     *
     * @param name наименование
     * @param price цена
     * @param count количество (три знака после запятой)
     * @return объект для формирования цепочки команд
     */
    public RegistrationCommandBuilder registerItem(String name, long price, long count) {
        return append(new ItemRegistrationCommand(name, price, count));
    }
    
    public LastRegistrationCommandBuilder discount(long sum, boolean test) {
        return append(new DiscountCommand(sum, test));
    }
    
    public LastRegistrationCommandBuilder discount(long sum) {
        return discount(sum, false);
    }
    
    @Override
    public RegistrationCommandBuilder closeBill(long sum, byte paymentType, boolean test) {
        return append(new CloseBillCommand(sum, paymentType, test));
    }

    @Override
    public CommandBuilder resetMode() {
        return parent.resetMode();
    }

    @Override
    public RegistrationCommandBuilder append(Command<? extends Result> command) {
        parent.append(command);
        return this;
    }
    
}
