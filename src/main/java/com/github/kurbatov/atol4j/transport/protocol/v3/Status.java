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
package com.github.kurbatov.atol4j.transport.protocol.v3;

/**
 * Содержит определения статусов задания.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public interface Status {
    
    /**
     * В очереди
     */
    static final byte PENDING = (byte) 0xA1;
    
    /**
     * Выполняется
     */
    static final byte IN_PROGRESS = (byte) 0xA2;
    
    /**
     * Выполнено без ошибок
     */
    static final byte RESULT = (byte) 0xA3;
    
    /**
     * Ошибка
     */
    static final byte ERROR = (byte) 0xA4;
    
    /**
     * Остановлено
     */
    static final byte STOPPED = (byte) 0xA5;
    
    /**
     * Выполнено асинхронно без ошибок
     */
    static final byte ASYNC_RESULT = (byte) 0xA6;
    
    /**
     * Во время асинхронного выполнения произошла ошибка
     */
    static final byte ASYNC_ERROR = (byte) 0xA7;
    
    /**
     * Ожидание данных от внешнего устройства
     */
    static final byte WAITING = (byte) 0xA8;
    
    // ошибки
    
    /**
     * Переполнение очереди команд
     */
    static final byte OVERFLOW = (byte) 0xB1;
    
    /**
     * Уже существует
     */
    static final byte ALREADY_EXISTS = (byte) 0xB2;
    
    /**
     * Не найдено
     */
    static final byte NOT_FOUND = (byte) 0xB3;
    
    /**
     * Недопустимое значение
     */
    static final byte ILLEGAL_VALUE = (byte) 0xB4;
    
}