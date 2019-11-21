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
package com.github.kurbatov.atol4j.command.common.request;

import com.github.kurbatov.atol4j.CashRegister;
import com.github.kurbatov.atol4j.command.Command;
import com.github.kurbatov.atol4j.command.Result;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * Запрос версии прошивки и поддерживаемого языка.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class FirmwareVersionRequest implements Command<FirmwareVersionRequest.Response>{
    
    public static final byte SOURCE_CPU = 1;
    
    public static final byte SOURCE_BOOTBLOCK = 3;
    
    private static final byte COMMAND = (byte) 0x9D;
    
    private final byte source;

    public FirmwareVersionRequest(byte source) {
        this.source = source;
    }
    
    @Override
    public CompletableFuture<Response> executeOn(CashRegister device) {
        return device.execute(COMMAND, source).thenApply(r -> new Response(r));
    }
    
    /**
     * Версия прошивки.
     */
    public static class Response extends Result {

        private final long major;
        private final long minor;
        private final long language;
        private final long build;
        
        public Response(byte[] buf) {
            super(buf[0], buf[1], buf[2]);
            if (hasError()) {
                major = 0;
                minor = 0;
                language = 0;
                build = 0;
            } else {
                major = decode(Arrays.copyOfRange(buf, 3, 4));
                minor = decode(Arrays.copyOfRange(buf, 4, 5));
                language = decode(Arrays.copyOfRange(buf, 5, 6));
                build = decode(Arrays.copyOfRange(buf, 6, 8));
            }
        }

        /**
         * Версия прошивки.
         * 
         * @return номер версии прошивки
         */
        public long getMajor() {
            return major;
        }

        /**
         * Подверсия прошивки.
         * 
         * @return номер подверсии прошивки
         */
        public long getMinor() {
            return minor;
        }

        /**
         * Язык прошивки.
         * 
         * @return код языка прошивки (0 - русский)
         */
        public long getLanguage() {
            return language;
        }

        /**
         * Сборка прошивки.
         * 
         * @return номер сборки прошивки
         */
        public long getBuild() {
            return build;
        }

        @Override
        public String toString() {
            return String.format("%d.%d.%d.%d", major, minor, language, build);
        }

    }
    
}
