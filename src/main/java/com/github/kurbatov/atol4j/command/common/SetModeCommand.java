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
package com.github.kurbatov.atol4j.command.common;

import com.github.kurbatov.atol4j.command.BasicCommand;

/**
 * Команда входа в режим.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class SetModeCommand extends BasicCommand {

    /**
     * Режим регистрации
     */
    public static final byte REGISTRATION = 0x01;

    /**
     * Режим отчетов без гашения
     */
    public static final byte REPORT_WOT_CANCEL = 0x02;

    /**
     * Режим отчетов c гашением
     */
    public static final byte REPORT_WITH_CANCEL = 0x03;

    /**
     * Режим программирования
     */
    public static final byte CONFIGURATION = 0x04;

    /**
     * Режим доступа к ФП
     */
    public static final byte FISCAL_MEMORY_ACCESS = 0x05;

    /**
     * Режим доступа к ЭКЛЗ
     */
    public static final byte SECURE_FISCAL_MEMORY_ACCESS = 0x06;

    private static final byte COMMAND = 0x56;

    /**
     * Создаёт команду входа в указанный режим.
     *
     * @param mode режим
     * @param password пароль для входа в режим
     */
    public SetModeCommand(byte mode, byte[] password) {
        super(wrap(mode, password));
    }

    private static byte[] wrap(byte mode, byte[] password) {
        if (password == null) {
            password = new byte[0];
        }
        if (password.length != 4) {
            byte[] tmp = {0, 0, 0, 0};
            System.arraycopy(password, 0, tmp, 4 - password.length, password.length);
            password = tmp;
        }
        return new byte[]{COMMAND, mode, password[0], password[1], password[2], password[3]};
    }

}
