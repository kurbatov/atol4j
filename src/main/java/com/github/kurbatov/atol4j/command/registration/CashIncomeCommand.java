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

import com.github.kurbatov.atol4j.command.BasicCommand;
import com.github.kurbatov.atol4j.command.Command;

/**
 * Регестрирует внесение денежных средств.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class CashIncomeCommand extends BasicCommand {

    private static final byte COMMAND = 0x49;

    /**
     * Создаёт команду регистрации внесения денежных средств.
     *
     * @param sum сумма в минимальных денежных единицах
     * @param test выполнить команду в тестовом режиме
     */
    public CashIncomeCommand(long sum, boolean test) {
        super(wrap(sum, test));
    }
    
    private static byte[] wrap(long sum, boolean test) {
        byte[] s = Command.encode(sum, 5);
        return new byte[]{COMMAND, (byte) (test ? 1 : 0), s[0], s[1], s[2], s[3], s[4]};
    }
    
}
