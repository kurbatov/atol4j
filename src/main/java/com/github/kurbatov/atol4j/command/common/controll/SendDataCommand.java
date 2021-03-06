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
 * Передаёт информацию от ККТ к порту RS-232 по первому или второму каналу
 * обмена данными (не поддерживается в ККТ АТОЛ 15Ф, АТОЛ 30Ф, АТОЛ 20Ф,
 * АТОЛ 50Ф, АТОЛ 60Ф, АТОЛ 90Ф, АТОЛ 91Ф, АТОЛ 92Ф, АТОЛ 42ФС, АТОЛ 150Ф).
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class SendDataCommand extends BasicCommand {

    private static final byte COMMAND = (byte) 0x8F;

    /**
     * Создаёт команду передачи данных от ККТ на внешнее устройство (например,
     * дисплей покупателя).
     *
     * @param port порт подключения внешнего устройства
     * @param data данные для передачи
     * @throws IllegalArgumentException при попытке передачи пустого массива или
     * если объём передаваемых данных более 94 байт
     */
    public SendDataCommand(byte port, byte[] data) {
        super(wrap(port, data));
    }

    private static byte[] wrap(byte port, byte[] data) {
        if (data.length < 1 || data.length > 94) {
            throw new IllegalArgumentException("Длинна данных должна быть в диапазоне от 1 до 94 байт.");
        }
        byte[] result = new byte[data.length + 2];
        result[0] = COMMAND;
        result[1] = port;
        System.arraycopy(data, 0, result, 2, data.length);
        return result;
    }

}
