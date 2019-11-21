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
package com.github.kurbatov.atol4j.charset;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Oleg Kurbatov &lt;o.v.kurbatov@gmail.com&gt;
 */
public class ExternalCharsetTest {
    
    @Test
    public void externalCharsetTest() {
        Charset charset = Charset.forName("AtolExternal");
        assertNotNull(charset, "Кодировка должна быть найдена по имени");
        assertTrue(charset.encode("").array().length == 0, "Пустая строка должна остаться пустой");
        byte[] result = charset.encode("0123456789").array();
        assertTrue(result.length == 10, "Длинна результата должна совпадать с длинной исходной строки");
        assertEquals(new byte[]{0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39}, result, "Строка должна быть закодирована правильно");
        assertEquals("0123456789", charset.decode(ByteBuffer.wrap(result)).toString(), "Обратная конвертация должна восстанавливать исходную строку");
        String atol91f = charset.decode(ByteBuffer.wrap(new byte[] {(byte) 0x80, (byte) 0x92, (byte) 0x8E, (byte) 0x8B, 0x20, 0x39, 0x31, (byte) 0x94})).toString();
        assertEquals("АТОЛ 91Ф", atol91f);
    }
    
}
