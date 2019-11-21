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
package com.github.kurbatov.atol4j.command.common.date;

import com.github.kurbatov.atol4j.command.BasicCommand;
import static com.github.kurbatov.atol4j.command.Command.encode;

/**
 * Устанавливает дату и время на ККТ. Может быть выполнена только при закрытой
 * смене.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class SetDateTimeCommand extends BasicCommand {
    
    private static final byte COMMAND = (byte) 0xED;
    
    /**
     * Создаёт команду установки даты и времени на ККТ.
     *
     * @param day день
     * @param month месяц
     * @param year год (2-х значное число)
     * @param hour час
     * @param minute минута
     * @param second секунда
     */
    public SetDateTimeCommand(byte day, byte month, byte year, byte hour, byte minute, byte second) {
        super(
                COMMAND,
                encode(day, 1)[0],
                encode(month, 1)[0],
                encode(year, 1)[0],
                encode(hour, 1)[0],
                encode(minute, 1)[0],
                encode(second, 1)[0]
        );
    }
    
}
