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

/**
 * Подаёт звуковой сигнал заданной частоты и длительности.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class BeepCommand extends BasicCommand {

    private static final byte COMMAND = (byte) 0x88;

    /**
     * Создаёт команду подачи звукового сигнала заданного тона и
     * продолжительности.
     *
     * @param tone частота звука от 100 до 2500 Гц
     * @param duration продолжительность сигнала в десятках мллисекунд
     * @throws IllegalArgumentException если указана частота звука менее 100 или
     * более 2500 Гц
     */
    public BeepCommand(int tone, int duration) {
        super(generatePayload(tone, duration));
    }

    private static byte[] generatePayload(int tone, int duration) {
        if (tone < 100 || tone > 2500) {
            throw new IllegalArgumentException("Частота звука должна быть от 100 до 2500 Гц. Получено " + tone);
        }
        int divider = 65536 - (921600 / tone);
        return new byte[]{COMMAND, (byte) (divider >>> 8), (byte) (0xFF & divider), (byte) duration};
    }

}
