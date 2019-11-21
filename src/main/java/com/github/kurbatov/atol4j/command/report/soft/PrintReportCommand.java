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
package com.github.kurbatov.atol4j.command.report.soft;

import com.github.kurbatov.atol4j.command.BasicCommand;

/**
 * Снятие отчета без гашения.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class PrintReportCommand extends BasicCommand {
    
    /**
     * суточный отчет без гашения
     */
    public static final byte DAILY = 0x01;
    
    /**
     * отчет по секциям
     */
    public static final byte SECTIONS = 0x02;
    
    /**
     * отчет по кассирам
     */
    public static final byte CASHIERS = 0x03;
    
    /**
     * почасовой отчет
     */
    public static final byte HOURLY = 0x05;
    
    /**
     * отчет количеств
     */
    public static final byte COUNTS = 0x07;
    
    /**
     * служебный отчет
     */
    public static final byte SERVICE = 0x08;
    
    /**
     * фискальный отчет о текущем состоянии расчетов
     */
    public static final byte FISCAL = 0x09;
    
    /**
     * отчет по товарам
     */
    public static final byte GOODS = 0x10;
    
    /**
     * отчет по товарам по СНО
     */
    public static final byte GOODS_SNO = 0x13;
    
    /**
     * отчет по товарам по секциям
     */
    public static final byte GOODS_SECTIONS = 0x14;
    
    /**
     * отчет по товарам по сумме
     */
    public static final byte GOODS_TOTALS = 0x15;
    
    /**
     * Создаёт команду снятия отчёта без гашения указанного типа.
     *
     * @param reportType тип отчёта
     */
    public PrintReportCommand(byte reportType) {
        super((byte) 0x67, reportType);
    }

}
