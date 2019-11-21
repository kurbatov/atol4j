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
package com.github.kurbatov.atol4j.command.registration;

import com.github.kurbatov.atol4j.CashRegister;
import com.github.kurbatov.atol4j.command.Command;
import java.util.concurrent.CompletableFuture;

/**
 * Рассчёт по чеку.
 *
 * После удачного выполнения этой команды ККТ переходит в состояние 1.4 (режим
 * приёма платежей по чеку).
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class RemittanceCommand implements Command<RemittanceResult> {

    /**
     * Наличные
     */
    public static final byte CASH = 0x01;

    /**
     * Безнал (электронные)
     */
    public static final byte CASHLESS = 0x02;

    /**
     * Предварительная оплата (аванс)
     */
    public static final byte ADVANCE_PAYMENT = 0x03;

    /**
     * Последующая оплата (кредит)
     */
    public static final byte CREDIT = 0x04;

    /**
     * Иная форма оплаты (встречное предоставление)
     */
    public static final byte OTHER = 0x05;

    private static final byte COMMAND = (byte) 0x99;

    private final byte[] message;

    /**
     * Создаёт команду рассчёта по чеку.
     *
     * @param sum сумма в минимальных денежных единицах
     * @param paymentType тип оплаты
     * @param test выполнить команду в тестовом режиме
     */
    public RemittanceCommand(long sum, byte paymentType, boolean test) {
        byte[] s = Command.encode(sum, 5);
        message = new byte[]{COMMAND, (byte) (test ? 1 : 0), paymentType, s[0], s[1], s[2], s[3], s[4]};
    }

    @Override
    public CompletableFuture<RemittanceResult> executeOn(CashRegister device) {
        return device.execute(message).thenApply(r -> new RemittanceResult(r));
    }

}
