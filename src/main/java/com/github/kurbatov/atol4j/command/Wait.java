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
 * Эта команда позволяет добавить задержку в цепочку выполнения команд.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class Wait implements Command<Result> {

    private final long millis;

    /**
     * Создаёт команду задержки отправки следующей команды в цепочке команд.
     *
     * @param millis длительность задержки в миллисекундах
     */
    public Wait(long millis) {
        this.millis = millis;
    }
    
    @Override
    public CompletableFuture<Result> executeOn(CashRegister device) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return new Result();
        });
    }
    
}
