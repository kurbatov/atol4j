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
package com.github.kurbatov.atol4j.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Реализация транспортировки данных по сетевому интерфейсу (Ethernet или WiFi).
 * 
 * Этот транспорт не поддерживает протокол обмена нижнего уровня версии 2.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class NetworkTransport implements Transport {

    private ExecutorService executor;
    private AsynchronousSocketChannel client;
    private final InetSocketAddress addr;
    private final Set<Consumer<byte[]>> subscribers = new CopyOnWriteArraySet<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkTransport.class);
    
    /**
     * Создаёт объект, который передаёт данные ККТ по протоколу TCP/IP.
     * 
     * Этот транспорт не поддерживает протокол обмена нижнего уровня версии 2.
     *
     * @param host хост
     */
    public NetworkTransport(String host) {
        this(host, 5555);
    }
    
    /**
     * Создаёт объект, который передаёт данные ККТ по протоколу TCP/IP.
     * 
     * Этот транспорт не поддерживает протокол обмена нижнего уровня версии 2.
     *
     * @param host хост
     * @param port порт
     */
    public NetworkTransport(String host, int port) {
        addr = new InetSocketAddress(host, port);
    }
    
    @Override
    public void connect() {
        executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "atol4j-NetworkTransport"));
        try {
            client = AsynchronousSocketChannel.open();
            client.connect(addr)
                    .get();
            ByteBuffer readBuffer = ByteBuffer.allocate(128);
            client.read(readBuffer, client, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                @Override
                public void completed(Integer result, AsynchronousSocketChannel attachment) {
                    if (result > 0) {
                        byte[] msg = new byte[readBuffer.position()];
                        System.arraycopy(readBuffer.array(), 0, msg, 0, msg.length);
                        subscribers.forEach(s -> s.accept(msg));
                    }
                    if (result > -1) {
                        readBuffer.clear();
                        attachment.read(readBuffer, attachment, this);
                    }
                }

                @Override
                public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                    if (exc instanceof AsynchronousCloseException) {
                        LOGGER.debug("Соединение с ККТ закрыто", exc);
                    } else {
                        LOGGER.error("Ошибка чтения данных от ККТ", exc);
                    }
                }
            });
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disconnect() {
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        try {
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(byte[] b) {
        executor.submit(() -> {
            try {
                client.write(ByteBuffer.wrap(b)).get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Ошибка отправки данных по сети", e);
            }
        });
    }

    @Override
    public byte[] read() {
        CompletableFuture<byte[]> result = new CompletableFuture<>();
        subscribe(new Consumer<byte[]>() {
            @Override
            public void accept(byte[] b) {
                result.complete(b);
                subscribers.remove(this);
            }
        });
        return result.join();
    }

    @Override
    public byte[] read(int count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] read(int count, int timeout) throws TimeoutException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void subscribe(Consumer<byte[]> consumer) {
        subscribers.add(consumer);
    }
    
}
