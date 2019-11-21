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

import com.github.kurbatov.atol4j.command.Result;
import java.util.Arrays;
import static com.github.kurbatov.atol4j.command.Result.decode;

/**
 * Результат выполнения команды {@link RemittanceCommand рассчёта по чеку} и
 * {@link RemittanceStornoCommand сторно рассчёта по чеку}.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class RemittanceResult extends Result {

    private int remainder = -1;

    private int change = -1;

    public RemittanceResult(byte[] buffer) {
        super(buffer);
        if (buffer.length == 12) {
            remainder = (int) decode(Arrays.copyOfRange(buffer, 3, 8));
            change = (int) decode(Arrays.copyOfRange(buffer, 8, 13));
        }
    }

    /**
     * Неоплаченная сумма чека. Если значение равно нулю, то чек полностью
     * оплачен и может быть закрыт.
     *
     * @return неоплаченная сумма чека или -1, если произошла ошибка
     */
    public int getRemainder() {
        return remainder;
    }

    /**
     * Сумма сдачи, начисленная в результате всех платежей по текущему чеку (с
     * учётом этого платежа).
     *
     * @return сумма сдачи или -1, если произошла ошибка
     */
    public int getChange() {
        return change;
    }

}
