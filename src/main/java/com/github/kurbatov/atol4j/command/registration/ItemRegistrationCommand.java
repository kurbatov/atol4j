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

import com.github.kurbatov.atol4j.charset.ExternalCharset;
import com.github.kurbatov.atol4j.command.BasicCommand;
import com.github.kurbatov.atol4j.command.Command;
import java.util.Arrays;

/**
 * Регистрация позиции.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class ItemRegistrationCommand extends BasicCommand {

    private static final byte COMMAND = (byte) 0xE6;

    /**
     * Создаёт команду регистрации позиции.
     *
     * @param name наименование
     * @param price цена
     * @param count количество (три знака после запятой)
     * @param discount true - скидка, false - надбавка
     * @param discountAmount размер скидки или надбавки
     * @param tax тип налога
     * @param section секция, в которой проходит регистрация
     * @param test выполнить команду в тестовом режиме
     */
    public ItemRegistrationCommand(String name, long price, long count, boolean discount, long discountAmount, byte tax, byte section, boolean test) {
        super(wrap(name, price, count, discount, discountAmount, tax, section, test));
    }
    
    /**
     * Создаёт команду регистрации позиции.
     *
     * @param name наименование
     * @param price цена
     * @param count количество (три знака после запятой)
     */
    public ItemRegistrationCommand(String name, long price, long count) {
        this(name, price, count, false, (byte) 0, (byte) 0, (byte) 0, false);
    }

    private static byte[] wrap(String name, long price, long count, boolean discount, long discountAmount, byte tax, byte section, boolean test) {
        byte[] nameBytes = ExternalCharset.INSTANCE.encode(name).array();
        if (nameBytes.length < 64) {
            byte[] tmp = new byte[64];
            Arrays.fill(tmp, 64 - nameBytes.length, 64, (byte) 0);
            System.arraycopy(nameBytes, 0, tmp, 0, nameBytes.length);
            nameBytes = tmp;
        }
        byte[] p = Command.encode(price, 6);
        byte[] c = Command.encode(count, 5);
        byte[] da = Command.encode(discountAmount, 6);
        byte[] message = new byte[104];
        message[0] = COMMAND;
        message[1] = (byte) ((test ? 1 : 0) | 2);
        System.arraycopy(nameBytes, 0, message, 2, 64);
        System.arraycopy(p, 0, message, 66, p.length);
        System.arraycopy(c, 0, message, 72, c.length);
        message[77] = 1;
        message[78] = (byte) (discount ? 0 : 1);
        System.arraycopy(da, 0, message, 79, da.length);
        message[85] = tax;
        message[86] = section;
        Arrays.fill(message, 86, message.length, (byte) 0);
        return message;
    }
    
}
