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
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.HashMap;
import java.util.Map;

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
public class ExternalCharsetEncoder extends CharsetEncoder {

    static final Map<Character, Integer> CODEPAGE = new HashMap<>();

    static {
        CODEPAGE.put(' ', 0x20);
        CODEPAGE.put('!', 0x21);
        CODEPAGE.put('"', 0x22);
        CODEPAGE.put('#', 0x23);
        CODEPAGE.put('№', 0x24);
        CODEPAGE.put('%', 0x25);
        CODEPAGE.put('&', 0x26);
        CODEPAGE.put('’', 0x27);
        CODEPAGE.put('(', 0x28);
        CODEPAGE.put(')', 0x29);
        CODEPAGE.put('*', 0x2A);
        CODEPAGE.put('+', 0x2B);
        CODEPAGE.put(',', 0x2C);
        CODEPAGE.put('-', 0x2D);
        CODEPAGE.put('.', 0x2E);
        CODEPAGE.put('/', 0x2F);

        CODEPAGE.put('0', 0x30);
        CODEPAGE.put('1', 0x31);
        CODEPAGE.put('2', 0x32);
        CODEPAGE.put('3', 0x33);
        CODEPAGE.put('4', 0x34);
        CODEPAGE.put('5', 0x35);
        CODEPAGE.put('6', 0x36);
        CODEPAGE.put('7', 0x37);
        CODEPAGE.put('8', 0x38);
        CODEPAGE.put('9', 0x39);
        CODEPAGE.put(':', 0x3A);
        CODEPAGE.put(';', 0x3B);
        CODEPAGE.put('<', 0x3C);
        CODEPAGE.put('=', 0x3D);
        CODEPAGE.put('>', 0x3E);
        CODEPAGE.put('?', 0x3F);

        CODEPAGE.put('@', 0x40);
        CODEPAGE.put('A', 0x41);
        CODEPAGE.put('B', 0x42);
        CODEPAGE.put('C', 0x43);
        CODEPAGE.put('D', 0x44);
        CODEPAGE.put('E', 0x45);
        CODEPAGE.put('F', 0x46);
        CODEPAGE.put('G', 0x47);
        CODEPAGE.put('H', 0x48);
        CODEPAGE.put('I', 0x49);
        CODEPAGE.put('J', 0x4A);
        CODEPAGE.put('K', 0x4B);
        CODEPAGE.put('L', 0x4C);
        CODEPAGE.put('M', 0x4D);
        CODEPAGE.put('N', 0x4E);
        CODEPAGE.put('O', 0x4F);

        CODEPAGE.put('P', 0x50);
        CODEPAGE.put('Q', 0x51);
        CODEPAGE.put('R', 0x52);
        CODEPAGE.put('S', 0x53);
        CODEPAGE.put('T', 0x54);
        CODEPAGE.put('U', 0x55);
        CODEPAGE.put('V', 0x56);
        CODEPAGE.put('W', 0x57);
        CODEPAGE.put('X', 0x58);
        CODEPAGE.put('Y', 0x59);
        CODEPAGE.put('Z', 0x5A);
        CODEPAGE.put('[', 0x5B);
        CODEPAGE.put('\\', 0x5C);
        CODEPAGE.put(']', 0x5D);
        CODEPAGE.put('^', 0x5E);
        CODEPAGE.put('_', 0x5F);

        CODEPAGE.put('`', 0x60);
        CODEPAGE.put('a', 0x61);
        CODEPAGE.put('b', 0x62);
        CODEPAGE.put('c', 0x63);
        CODEPAGE.put('d', 0x64);
        CODEPAGE.put('e', 0x65);
        CODEPAGE.put('f', 0x66);
        CODEPAGE.put('g', 0x67);
        CODEPAGE.put('h', 0x68);
        CODEPAGE.put('i', 0x69);
        CODEPAGE.put('j', 0x6A);
        CODEPAGE.put('k', 0x6B);
        CODEPAGE.put('l', 0x6C);
        CODEPAGE.put('m', 0x6D);
        CODEPAGE.put('n', 0x6E);
        CODEPAGE.put('o', 0x6F);

        CODEPAGE.put('p', 0x70);
        CODEPAGE.put('q', 0x71);
        CODEPAGE.put('r', 0x72);
        CODEPAGE.put('s', 0x73);
        CODEPAGE.put('t', 0x74);
        CODEPAGE.put('u', 0x75);
        CODEPAGE.put('v', 0x76);
        CODEPAGE.put('w', 0x77);
        CODEPAGE.put('x', 0x78);
        CODEPAGE.put('y', 0x79);
        CODEPAGE.put('z', 0x7A);
        CODEPAGE.put('{', 0x7B);
        CODEPAGE.put('|', 0x7C);
        CODEPAGE.put('}', 0x7D);
        CODEPAGE.put('~', 0x7E);
        //CODEPAGE.put('', 0x7F);

        CODEPAGE.put('А', 0x80);
        CODEPAGE.put('Б', 0x81);
        CODEPAGE.put('В', 0x82);
        CODEPAGE.put('Г', 0x83);
        CODEPAGE.put('Д', 0x84);
        CODEPAGE.put('Е', 0x85);
        CODEPAGE.put('Ж', 0x86);
        CODEPAGE.put('З', 0x87);
        CODEPAGE.put('И', 0x88);
        CODEPAGE.put('Й', 0x89);
        CODEPAGE.put('К', 0x8A);
        CODEPAGE.put('Л', 0x8B);
        CODEPAGE.put('М', 0x8C);
        CODEPAGE.put('Н', 0x8D);
        CODEPAGE.put('О', 0x8E);
        CODEPAGE.put('П', 0x8F);

        CODEPAGE.put('Р', 0x90);
        CODEPAGE.put('С', 0x91);
        CODEPAGE.put('Т', 0x92);
        CODEPAGE.put('У', 0x93);
        CODEPAGE.put('Ф', 0x94);
        CODEPAGE.put('Х', 0x95);
        CODEPAGE.put('Ц', 0x96);
        CODEPAGE.put('Ч', 0x97);
        CODEPAGE.put('Ш', 0x98);
        CODEPAGE.put('Щ', 0x99);
        CODEPAGE.put('Ъ', 0x9A);
        CODEPAGE.put('Ы', 0x9B);
        CODEPAGE.put('Ь', 0x9C);
        CODEPAGE.put('Э', 0x9D);
        CODEPAGE.put('Ю', 0x9E);
        CODEPAGE.put('Я', 0x9F);

        CODEPAGE.put('а', 0xA0);
        CODEPAGE.put('б', 0xA1);
        CODEPAGE.put('в', 0xA2);
        CODEPAGE.put('г', 0xA3);
        CODEPAGE.put('д', 0xA4);
        CODEPAGE.put('е', 0xA5);
        CODEPAGE.put('ж', 0xA6);
        CODEPAGE.put('з', 0xA7);
        CODEPAGE.put('и', 0xA8);
        CODEPAGE.put('й', 0xA9);
        CODEPAGE.put('к', 0xAA);
        CODEPAGE.put('л', 0xAB);
        CODEPAGE.put('м', 0xAC);
        CODEPAGE.put('н', 0xAD);
        CODEPAGE.put('о', 0xAE);
        CODEPAGE.put('п', 0xAF);

        CODEPAGE.put('░', 0xB0);
        CODEPAGE.put('▒', 0xB1);
        CODEPAGE.put('▓', 0xB2);
        CODEPAGE.put('│', 0xB3);
        CODEPAGE.put('┤', 0xB4);
        CODEPAGE.put('╡', 0xB5);
        CODEPAGE.put('╢', 0xB6);
        CODEPAGE.put('╖', 0xB7);
        CODEPAGE.put('╕', 0xB8);
        CODEPAGE.put('╣', 0xB9);
        CODEPAGE.put('║', 0xBA);
        CODEPAGE.put('╗', 0xBB);
        CODEPAGE.put('╝', 0xBC);
        CODEPAGE.put('╜', 0xBD);
        CODEPAGE.put('╛', 0xBE);
        CODEPAGE.put('┐', 0xBF);

        CODEPAGE.put('└', 0xC0);
        CODEPAGE.put('┴', 0xC1);
        CODEPAGE.put('┬', 0xC2);
        CODEPAGE.put('├', 0xC3);
        CODEPAGE.put('─', 0xC4);
        CODEPAGE.put('┼', 0xC5);
        CODEPAGE.put('╞', 0xC6);
        CODEPAGE.put('╟', 0xC7);
        CODEPAGE.put('╚', 0xC8);
        CODEPAGE.put('╔', 0xC9);
        CODEPAGE.put('╩', 0xCA);
        CODEPAGE.put('╦', 0xCB);
        CODEPAGE.put('╠', 0xCC);
        CODEPAGE.put('═', 0xCD);
        CODEPAGE.put('╬', 0xCE);
        CODEPAGE.put('╧', 0xCF);

        CODEPAGE.put('╨', 0xD0);
        CODEPAGE.put('╤', 0xD1);
        CODEPAGE.put('╥', 0xD2);
        CODEPAGE.put('╙', 0xD3);
        CODEPAGE.put('╘', 0xD4);
        CODEPAGE.put('╒', 0xD5);
        CODEPAGE.put('╓', 0xD6);
        CODEPAGE.put('╫', 0xD7);
        CODEPAGE.put('╪', 0xD8);
        CODEPAGE.put('┘', 0xD9);
        CODEPAGE.put('┌', 0xDA);
        CODEPAGE.put('█', 0xDB);
        CODEPAGE.put('▄', 0xDC);
        CODEPAGE.put('▌', 0xDD);
        CODEPAGE.put('▐', 0xDE);
        CODEPAGE.put('▀', 0xDF);

        CODEPAGE.put('р', 0xE0);
        CODEPAGE.put('с', 0xE1);
        CODEPAGE.put('т', 0xE2);
        CODEPAGE.put('у', 0xE3);
        CODEPAGE.put('ф', 0xE4);
        CODEPAGE.put('х', 0xE5);
        CODEPAGE.put('ц', 0xE6);
        CODEPAGE.put('ч', 0xE7);
        CODEPAGE.put('ш', 0xE8);
        CODEPAGE.put('щ', 0xE9);
        CODEPAGE.put('ъ', 0xEA);
        CODEPAGE.put('ы', 0xEB);
        CODEPAGE.put('ь', 0xEC);
        CODEPAGE.put('э', 0xED);
        CODEPAGE.put('ю', 0xEE);
        CODEPAGE.put('я', 0xEF);

        CODEPAGE.put('Ё', 0xF0);
        CODEPAGE.put('ё', 0xF1);
        CODEPAGE.put('€', 0xF2);
        CODEPAGE.put('\u2011', 0xFA);
        CODEPAGE.put('$', 0xFC);
        CODEPAGE.put('■', 0xFF);
    }

    public ExternalCharsetEncoder(Charset charset) {
        super(charset, 1, 1);
    }

    @Override
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        for (;;) {
            if (in.hasRemaining() && !out.hasRemaining()) {
                return CoderResult.OVERFLOW;
            }
            try {
                char c = in.get();
                Integer outCode;
                if (CODEPAGE.containsKey(c)) {
                    outCode = CODEPAGE.get(c);
                } else {
                    outCode = CODEPAGE.get('?');
                }
                out.put(outCode.byteValue());
            } catch (BufferUnderflowException e) {
                return CoderResult.UNDERFLOW;
            }
        }
    }
}
