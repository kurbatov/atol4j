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

import com.github.kurbatov.atol4j.command.Command;
import com.github.kurbatov.atol4j.command.CommandBuilder;
import com.github.kurbatov.atol4j.command.FirstCommandBuilder;
import com.github.kurbatov.atol4j.command.Result;
import com.github.kurbatov.atol4j.command.common.request.DeviceTypeRequest;
import com.github.kurbatov.atol4j.command.common.request.RequestBuilder;
import com.github.kurbatov.atol4j.transport.Transport;
import com.github.kurbatov.atol4j.transport.protocol.TransportProtocol;
import com.github.kurbatov.atol4j.transport.protocol.v3.TransportProtocolV3;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Кассовый аппарат.
 * 
 * Объект этого класса хранит конфигурацию подключения к физическому устройству
 * и предоставляет доступ к API для работы с ним.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class CashRegister {
    
    private final TransportProtocol protocol;

    private final byte[] password;

    private DeviceTypeRequest.Response deviceType;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CashRegister.class);

    /**
     * Создаёт объект кассового аппарата. Для подключения к физическому
     * устройству будет использован указанный транспортный протокол. Каждая
     * команда устройству будет дополнена указанным паролем.
     *
     * @param protocol транспортный протокол
     * @param password пароль
     */
    public CashRegister(TransportProtocol protocol, byte[] password) {
        this.protocol = protocol;
        this.password = password;
    }
    
    /**
     * Создаёт объект кассового аппарата. Для подключения к физическому
     * устройству будет использован указанный транспорт с последней версией
     * транспортного протокола. Каждая команда устройству будет дополнена
     * указанным паролем.
     *
     * @param transport физический канал передачи данных
     * @param password пароль
     */
    public CashRegister(Transport transport, byte[] password) {
        this(new TransportProtocolV3(transport), password);
    }

    /**
     * Подключиться к устройству.
     * 
     * При подключении состояние устройства сбрасывается до режима выбора.
     * 
     * @return будущее значение ответа устройства содержащее его тип
     */
    public CompletableFuture<DeviceTypeRequest.Response> connect() {
        protocol.start();
        return toInitialState()
                .thenCompose(r -> request().deviceType())
                .whenComplete((r, err) -> {
                    deviceType = r;
                    if (err != null) {
                        LOGGER.error("Ошибка при попытке подключения к устройству.", err);
                    }
                });
    }

    /**
     * Привести устройство в исходное состояние.
     * 
     * Для кассового аппарата вызов этого метода приводит к попытке установить
     * режим выбора на устройстве. Успешность этой операции зависит от текущего
     * режима и состояния устройства. 
     * 
     * @return объект, который оповещает об успешном или неуспешном завершении
     */
    public CompletableFuture<Void> toInitialState() {
        abort();
        return request().deviceState()
                .thenCompose(response -> {
                    CompletableFuture<Void> result;
                    if (response.getMode() != 0) {
                        CommandBuilder cb = null;
                        if (response.getBillState() != 0) {
                            cb = command().cancelBill();
                        }
                        if (cb == null) {
                            cb = command().resetMode();
                        } else {
                            cb = cb.resetMode();
                        }
                        if (response.getSubMode() != 0) {
                            cb = cb.resetMode();
                        }
                        result = cb.execute().thenAccept(r -> {
                            if (r.hasError()) {
                                LOGGER.warn("Ошибка приведение устройства в режим выбора: {}", r.getErrorMessage());
                                abort();
                            }
                        });
                    } else {
                        result = CompletableFuture.completedFuture(null);
                    }
                    return result;
                });
    }
    
    /**
     * Отключиться от устройства.
     */
    public void disconnect() {
        protocol.stop();
    }

    /**
     * Начать формирование цепочки команд для устройства.
     *
     * @return объект для формирования цепочки команд
     */
    public FirstCommandBuilder command() {
        return new FirstCommandBuilder(this);
    }
    
    /**
     * Выполнить запрос к устройству.
     *
     * @return объект для формирования запроса
     */
    public RequestBuilder request() {
        return new RequestBuilder(this);
    }

    /**
     * Получить тип устройства.
     *
     * @return тип устройства
     */
    public DeviceTypeRequest.Response getDeviceType() {
        return deviceType;
    }
    
    /**
     * Посылает устройству команду на очистку очереди задач.
     */
    public void abort() {
        if (protocol instanceof TransportProtocolV3) {
            ((TransportProtocolV3) protocol).abort();
        } else {
            LOGGER.warn("Транспортный протокол не поддерживает очистку очереди задач.");
        }
    }

    /**
     * Отправляет бинарное представление команды устройству предварительно
     * добавляя к ней пароль.
     *
     * @param command бинарное представление команды
     * @return бинарное представление ответа устройства
     */
    public CompletableFuture<byte[]> execute(byte... command) {
        byte[] payload = new byte[password.length + command.length];
        System.arraycopy(password, 0, payload, 0, password.length);
        System.arraycopy(command, 0, payload, password.length, command.length);
        return protocol.send(payload);
    }
    
    /**
     * Выполняет команду и возвращает результат выполнения.
     *
     * @param <C> тип команды
     * @param <R> тип результата выполнения команды
     * @param command команда
     * @return результат выполнения команды
     */
    public <C extends Command<R>, R extends Result> CompletableFuture<R> execute(C command) {
        return command.executeOn(this).thenCompose(r -> {
            if (r.hasError()) {
                return this.toInitialState().thenApply(a -> r);
            } else {
                return CompletableFuture.completedFuture(r);
            }
        });
    }

}
