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
package com.github.kurbatov.atol4j.command.common.controll;

import com.github.kurbatov.atol4j.command.BasicCommand;
import com.github.kurbatov.atol4j.command.Command;

/**
 * Импульсное открытие денежного ящика.
 * 
 * Формирует импульсы на выходе денежного ящика по указанным в команде
 * настройкам (не поддерживается в ККТ АТОЛ 15Ф, АТОЛ 30Ф, АТОЛ 60Ф, АТОЛ 90Ф,
 * АТОЛ 91Ф, АТОЛ 92Ф, АТОЛ 42ФС).
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class OpenCashboxImpulseCommand extends BasicCommand {

    private static final byte COMMAND = (byte) 0x85;

    /**
     * Создаёт команду импульсного открытия денежного ящика.
     *
     * @param pulse время подачи напряжения на ящик х10 мс
     * @param delay время задержки перед повтором х10 мс
     * @param repeat количество повторов от 0 до 99
     */
    public OpenCashboxImpulseCommand(int pulse, int delay, int repeat) {
        super(wrap(pulse, delay, repeat));
    }

    private static byte[] wrap(int pulse, int delay, int repeat) {
        if (repeat < 0 || repeat > 99) {
            throw new IllegalArgumentException("Количество повторов должно быть неотрицательным, но не более 99.");
        }
        byte[] pulseBinary = Command.encode(pulse, 2);
        byte[] delayBinary = Command.encode(delay, 2);
        byte[] result = new byte[6];
        result[0] = COMMAND;
        result[1] = pulseBinary[0];
        result[2] = pulseBinary[1];
        result[3] = delayBinary[0];
        result[4] = delayBinary[1];
        result[5] = (byte) repeat;
        return result;
    }

}
