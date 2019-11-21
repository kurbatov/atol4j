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
package com.github.kurbatov.atol4j.command;

import com.github.kurbatov.atol4j.CashRegister;
import java.util.concurrent.CompletableFuture;

/**
 * Содержит определения команд верхнего уровня.
 *
 * @param <R> тип результата выполнения команды
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public interface Command<R extends Result> {
    
    /**
     * Исполняет команду на указанном устройстве.
     *
     * @param device устройство
     * @return результат выполнения команды
     */
    CompletableFuture<R> executeOn(CashRegister device);
    
    /**
     * Кодирует число в двоично-десятичный формат.
     *
     * @param n число
     * @param length длинна бинарного представления в байтах
     * @return число в двоично-десятичном формате
     */
    public static byte[] encode(long n, int length) {
        if (n > 4294967295L) {
            throw new IllegalArgumentException("Число не может быть больше, чем 4294967295");
        }
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            long c = n / (long) Math.pow(100, i);
            c = c - (c / 100 * 100);
            long a = c / 10;
            long b = c - a * 10;
            result[length - i - 1] = (byte) ((a << 4) | (b & 0xF));
        }
        return result;
    }
    
}