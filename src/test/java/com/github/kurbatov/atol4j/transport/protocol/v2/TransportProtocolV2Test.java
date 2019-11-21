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
package com.github.kurbatov.atol4j.transport.protocol.v2;

import com.github.kurbatov.atol4j.transport.Transport;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Тестирует упаковку данных в соответствии с протоколом нижнего уровня v2.
 *
 * @author Oleg Kurbatov &lt;o.v.kurbatov@gmail.com&gt;
 */
public class TransportProtocolV2Test {
    
    private TransportProtocolV2 protocol = new TransportProtocolV2(new TransportStub());
    
    @Test
    public void wrapTest() {
        byte[] data = new byte[] {0x1F, 0x00, (byte) 0xFF, 0x10, 0x02, 0x03, 0x1A};
        byte[] frame = protocol.wrap(data);
        assertEquals(new byte[] {0x02, 0x1F, 0x00, (byte) 0xFF, 0x10, 0x10, 0x02, 0x10, 0x03, 0x1A, 0x03, (byte) 0xE8}, frame);
    }
    
    public void unwrapTest() {
        byte[] frame = new byte[] {0x02, 0x1F, 0x00, (byte) 0xFF, 0x10, 0x10, 0x02, 0x10, 0x03, 0x1A, 0x03, (byte) 0xE8};
        byte[] data = protocol.unwrap(frame);
        assertEquals(new byte[] {0x1F, 0x00, (byte) 0xFF, 0x10, 0x02, 0x03, 0x1A}, data);
    }
    
    @Test
    public void checkTest() {
        boolean result = protocol.check(new byte[] {0x02, 0x1F, 0x00, (byte) 0xFF, 0x10, 0x10, 0x02, 0x10, 0x03, 0x1A, 0x03, (byte) 0xE8});
        assertTrue(result, "Must return true for correct frame");
        result = protocol.check(new byte[] {0x02, 0x1F, 0x00, (byte) 0xFF, 0x10, 0x10, 0x02, 0x10, 0x03, 0x1A, 0x03, (byte) 0xE7});
        assertFalse(result, "Must return false for incorrect frame");
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
