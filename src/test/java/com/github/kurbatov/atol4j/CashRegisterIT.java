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
package com.github.kurbatov.atol4j;

import com.github.kurbatov.atol4j.command.Result;
import com.github.kurbatov.atol4j.command.common.SetModeCommand;
import com.github.kurbatov.atol4j.command.registration.CloseBillCommand;
import com.github.kurbatov.atol4j.command.registration.OpenBillCommand;
import com.github.kurbatov.atol4j.transport.SerialTransport;
import com.github.kurbatov.atol4j.transport.protocol.v3.TransportProtocolV3;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Oleg Kurbatov &lt;o.v.kurbatov@gmail.com&gt;
 */
@Test(timeOut = 3000)
public class CashRegisterIT {
    
    private final CashRegister cashRegister = new CashRegister(new TransportProtocolV3(new SerialTransport("COM6")), new byte[] {0, 0});
    
    @Test
    public void connectTest() throws InterruptedException, ExecutionException {
        Result result = cashRegister.connect()
                .get();
        assertNotNull(result);
        assertFalse(result.hasError(), result.getErrorMessage());
        result = cashRegister.command()
                .setMode(SetModeCommand.REGISTRATION, new byte[] {0x29})
                .execute()
                .get();
        assertNotNull(result);
        assertFalse(result.hasError(), result.getErrorMessage());
        result = cashRegister.command()
                .resetMode()
                .execute()
                .get();
        assertNotNull(result);
        assertFalse(result.hasError(), result.getErrorMessage());
    }
    
//    @Test(dependsOnMethods = "connectTest")
//    public void shutdownTest() throws InterruptedException, ExecutionException {
//        Result result = cashRegister.command()
//                .report(new byte[] {0x29})
//                .shutdown()
//                .execute()
//                .get();
//        assertNotNull(result);
//        assertFalse(result.hasError(), result.getErrorMessage());
//    }
    
//    @Test(dependsOnMethods = "connectTest")
//    public void printingTest() throws InterruptedException, ExecutionException, TimeoutException {
//        Result r = cashRegister.command()
//                .printString("Старт тестового прогона печати")
//                .printInfo()
//                .printString("Конец тестовой печати")
//                .execute()
//                .get(10, TimeUnit.SECONDS);
//        assertNotNull(r);
//        assertFalse(r.hasError());
//    }
    
//    @Test(dependsOnMethods = "connectTest")
//    public void registrationTest() throws InterruptedException, ExecutionException {
//        Result result = cashRegister.command()
//                .registration(new byte[] {0x29})
//                .openShift()
//                .openBill(OpenBillCommand.INCOME)
//                .registerItem("Тестовый велик", 3200000, 1)
//                .closeBill(0, CloseBillCommand.CASH, false)
//                .resetMode()
//                .execute()
//                .get();
//        assertNotNull(result);
//        assertFalse(result.hasError());
//    }
    
//    @Test(dependsOnMethods = "connectTest")
//    public void rebootTest() throws InterruptedException, ExecutionException {
//        Result result = cashRegister.command()
//                .reboot()
//                .execute()
//                .get();
//        assertNotNull(result);
//        assertFalse(result.hasError(), result.getErrorMessage());
//    }
    
}
