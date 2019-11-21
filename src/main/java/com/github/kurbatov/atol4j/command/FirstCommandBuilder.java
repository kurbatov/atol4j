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
package com.github.kurbatov.atol4j.command;

import com.github.kurbatov.atol4j.CashRegister;
import com.github.kurbatov.atol4j.command.registration.CancelBillCommand;
import com.github.kurbatov.atol4j.command.registration.RegistrationCommandBuilder;
import com.github.kurbatov.atol4j.command.report.ReportCommandBuilder;
import com.github.kurbatov.atol4j.command.select.SelectCommandBuilder;

/**
 * Пустая цепочка команд.
 * 
 * Добавляет в цепочку команд первую команду или сразу исполняет команду,
 * которую нельзя поставить в цепочку.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class FirstCommandBuilder implements SelectCommandBuilder {
    
    private final CashRegister device;

    public FirstCommandBuilder(CashRegister device) {
        this.device = device;
    }
    
    /**
     * Аннулировать открытый чек.
     *
     * @return результат выполнения команды
     */
    public CommandBuilder cancelBill() {
        return append(CancelBillCommand.INSTANCE);
    }

    @Override
    public CommandBuilder reboot() {
        return (CommandBuilder) SelectCommandBuilder.super.reboot();
    }

    @Override
    public RegistrationCommandBuilder registration(byte... password) {
        return new CommandBuilder(device).registration(password);
    }
    
    @Override
    public ReportCommandBuilder report(byte... password) {
        return new CommandBuilder(device).report(password);
    }

    @Override
    public CommandBuilder append(Command<? extends Result> command) {
        return new CommandBuilder(device).append(command);
    }
    
}
