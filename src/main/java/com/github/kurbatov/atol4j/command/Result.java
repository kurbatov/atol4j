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

import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Результат выполнения команды.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class Result {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Result.class);
    
    private static final Properties ERROR_MESSAGES;
    
    public static byte RESPONSE_CODE = 0x55;
    
    private byte responseCode;
    
    private byte errorCode;
    
    private byte errorExt;

    static {
        ERROR_MESSAGES = new Properties();
        try {
            ERROR_MESSAGES.load(Result.class.getClassLoader().getResourceAsStream("META-INF/error-messages.properties"));
        } catch (IOException e) {
            LOGGER.warn("Cannot load error messages.", e);
        }
    }
    
    /**
     * Создаёт пустой результат
     */
    public Result() {
    }

    /**
     * Создаёт результат с указанным кодом ошибки и расширенным кодом ошибки.
     *
     * @param errorCode код ошибки
     * @param errorExt расширенный код ошибки
     */
    public Result(byte errorCode, byte errorExt) {
        this.errorCode = errorCode;
        this.errorExt = errorExt;
    }
    
    /**
     * Создаёт результат с указанным кодом ответа, кодом ошибки и расширенным
     * кодом ошибки.
     *
     * @param responseCode код ответа
     * @param errorCode код ошибки
     * @param errorExt расширенный код ошибки
     */
    public Result(byte responseCode, byte errorCode, byte errorExt) {
        this.responseCode = responseCode;
        this.errorCode = errorCode;
        this.errorExt = errorExt;
    }
    
    /**
     * Создаёт результат с полученными из буфера кодом ответа, кодом ошибки и
     * расширенным кодом ошибки.
     *
     * @param buffer буфер, содержащий код ответа, код ошибки и расширенный код
     * ошибки
     */
    public Result(byte[] buffer) {
        if (buffer.length < 3) {
            throw new IllegalArgumentException("Размер буфер для построения результата выполнения команды должен быть не меньше 3 байт.");
        }
        this.responseCode = buffer[0];
        this.errorCode = buffer[1];
        this.errorExt = buffer[2];
    }

    /**
     * Получить код ответа.
     *
     * @return код ответа
     */
    public byte getResponseCode() {
        return responseCode;
    }

    /**
     * Установить код ответа.
     *
     * @param responseCode код ответа
     */
    public void setResponseCode(byte responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Получить код ошибки.
     *
     * @return код ошибки
     */
    public byte getErrorCode() {
        return errorCode;
    }

    /**
     * Установить код ошибки.
     *
     * @param errorCode код ошибки
     */
    public void setErrorCode(byte errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Получить расширенный код ошибки.
     *
     * @return расширенный код ошибки
     */
    public byte getErrorExt() {
        return errorExt;
    }

    /**
     * Установить расширенный код ошибки.
     *
     * @param errorExt расширенный код ошибки
     */
    public void setErrorExt(byte errorExt) {
        this.errorExt = errorExt;
    }
    
    /**
     * Узнать, содержит ли ответ ошибку.
     *
     * @return true - ответ содержит ошибку, false - ответ не содержит ошибок
     */
    public boolean hasError() {
        return errorCode != Errors.NO_ERROR.code;
    }
    
    /**
     * Получить человеко-читаемое сообщение об ошибке.
     *
     * @return сообщение об ошибке
     */
    public String getErrorMessage() {
        String key;
        if (getErrorExt() == Errors.NO_ERROR.code) {
            key = String.format("%d", Byte.toUnsignedInt(getErrorCode()));
        } else {
            key = String.format("%d.%d", Byte.toUnsignedInt(getErrorCode()), Byte.toUnsignedInt(getErrorExt()));
        }
        String message = ERROR_MESSAGES.getProperty(key);
        if (message == null) {
            message = String.format("<НЕИЗВЕСТНЫЙ ТИП ОШИБКИ>: %d", Byte.toUnsignedInt(getErrorCode()));
        }
        return message;
    }
    
    /**
     * Декодирует число из бинарно-десятичной записи в целое десятичное число.
     *
     * @param buffer байты числа в бинарно-десятичном формате
     * @return декодированное число
     */
    public static long decode(byte... buffer) {
        long result = 0;
        for (int i = 0; i < buffer.length; i++) {
            byte b = buffer[i];
            int a = b & 15;
            int c = 10 * ((b & 0xF0) >>> 4);
            int r = a + c;
            result += r * Math.pow(100, buffer.length - i - 1);
        }
        return result;
    }
    
}
