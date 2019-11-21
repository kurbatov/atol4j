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
package com.github.kurbatov.atol4j.command.report;

import com.github.kurbatov.atol4j.command.Command;
import com.github.kurbatov.atol4j.command.CommandBuilder;
import com.github.kurbatov.atol4j.command.CommandExecutor;
import com.github.kurbatov.atol4j.command.Result;
import com.github.kurbatov.atol4j.command.Wait;
import com.github.kurbatov.atol4j.command.common.CommonCommandBuilder;
import com.github.kurbatov.atol4j.command.common.SetModeCommand;
import com.github.kurbatov.atol4j.command.report.hard.CloseShiftWithReportCommand;
import com.github.kurbatov.atol4j.command.report.hard.CounterResetCommand;
import com.github.kurbatov.atol4j.command.report.hard.ShutdownCommand;
import com.github.kurbatov.atol4j.command.report.soft.PrintBillFooterCommand;
import com.github.kurbatov.atol4j.command.report.soft.PrintReportCommand;

/**
 * Добавляет в цепочку команды режима отчётов.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class ReportCommandBuilder implements CommonCommandBuilder<CommandBuilder> {

    private final CommandBuilder parent;
    private final byte[] password;

    public ReportCommandBuilder(CommandBuilder parent, byte[] password) {
        this.parent = parent;
        this.password = password;
    }
    
    /**
     * Снять суточный отчёт с гашением (закрыть смену).
     *
     * @return объект для формирования цепочки команд
     */
    public CommandBuilder closeShiftWithReport() {
        return parent.setMode(SetModeCommand.REPORT_WITH_CANCEL, password)
                .append(CloseShiftWithReportCommand.INSTANCE)
                .append(new Wait(8000))
                .resetMode();
    }
    
    /**
     * Выполнить общее гашение.
     *
     * @return объект для формирования цепочки команд
     * @see CounterResetCommand
     */
    public CommandBuilder resetCounters() {
        return parent.setMode(SetModeCommand.REPORT_WITH_CANCEL, password)
                .append(CounterResetCommand.INSTANCE)
                .resetMode();
    }
    
    /**
     * Выключить устройств.
     *
     * @return объект для формирования цепочки команд
     */
    public CommandExecutor shutdown() {
        return parent.setMode(SetModeCommand.REPORT_WITH_CANCEL, password)
                .append(ShutdownCommand.INSTANCE);
    }
    
    /**
     * Печатать отчёт без гашения.
     *
     * @param reportType тип отчёта
     * @return объект для формирования цепочки команд
     */
    public CommandBuilder printReport(byte reportType) {
        return parent.setMode(SetModeCommand.REPORT_WOT_CANCEL, password)
                .append(new PrintReportCommand(reportType))
                .resetMode();
    }
    
    /**
     * Напечатать нижнюю часть чека.
     *
     * @return объект для формирования цепочки команд
     */
    public CommandBuilder printBillFooter() {
        return parent.setMode(SetModeCommand.REPORT_WOT_CANCEL, password)
                .append(PrintBillFooterCommand.INSTANCE)
                .resetMode();
    }
    
    @Override
    public CommandBuilder resetMode() {
        return parent;
    }

    @Override
    public CommandBuilder append(Command<? extends Result> command) {
        return parent.append(command);
    }
    
}
