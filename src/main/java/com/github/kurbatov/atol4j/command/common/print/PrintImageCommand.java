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

import com.github.kurbatov.atol4j.CashRegister;
import com.github.kurbatov.atol4j.command.Command;
import java.awt.image.BufferedImage;
import com.github.kurbatov.atol4j.command.Result;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Печатает картинку на кассовой ленте.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class PrintImageCommand implements Command<Result> {
    
    private static final byte COMMAND = (byte) 0x8E;
    
    private static final Map<Byte, Integer> RASTER_LENGTH = new HashMap<>();
    
    private final int repeat;
    
    private final int offset;
    
    private final BufferedImage image;
    
    static {
        RASTER_LENGTH.put((byte) 57, 72);
        RASTER_LENGTH.put((byte) 63, 72);
        RASTER_LENGTH.put((byte) 69, 72);
        RASTER_LENGTH.put((byte) 81, 72);
        RASTER_LENGTH.put((byte) 67, 48);
        RASTER_LENGTH.put((byte) 78, 48);
        RASTER_LENGTH.put((byte) 61, 48);
        RASTER_LENGTH.put((byte) 75, 48);
        RASTER_LENGTH.put((byte) 72, 48);
        RASTER_LENGTH.put((byte) 82, 48);
        RASTER_LENGTH.put((byte) 84, 48);
        RASTER_LENGTH.put((byte) 80, 47);
        RASTER_LENGTH.put((byte) 86, 47);
        RASTER_LENGTH.put((byte) 64, 52);
        RASTER_LENGTH.put((byte) 62, 54);
    }

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
        this.repeat = repeat;
        this.offset = offset;
        this.image = image;
    }

    @Override
    public CompletableFuture<Result> executeOn(CashRegister device) {
        byte[] header = new byte[] {
            COMMAND,
            1,
            (byte) (repeat >>> 8),
            (byte) repeat,
            (byte) (offset >>> 8),
            (byte) offset
        };
        byte model = device.getDeviceType().getModel();
        byte[] raster = new byte[RASTER_LENGTH.get(model)];
        int maxWidth = raster.length * 8;
        int width = image.getWidth();
        int height = image.getHeight();
        int padding = (maxWidth - width) / 2;
        CompletableFuture<Result> result = CompletableFuture.completedFuture(new Result());
        for (int i = 0; i < height; i++) {
            int[] data = image.getRGB(0, i, width, 1, null, 0, width);
            if (padding < 0) {
                data = Arrays.copyOfRange(data, -padding, -padding + maxWidth);
            } else if (padding > 0) {
                int[] tmp = new int[data.length + padding];
                System.arraycopy(data, 0, tmp, padding, data.length);
                data = tmp;
                Arrays.fill(data, 0, padding, 0xFFFFFF);
            }
            raster = convertRow(data, raster);
            byte[] command = new byte[header.length + raster.length];
            System.arraycopy(header, 0, command, 0, header.length);
            System.arraycopy(raster, 0, command, header.length, raster.length);
            result = result.thenCompose(r ->
                    r.hasError() ?
                            CompletableFuture.completedFuture(r) :
                            device.execute(command).thenApply(b -> new Result(b))
            );
        }
        return result;
    }
    
    private static byte[] convertRow(int[] src, byte[] dst) {
        for (int i = 0; i < dst.length; i++) {
            int seq = 0;
            for (int j = 0; j < 8 && i * 8 + j < src.length; j++) {
                int pixel = src[i * 8 + j];
                int point = pixelToPoint(pixel);
                seq |= (point << (7 - j));
            }
            dst[i] = (byte) seq;
        }
        return dst;
    }
    
    private static int pixelToPoint(int pixel) {
        int brightness = (pixel & 0xFF + ((pixel >>> 8) & 0xFF) + ((pixel >>> 16) & 0xFF)) / 3;
        return brightness < 128 ? 1 : 0;
    }
    
}
