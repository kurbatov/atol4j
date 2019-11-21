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

/**
 * Команда открытия чека.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class OpenBillCommand extends BasicCommand {

    /**
     * Чек прихода
     */
    public static final byte INCOME = 1;
    
    /**
     * Чек возврата прихода
     */
    public static final byte INCOME_REFUND = 2;
    
    /**
     * Чек расхода
     */
    public static final byte EXPENCE = 4;
    
    /**
     * Чек возврата расхода
     */
    public static final byte EXPENCE_REFUND = 5;
    
    /**
     * Чек коррекции прихода
     */
    public static final byte CORRECTION_INCOME = 7;
    
    /**
     * Чек коррекции расхода
     */
    public static final byte CORRECTION_EXPENCE = 9;
    
    /**
     * Чек коррекции возврата прихода
     */
    public static final byte CORRECTION_INCOME_REFUND = 10;
    
    private static final byte COMMAND = (byte) 0x92;

    /**
     * Создаёт команду открытия чека.
     *
     * @param type тип чека
     * @param print печатать чек
     * @param test выполнить команду в тестовом режиме
     */
    public OpenBillCommand(byte type, boolean print, boolean test) {
        super(new byte[]{COMMAND, (byte) ((test ? 1 : 0) | (print ? 0 : 2)), type});
    }
    
}
