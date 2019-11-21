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
 * Регистрация налога на весь чек.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class TaxRegistrationCommand extends BasicCommand {

    private static final byte COMMAND = (byte) 0xB8;

    /**
     * Создаёт команду регистрации налога на весь чек.
     *
     * @param sum сумма в минимальных денежных единицах
     * @param type тип налога от 1 до 6 (индекс ставки налога)
     * @param test выполнить команду в тестовом режиме
     */
    public TaxRegistrationCommand(long sum, byte type, boolean test) {
        super(wrap(sum, type, test));
    }

    private static byte[] wrap(long sum, byte type, boolean test) {
        byte[] s = Command.encode(sum, 7);
        return new byte[]{COMMAND, (byte) (test ? 1 : 0), 0, type, s[0], s[1], s[2], s[3], s[4], s[5], s[6]};
    }
    
}
