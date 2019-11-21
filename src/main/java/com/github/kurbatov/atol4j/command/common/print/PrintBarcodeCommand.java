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
package com.github.kurbatov.atol4j.command.common.print;

import com.github.kurbatov.atol4j.command.BasicCommand;
import java.nio.charset.StandardCharsets;

/**
 * Печать штрих-кода или QR-кода.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class PrintBarcodeCommand extends BasicCommand {
    
    /**
     * Выравнивание по левому краю
     */
    public static final byte ALIGN_LEFT = 1;
    
    /**
     * Выравнивание по центру
     */
    public static final byte ALIGN_CENTER = 2;
    
    /**
     * Выравнивание по правому краю
     */
    public static final byte ALIGN_RIGHT = 3;
    
    private static final byte COMMAND = (byte) 0xC1;
    
    /**
     * Создаёт команду печати штрих-кода по центру чековой ленты.
     *
     * @param barcode данные для кодирования
     */
    public PrintBarcodeCommand(String barcode) {
        this(barcode, ALIGN_CENTER);
    }
    
    /**
     * Создаёт команду печати штрих-кода с указанным выравниванием на чековой
     * ленте.
     *
     * @param barcode данные для кодирования
     * @param alignment тип выравнивания
     */
    public PrintBarcodeCommand(String barcode, byte alignment) {
        super(wrap(barcode, alignment));
    }
    
    private static byte[] wrap(String barcode, byte alignment) {
        byte type = 0;
        if (barcode.length() == 13) {
            type = 1;
        }
        byte[] header = new byte[] {
            COMMAND,
            type,
            alignment,
            1, // коэффициент масштабирования по горизонтали
            0, 0, // версия (0 - автоматический подбор)
            1, 0, // опции (QR: UTF-8, числовой; EAN-13: рассчитать контрольную цифру)
            0, // уровень коррекции
            0, // количество строк
            0, // количество столбцов
            0, 0, // пропорции штрих-кода
            0, 0 // пропорции пикселя
        };
        byte[] barcodeBytes = barcode.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[header.length + barcodeBytes.length];
        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(barcodeBytes, 0, result, header.length, barcodeBytes.length);
        return result;
    }
    
}
