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
package com.github.kurbatov.atol4j.command.select;

import com.github.kurbatov.atol4j.command.BasicCommand;

/**
 * Команда ввода кода защиты ККТ.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class EnterDefenceCodeCommand extends BasicCommand {

    private static final byte COMMAND = (byte) 0x6D;

    /**
     * Создаёт команду ввода кода защиты ККТ.
     *
     * @param number номер кода защиты
     * @param code код защиты
     */
    public EnterDefenceCodeCommand(byte number, byte[] code) {
        super(wrap(number, code));
    }
    
    private static byte[] wrap(byte number, byte[] code) {
        if (number < 0 || number > 30) {
            throw new IllegalArgumentException("Номер кода защиты должен быть от 0 до 30: " + number);
        }
        byte[] message = new byte[code.length + 2];
        message[0] = COMMAND;
        message[1] = number;
        System.arraycopy(code, 0, message, 2, code.length);
        return message;
    }

}
