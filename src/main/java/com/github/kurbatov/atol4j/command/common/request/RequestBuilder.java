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
package com.github.kurbatov.atol4j.command.common.request;

import com.github.kurbatov.atol4j.CashRegister;
import com.github.kurbatov.atol4j.command.Result;
import com.github.kurbatov.atol4j.command.common.request.fiscal.FiscalDataExchangeStateRequest;
import com.github.kurbatov.atol4j.command.common.request.fiscal.FiscalMemoryExpirationRequest;
import com.github.kurbatov.atol4j.command.common.request.fiscal.FiscalMemoryNumberRequest;
import com.github.kurbatov.atol4j.command.common.request.fiscal.ShiftStateRequest;
import java.util.concurrent.CompletableFuture;

/**
 * Выполняет запрос к ККТ и возвращает результат.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public class RequestBuilder {
    
    private final CashRegister device;

    public RequestBuilder(CashRegister device) {
        this.device = device;
    }
    
    /**
     * Запросить тип устройства.
     *
     * @return тип устройства
     */
    public CompletableFuture<DeviceTypeRequest.Response> deviceType() {
        return device.execute(DeviceTypeRequest.INSTANCE);
    }
    
    /**
     * Запросить текущее состояние устройства.
     *
     * @return состояние устройства
     */
    public CompletableFuture<DeviceStateRequest.Response> deviceState() {
        return device.execute(DeviceStateRequest.INSTANCE);
    }
    
    /**
     * Запросить количество наличных в денежном ящике.
     *
     * @return ответ от устройства с количеством наличных в минимальных денежных
     * единицах
     */
    public CompletableFuture<CashRequest.Response> cash() {
        return device.execute(CashRequest.INSTANCE);
    }
    
    /**
     * Запросить последний сменный итог.
     *
     * @return последний сменный итог
     */
    public CompletableFuture<LastShiftResultRequest.Response> lastShiftResult() {
        return device.execute(LastShiftResultRequest.INSTANCE);
    }
    
    /**
     * Запросить версию прошивки.
     *
     * @param source подсистема, которая должна сформировать ответ (процессор или загрузочный блок)
     * @return обыект с описанием версии прошивки
     */
    public CompletableFuture<FirmwareVersionRequest.Response> firmwareVersion(byte source) {
        return device.execute(new FirmwareVersionRequest(source));
    }
    
    /**
     * Запросить версию прошивки процессора.
     *
     * @return обыект с описанием версии прошивки
     */
    public CompletableFuture<FirmwareVersionRequest.Response> firmwareVersionCPU() {
        return firmwareVersion(FirmwareVersionRequest.SOURCE_CPU);
    }
    
    /**
     * Запросить версию прошивки загрузочного блока.
     *
     * @return обыект с описанием версии прошивки
     */
    public CompletableFuture<FirmwareVersionRequest.Response> firmwareVersionBootblock() {
        return firmwareVersion(FirmwareVersionRequest.SOURCE_BOOTBLOCK);
    }
    
    /**
     * Запросить значение регистра (считать регистр).
     *
     * @param register номер регистра
     * @param params параметры
     * @return значение регистра
     */
    public CompletableFuture<RegisterValueRequest.Response> registerValue(byte register, byte... params) {
        return device.execute(new RegisterValueRequest(register, params));
    }
    
    /**
     * Запросить код текущего состояния устройства.
     *
     * @return состояние устройства
     */
    public CompletableFuture<DeviceStateCodeRequest.Response> deviceStateCode() {
        return device.execute(DeviceStateCodeRequest.INSTANCE);
    }
    
    /**
     * Запросить состояние активации кода защиты по номеру.
     *
     * @param codeNumber номер кода защиты
     * @return состояние активации
     */
    public CompletableFuture<SecurityCodeStateRequest.Response> securityCodeState(byte codeNumber) {
        return device.execute(new SecurityCodeStateRequest(codeNumber));
    }

    /**
     * Запросить последнюю ошибку устройства.
     *
     * @return объект, содержащий последнюю ошибку
     */
    public CompletableFuture<Result> lastError() {
        // способ выполнения команды отличается чтобы обойти автоматическое
        // восстановление состояния после получения ошибки
        return LastErrorRequest.INSTANCE.executeOn(device);
    }
    
    /**
     * Запросить состояние смены.
     *
     * @return состояние смены
     */
    public CompletableFuture<ShiftStateRequest.Response> shiftState() {
        return device.execute(ShiftStateRequest.INSTANCE);
    }
    
    /**
     * Запросить состояние обмена данными с оператором фискальных данных (ОФД).
     *
     * @return состояние обмена данными с ОФД
     */
    public CompletableFuture<FiscalDataExchangeStateRequest.Response> fiscalDataExchangeState() {
        return device.execute(FiscalDataExchangeStateRequest.INSTANCE);
    }
    
    /**
     * Запросить номер фискального накопителя установленного в ККТ.
     *
     * @return номер фискального накопителя
     */
    public CompletableFuture<FiscalMemoryNumberRequest.Response> fiscalMemoryNumber() {
        return device.execute(FiscalMemoryNumberRequest.INSTANCE);
    }
    
    /**
     * Запросить срок действия фискального накопителя установленного в ККТ.
     *
     * @return срок действия фискального накопителя
     */
    public CompletableFuture<FiscalMemoryExpirationRequest.Response> fiscalMemoryExpiration() {
        return device.execute(FiscalMemoryExpirationRequest.INSTANCE);
    }

}
