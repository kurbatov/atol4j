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
package com.github.kurbatov.atol4j.command.common.print;

import com.github.kurbatov.atol4j.charset.ExternalCharset;
import com.github.kurbatov.atol4j.command.BasicCommand;

/**
 * Печатает строку символов на кассовой ленте.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class PrintStringCommand extends BasicCommand {

    /**
     * Правила переноса текста
     */

    /**
     * Нет переноса
     */
    static final byte CARRYING_NO = 0x36;
    /**
     * По словам
     */
    static final byte CARRYING_BY_WORDS = 0x37;
    /**
     * По строке
     */
    static final byte CARRYING_BY_STRINGS = 0x38;
    
    private static final byte COMMAND = 0x4C;

    /**
     * Создаёт команду печати строки на кассовой ленте.
     *
     * @param string строка для печати на кассовой ленте
     */
    public PrintStringCommand(String string) {
        super(wrap(string));
    }
    
    private static byte[] wrap(String string) {
        byte[] payload = string.getBytes(ExternalCharset.INSTANCE);
        byte[] message = new byte[payload.length + 1];
        message[0] = COMMAND;
        System.arraycopy(payload, 0, message, 1, payload.length);
        return message;
    }
    
}
