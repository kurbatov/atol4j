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
import java.awt.image.BufferedImage;
import static com.github.kurbatov.atol4j.command.Command.encode;

/**
 * Печатает картинку на кассовой ленте.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class PrintImageCommand extends BasicCommand {
    
    private static final byte COMMAND = (byte) 0x8E;

    /**
     * Создаёт команду печати картинки на кассовой ленте.
     * 
     * Смещение указывается в пикселях, но фактически смещение происходит
     * побайтно. Например, если указать смещение 4 или 5, то при печати смещение
     * картинки не произойдет, а если указать смещение 8, то картинка сместится
     * на 1 байт (на 8 пикселей).
     *
     * @param repeat количество повторов печати картинки от 0 до 4096
     * @param offset смещение в пикселях
     * @param image картинка
     */
    public PrintImageCommand(int repeat, int offset, BufferedImage image) {
        super(wrap(repeat, offset, image));
    }
    
    private static byte[] wrap(int repeat, int offset, BufferedImage image) {
        byte[] header = new byte[] {
            COMMAND,
            1,
            (byte) (repeat >>> 8),
            (byte) repeat,
            (byte) (offset >>> 8),
            (byte) offset
        };
        byte[] raster = convert(image);
        byte[] result = new byte[header.length + raster.length];
        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(raster, 0, result, header.length, raster.length);
        return result;
    }
    
    private static byte[] convert(BufferedImage image) {
        int[] data = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, 0);
        byte[] result = new byte[data.length / 8];
        for (int i = 0; i < result.length; i++) {
            int row = 0;
            for (int j = 0; j < 8; j++) {
                int pixel = data[i + j];
                int brightness = (pixel & 0xFF + ((pixel >>> 8) & 0xFF) + ((pixel >>> 16) & 0xFF)) / 3;
                int point = brightness < 128 ? 1 : 0;
                row |= (point << (7 - j));
            }
            result[i] = encode(row, 1)[0];
        }
        return result;
    }
    
}
