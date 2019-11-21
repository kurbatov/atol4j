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
package com.github.kurbatov.atol4j.command.select;

import com.github.kurbatov.atol4j.command.CommandBuilder;
import com.github.kurbatov.atol4j.command.common.CommonCommandBuilder;
import com.github.kurbatov.atol4j.command.common.ResetModeCommand;
import com.github.kurbatov.atol4j.command.common.SetModeCommand;
import com.github.kurbatov.atol4j.command.registration.RegistrationCommandBuilder;
import com.github.kurbatov.atol4j.command.report.ReportCommandBuilder;

/**
 * Добавляет в цепочку команды режима выбора.
 *
 * @author Олег Курбатов &lt;o.v.kurbatov@gmail.com&gt;
 */
public interface SelectCommandBuilder extends CommonCommandBuilder<CommandBuilder> {
    
    /**
     * Устанавливает режим работы устройства.
     *
     * Режимы работы устройства:
     * <ol>
     * <li>Режим регистрации {@link  SetModeCommand#REGISTRATION}</li>
     * <li>Режим отчётов без гашения
     * {@link  SetModeCommand#REPORT_WOT_CANCEL}</li>
     * <li>Режим отчётов с гашением
     * {@link  SetModeCommand#REPORT_WITH_CANCEL}</li>
     * <li>Режим программирования {@link  SetModeCommand#CONFIGURATION}</li>
     * <li>Режим ввода заводского номера/доступа к ППД
     * {@link  SetModeCommand#FISCAL_MEMORY_ACCESS}</li>
     * <li>Режим доступа к ФД
     * {@link  SetModeCommand#SECURE_FISCAL_MEMORY_ACCESS}</li>
     * </ol>
     *
     * @param mode режим работы
     * @param password пароль оператора
     * @return объект для формирования цепочки команд
     */
    default CommandBuilder setMode(byte mode, byte... password) {
        return append(new SetModeCommand(mode, password));
    }
    
    @Override
    default CommandBuilder resetMode() {
        return append(ResetModeCommand.INSTANCE);
    }

    /**
     * Перейти в режим регистрации.
     *
     * @param password пароль оператора
     * @return объект для формирования цепочки команд
     */
    RegistrationCommandBuilder registration(byte... password);
    
    /**
     * Перейти в режим отчётов.
     *
     * @param password пароль оператора
     * @return объект для формирования цепочки команд
     */
    ReportCommandBuilder report(byte... password);
    
    /**
     * Печать демонстрационного чека.
     *
     * @param type тип чека
     * @param test выполнить команду в тестовом режиме
     * @return объект для формирования цепочки команд
     */
    default CommandBuilder printDemo(byte type, boolean test) {
        return append(new DemoPrintCommand(type, test));
    }
    
    /**
     * Печать демонстрационного чека.
     *
     * @return объект для формирования цепочки команд
     */
    default CommandBuilder printDemo() {
        return printDemo(DemoPrintCommand.DEMO, false);
    }
    
    /**
     * Печать альтернативного демонстрационного чека.
     *
     * @return объект для формирования цепочки команд
     */
    default CommandBuilder printDemoAlt() {
        return printDemo(DemoPrintCommand.DEMO_ALT, false);
    }
    
    /**
     * Печать информации о ККТ.
     *
     * @return объект для формирования цепочки команд
     */
    default CommandBuilder printInfo() {
        return printDemo(DemoPrintCommand.INFO, false);
    }
    
    /**
     * Печать диагностики соединения с ОФД.
     *
     * @return объект для формирования цепочки команд
     */
    default CommandBuilder printDiagnostics() {
        return printDemo(DemoPrintCommand.DIAGNOSTICS, false);
    }
    
    /**
     * Произвести технологическое обнуление ККТ.
     *
     * @return объект для формирования цепочки команд
     */
    default CommandBuilder technologicalReset() {
        return append(ResetCommand.INSTANCE);
    }
    
    /**
     * Произвести инициализацию таблиц начальными значениями.
     *
     * @return объект для формирования цепочки команд
     */
    default CommandBuilder initTables() {
        return append(TableInitializationCommand.INSTANCE);
    }
    
    /**
     * Ввести код защиты.
     *
     * @param number номер кода защиты
     * @param code код защиты
     * @return объект для формирования цепочки команд
     */
    default CommandBuilder enterDefenceCode(byte number, byte[] code) {
        return append(new EnterDefenceCodeCommand(number, code));
    }
    
}
