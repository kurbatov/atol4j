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
import java.util.concurrent.CompletableFuture;

/**
 * Команда, которая возвращает стандартный результат.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class BasicCommand implements Command<Result> {

    private final byte[] command;

    public BasicCommand(byte... command) {
        this.command = command;
    }
    
    @Override
    public CompletableFuture<Result> executeOn(CashRegister device) {
        return device.execute(command).thenApply(r -> new Result(r));
    }
    
}