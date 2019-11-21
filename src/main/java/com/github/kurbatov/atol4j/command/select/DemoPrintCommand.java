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
 * Команда демонстрационной печати.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class DemoPrintCommand extends BasicCommand {

    /**
     * Демонстрационная печать
     */
    public static final byte DEMO = 0;
    
    /**
     * Информация о ККТ
     */
    public static final byte INFO = 1;
    
    /**
     * Демонстрационная печать
     */
    public static final byte DEMO_ALT = 2;
    
    /**
     * Печать диагностики соединения с ОФД
     */
    public static final byte DIAGNOSTICS = 6;
    
    private static final byte COMMAND = (byte) 0x82;

    /**
     * Создаёт команду печати демонстрационного чека.
     *
     * @param type тип чека
     * @param test выполнить команду в тестовом режиме
     */
    public DemoPrintCommand(byte type, boolean test) {
        super(new byte[]{COMMAND, (byte) (test ? 0 : 1), type, 0});
    }
    
}