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
package com.github.kurbatov.atol4j.command.common;

import com.github.kurbatov.atol4j.command.Command;
import com.github.kurbatov.atol4j.command.Result;
import com.github.kurbatov.atol4j.command.Wait;
import com.github.kurbatov.atol4j.command.common.controll.BeepCommand;
import com.github.kurbatov.atol4j.command.common.controll.BuzzCommand;
import com.github.kurbatov.atol4j.command.common.controll.CutCommand;
import com.github.kurbatov.atol4j.command.common.controll.OpenCashboxCommand;
import com.github.kurbatov.atol4j.command.common.controll.OpenCashboxImpulseCommand;
import com.github.kurbatov.atol4j.command.common.controll.RebootCommand;
import com.github.kurbatov.atol4j.command.common.controll.SendDataCommand;
import com.github.kurbatov.atol4j.command.common.print.DiscardLastDocumentCommand;
import com.github.kurbatov.atol4j.command.common.print.PrintBarcodeCommand;
import com.github.kurbatov.atol4j.command.common.print.PrintClicheCommand;
import com.github.kurbatov.atol4j.command.common.print.PrintImageCommand;
import com.github.kurbatov.atol4j.command.common.print.PrintStringCommand;
import com.github.kurbatov.atol4j.command.common.print.ReprintLastDocumentCommand;
import com.github.kurbatov.atol4j.command.common.print.RetryReportCommand;
import java.awt.image.BufferedImage;

/**
 * Добавляет в цепочку команды, которые можно выполнить в любом режиме.
 *
 * @param <T> тип объекта для накопления последующих команд
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public interface CommonCommandBuilder<T> {

    /**
     * Напечатать строку символов на кассовой ленте.
     *
     * @param string строка символов
     * @return объект для формирования цепочки команд
     */
    default T printString(String string) {
        return append(new PrintStringCommand(string));
    }
    
    /**
     * Напечатать изображение на чековой ленте.
     *
     * @param image изображение
     * @param offset сдвиг в пикселях
     * @param repeat количество повторов печати
     * @return объект для формирования цепочки команд
     */
    default T printImage(BufferedImage image, int offset, int repeat) {
        return append(new PrintImageCommand(repeat, offset, image));
    }
    
    /**
     * Напечатать изображение на чековой ленте.
     *
     * @param image изображение
     * @return объект для формирования цепочки команд
     */
    default T printImage(BufferedImage image) {
        return append(new PrintImageCommand(1, 0, image));
    }

    /**
     * Напечатать клише на чековой ленте.
     * 
     * @return объект для формирования цепочки команд
     */
    default T printCliche() {
        return append(PrintClicheCommand.INSTANCE);
    }

    /**
     * Напечатать штрих-код или QR-код.
     *
     * @param barcode данные для кодирования в штрих-коде
     * @return объект для формирования цепочки команд
     */
    default T printBarcode(String barcode) {
        return append(new PrintBarcodeCommand(barcode));
    }

    /**
     * Повторная печать последнего документа.
     *
     * @return объект для формирования цепочки команд
     */
    default T reprintLastDocument() {
        return append(ReprintLastDocumentCommand.INSTANCE);
    }
    
    /**
     * Очистка буфера последнего документа.
     *
     * @return объект для формирования цепочки команд
     */
    default T discardLastDocument() {
        return append(DiscardLastDocumentCommand.INSTANCE);
    }

    /**
     * Допечать отчёта.
     * 
     * Команда используется для запуска печати отчета, который уже сохранен в
     * фискальном накопителе, но по причине сбоя в работе ККТ (например,
     * отключении питания) он не был распечатан полностью. Данная команда
     * предназначена для запуска печати таких отчетов на чековой ленте.
     *
     * @return объект для формирования цепочки команд
     */
    default T retryReport() {
        return append(RetryReportCommand.INSTANCE);
    }

    /**
     * Перезагрузить ККТ.
     *
     * @return объект для формирования цепочки команд
     */
    default T reboot() {
        return append(RebootCommand.INSTANCE);
    }
    
    /**
     * Отрезать чек полностью.
     *
     * @return объект для формирования цепочки команд
     */
    default T cut() {
        return append(new CutCommand(true));
    }
    
    /**
     * Отрезать чек.
     *
     * @param full true - полная отрезка, false - частичная отрезка
     * @return объект для формирования цепочки команд
     */
    default T cut(boolean full) {
        return append(new CutCommand(full));
    }

    /**
     * Подать звуковой сигнал заданного тона и продолжительности.
     *
     * @param tone частота звука от 100 до 2500 Гц
     * @param duration продолжительность сигнала в десятках мллисекунд
     * @return объект для формирования цепочки команд
     */
    default T beep(int tone, int duration) {
        return append(new BeepCommand(tone, duration));
    }
    
    /**
     * Воспроизвести звуковой сигнал.
     *
     * @return объект для формирования цепочки команд
     */
    default T buzz() {
        return append(BuzzCommand.INSTANCE);
    }
    
    /**
     * Передать данные внешнему устройству, подключенному к ККТ.
     *
     * @param port порт подключения внешнего устройства
     * @param data данные для передачи
     * @return объект для формирования цепочки команд
     * @throws IllegalArgumentException при попытке передачи пустого массива или
     * если объём передаваемых данных более 94 байт
     */
    default T sendData(byte port, byte[] data) {
        return append(new SendDataCommand(port, data));
    }

    /**
     * Открыть денежный ящик при помощи импульсного сигнала.
     *
     * @param pulse время подачи напряжения на ящик х10 мс
     * @param delay время задержки перед повтором х10 мс
     * @param repeat количество повторов от 0 до 99
     * @return объект для формирования цепочки команд
     */
    default T openCashboxImpulse(int pulse, int delay, int repeat) {
        return append(new OpenCashboxImpulseCommand(pulse, delay, repeat));
    }
    
    /**
     * Открыть денежный ящик.
     *
     * @return объект для формирования цепочки команд
     */
    default T openCashbox() {
        return append(OpenCashboxCommand.INSTANCE);
    }
    
    /**
     * Добавить задержку перед отправкой устройству следующей команды из цепочки.
     *
     * @param millis длительность задержки в миллисекундах
     * @return объект для формирования цепочки команд
     */
    default T sleep(long millis) {
        return append(new Wait(millis));
    }
    
    /**
     * Выйти из текущего подрежима или режима. Если устройство находится в
     * подрежиме, то для выхода в режим выбора (режим верхнего уровня)
     * необходимо выполнить команду дважды.
     *
     * @return объект для формирования цепочки команд
     */
    CommonCommandBuilder resetMode();

    /**
     * Добавить команду в цепочку выполнения.
     *
     * @param command команда
     * @return объект для формирования цепочки команд
     */
    T append(Command<? extends Result> command);

}
