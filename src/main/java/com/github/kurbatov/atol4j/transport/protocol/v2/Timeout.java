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
package com.github.kurbatov.atol4j.transport.protocol.v2;

/**
 * Содержит определения величины задержек в миллисекундах.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public interface Timeout {
    
    static final int T1 = 500;
    static final int T2 = 2000;
    static final int T3 = 500;
    static final int T4 = 500;
    static final int T5 = 10000;
    static final int T6 = 500;
    static final int T7 = 500;
    static final int T8 = 1000;
    
    static int T5(byte command) {
        int result;
        switch(command) {
            case 0x6B: result = 10000; break;
            case (byte)0x8D:
            case (byte)0x8E:
            case (byte)0xA7:
            case 0x4A: result = 20000; break;
            case 0x5A: result = 40000; break;
            case (byte)0x91: result = 45000; break;
            case (byte)0xA6:
            case (byte)0xE6:
            case (byte)0xEA:
            case (byte)0xEB: result = 50000; break;
            case (byte)0xA8:
            case (byte)0xAB: result = 120000; break;
            default: result = T5;
        }
        return result;
    }
    
}
