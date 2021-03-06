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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Содержит определения внешней кодовой страницы записи данных в ККТ (внешняя
 * кодировка ККТ).
 *
 * Используется в командах:
 * <ul>
 * <li>печать строки</li>
 * <li>печать поля</li>
 * <li>открыть смену</li>
 * <li>программирование таблицы</li>
 * <li>регистрация позиции</li>
 * <li>запись реквизита</li>
 * <li>чтение реквизита</li>
 * <li>комплексная команда формирования позиции: завершить формирование
 * позиции</li>
 * </ul>
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class ExternalCharsetDecoder extends CharsetDecoder {

    static final Map<Integer, Character> CODEPAGE = ExternalCharsetEncoder.CODEPAGE
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getValue(), e -> e.getKey()));

    public ExternalCharsetDecoder(Charset charset) {
        super(charset, 1, 1);
    }

    @Override
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        for (;;) {
            if (in.hasRemaining() && !out.hasRemaining()) {
                return CoderResult.OVERFLOW;
            }
            try {
                byte c = in.get();
                out.put(CODEPAGE.get(0xFF & c));
            } catch (BufferUnderflowException e) {
                return CoderResult.UNDERFLOW;
            }
        }
    }
}
