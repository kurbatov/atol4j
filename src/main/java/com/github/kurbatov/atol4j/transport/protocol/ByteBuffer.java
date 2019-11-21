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
package com.github.kurbatov.atol4j.transport.protocol;

/**
 * Буфер бинарных данных, который позволяет добавлять данные в конец и получать
 * их из начала.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class ByteBuffer {

    private byte[] buffer;

    private volatile int head;

    private volatile int tail;

    /**
     * Создаёт буфер с заданным начальным объёмом.
     *
     * @param initialCapacity начальный объём
     */
    public ByteBuffer(int initialCapacity) {
        buffer = new byte[initialCapacity];
    }

    /**
     * Добавляет данные из указанного массива в конец этого буфера.
     *
     * @param bytes массив данных для добавления
     * @param pos индекс стартового элемента
     * @param len длинна блока данных
     */
    public synchronized void append(byte[] bytes, int pos, int len) {
        ensure(len);
        System.arraycopy(bytes, pos, buffer, tail, len);
        tail += len;
    }

    /**
     * Добавляет все данные из указанного массива в конец этого буфера.
     *
     * @param bytes данные для добавления в конец буфера
     */
    public void append(byte... bytes) {
        append(bytes, 0, bytes.length);
    }

    /**
     * Получить значение в i-той позиции буфера.
     *
     * @param i индекс позиции буфера
     * @return значение в указанной позиции
     */
    public byte get(int i) {
        return buffer[head + i];
    }

    /**
     * Взять один байт данных из начала буфера.
     *
     * Последующие вызовы этого метода будут возвращать следующие байты из
     * буфера.
     *
     * @return первый байт буфера
     * @throws IndexOutOfBoundsException при попытке обращения к пустому буферу
     */
    public byte take() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("Attempt to take a byte from an empty buffer");
        }
        byte result = buffer[head];
        skip(1);
        return result;
    }

    /**
     * Взять указанное количество байт из начала буфера.
     * 
     * Последующие вызовы этого метода будут возвращать следующие байты из
     * буфера.
     *
     * @param len количество байт
     * @return данные из буфера
     * @throws IndexOutOfBoundsException при попытке взять больше байт, чем
     * хранится в буфере
     */
    public byte[] take(int len) {
        if (len < 1 || len > size()) {
            throw new IndexOutOfBoundsException(String.format("Attempt to take %d bytes from buffer of %d", len, size()));
        }
        byte[] result = new byte[len];
        System.arraycopy(buffer, head, result, 0, len);
        skip(len);
        return result;
    }
    
    /**
     * Пропустить заданное количество байт с начала буфера.
     *
     * @param len количество байт, которое нужно пропустить
     * @throws IndexOutOfBoundsException при попытке пропустить больше байт, чем
     * хранится в буфере
     */
    public void skip(int len) {
        if (size() < len) {
            throw new IndexOutOfBoundsException(String.format("Attempt to skip %d bytes in buffer of %d", len, size()));
        }
        head += len;
        if (head == tail) {
            head = 0;
            tail = 0;
        }
    }

    /**
     * Найти позицию указанного байта в буфере.
     *
     * @param b байт, позицию которого нужно найти
     * @return индекс искомого байта.
     */
    public int find(byte b) {
        return find(b, 0);
    }
    
    /**
     * Найти позицию указанного байта в буфере.
     *
     * @param b байт, позицию которого нужно найти
     * @param fromIndex индекс начала поиска
     * @return индекс искомого байта.
     */
    public int find(byte b, int fromIndex) {
        int result = -1;
        for (int i = head + fromIndex; i < tail; i++) {
            if (buffer[i] == b) {
                result = i - head;
                break;
            }
        }
        return result;
    }

    /**
     * Узнать количество данных в буфере.
     *
     * @return количество байт в буфере
     */
    public int size() {
        return tail - head;
    }
    
    /**
     * Узнать пуст ли буфер.
     *
     * @return true - буфер пуст, false - в буфере есть данные.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    private void ensure(int capasity) {
        if (buffer.length - size() < capasity) {
            byte[] tmp = new byte[buffer.length + capasity * 2];
            System.arraycopy(buffer, head, tmp, 0, size());
            buffer = tmp;
            tail = size();
            head = 0;
        } else if (buffer.length - tail < capasity) {
            System.arraycopy(buffer, head, buffer, 0, size());
            tail = size();
            head = 0;
        }
    }
}
