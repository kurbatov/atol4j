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
import com.github.kurbatov.atol4j.command.common.SetModeCommand;
import com.github.kurbatov.atol4j.command.registration.RegistrationCommandBuilder;
import com.github.kurbatov.atol4j.command.report.ReportCommandBuilder;
import com.github.kurbatov.atol4j.command.select.SelectCommandBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Цепочка команд, позволяющая добавлять дополнительные команды и выполнить уже
 * добавленные.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class CommandBuilder implements SelectCommandBuilder, CommandExecutor {
    
    private final CashRegister device;
    private final List<Command<? extends Result>> commands;

    /**
     * Создаёт цепочку команд для заданного устройства.
     *
     * @param device устройство, которое должно выполнить команды
     */
    public CommandBuilder(CashRegister device) {
        this.device = device;
        this.commands = new ArrayList<>();
    }
    
    @Override
    public RegistrationCommandBuilder registration(byte... password) {
        setMode(SetModeCommand.REGISTRATION, password);
        return new RegistrationCommandBuilder(this);
    }
    
    @Override
    public ReportCommandBuilder report(byte... password) {
        return new ReportCommandBuilder(this, password);
    }
    
    @Override
    public CompletableFuture<Result> execute() {
        CompletableFuture<Result> result = CompletableFuture.completedFuture(new Result());
        for (Command<? extends Result> c : commands) {
            result = result.thenCompose(r -> r.hasError() ? CompletableFuture.completedFuture(r) : c.executeOn(device).thenApply(Function.identity()));
        }
        return result.thenCompose(r -> {
            if (r.hasError()) {
                return device.toInitialState().thenApply(a -> r);
            } else {
                return CompletableFuture.completedFuture(r);
            }
        });
    }

    @Override
    public CommandBuilder append(Command<? extends Result> command) {
        commands.add(command);
        return this;
    }

}
