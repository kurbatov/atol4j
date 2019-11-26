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

import com.github.kurbatov.atol4j.command.Result;
import com.github.kurbatov.atol4j.transport.Transport;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import org.testng.annotations.Test;
import static com.github.kurbatov.atol4j.transport.protocol.v3.Token.*;
import static org.testng.Assert.*;

/**
 *
 * @author Oleg Kurbatov &lt;o.v.kurbatov@gmail.com&gt;
 */
public class TransportProtocolV3Test {
    
    private final TransportProtocolV3 protocol = new TransportProtocolV3(new TransportStub());
    
    @Test
    public void wrapTest() {
        byte[] data = {};
        byte[] frame = protocol.wrap(data);
        assertEquals(frame, new byte[] {STX, 0, 0, 1, (byte) 0x9D});
    }
    
    @Test
    public void unwrapTest() {
        byte[] data = {STX, 0, 0, 1, (byte) 0x9D};
        byte[] frame = protocol.unwrap(data);
        assertEquals(frame, new byte[] {});
    }
    
    @Test
    public void unwrapWithEscapeTest() {
        byte[] data = {STX, 5, 0, (byte) 0xF0, Status.ASYNC_RESULT, 87, Result.RESPONSE_CODE, 0, 0, ESC, TSTX};
        byte[] frame = protocol.unwrap(data);
        assertEquals(frame, new byte[] {Status.ASYNC_RESULT, 87, Result.RESPONSE_CODE, 0, 0});
    }
    
    @Test
    public void wrapUnwrapTest() {
        byte[] data = {0x1F, 0x00, (byte) 0xFF, 0x10, 0x02, 0x03, 0x1A};
        byte[] frame = protocol.wrap(data);
        assertEquals(protocol.unwrap(frame), data, "Unwrapped data should match the original");
    }
    
    @Test(dependsOnMethods = {"wrapTest", "wrapUnwrapTest"})
    public void idIncreasesTest() {
        byte[] data = {};
        int baseId = 3;
        for (int i = 0; i <= 220; i++) {
            byte[] frame = protocol.wrap(data);
            assertEquals(frame[3] & 0xFF, baseId + i);
        }
    }
    
    @Test(dependsOnMethods = "idIncreasesTest")
    public void maxIdTest() {
        byte[] data = {};
        byte[] frame = protocol.wrap(data);
        assertEquals(frame[3], 0);
    }
    
    private static class TransportStub implements Transport {
        @Override
        public void connect() {
        }

        @Override
        public void disconnect() {
        }

        @Override
        public void write(byte[] b) {
        }

        @Override
        public byte[] read() {
            return null;
        }

        @Override
        public byte[] read(int count) {
            return null;
        }

        @Override
        public byte[] read(int count, int timeout) throws TimeoutException {
            return null;
        }

        @Override
        public void subscribe(Consumer<byte[]> consumer) {
            
        }
    }
    
}
