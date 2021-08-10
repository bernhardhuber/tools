/*
 * Copyright 2021 pi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.huberb.services.dbunit.picocli;

import java.util.concurrent.Callable;
import picocli.CommandLine;

/**
 * Help sub-command showing logging configuration details.
 * 
 * @author pi
 */
@CommandLine.Command(name = "about-logging",
        helpCommand = true,
        description = "Display logging configuration options")
class AboutLogging implements Callable<Integer> {

    private final String descriptionText = "Logging configuration:%n"
            + "Simple implementation of Logger that sends all enabled log messages, for all defined loggers, to the console (System.err).%n"
            + "%n"
            + "The following system properties are supported to configure the behavior of this logger:%n"
            + "@|bold org.slf4j.simpleLogger.logFile |@ - The output target which can be the path to a file, or the special values \"System.out\" and \"System.err\". Default is \"System.err\".%n"
            + "@|bold org.slf4j.simpleLogger.cacheOutputStream |@ - If the output target is set to \"System.out\" or \"System.err\" (see preceding entry), by default, %n"
            + "  logs will be output to the latest value referenced by System.out/err variables.%n"
            + "  By setting this parameter to true, the output stream will be cached, i.e. assigned once at initialization time and re-used independently of the current value referenced by System.out/err.%n"
            + "@|bold org.slf4j.simpleLogger.defaultLogLevel |@ - Default log level for all instances of SimpleLogger. Must be one of (\"trace\", \"debug\", \"info\", \"warn\", \"error\" or \"off\").%n"
            + "  If not specified, defaults to \"info\".%n" + "@|bold org.slf4j.simpleLogger.log.a.b.c |@ - Logging detail level for a SimpleLogger instance named \"a.b.c\". Right-side value must be one of \"trace\", \"debug\", \"info\", \"warn\", \"error\" or \"off\".%n"
            + "  When a SimpleLogger named \"a.b.c\" is initialized, its level is assigned from this property.%n"
            + "  If unspecified, the level of nearest parent logger will be used, and if none is set, then the value specified by org.slf4j.simpleLogger.defaultLogLevel will be used.%n"
            + "@|bold org.slf4j.simpleLogger.showDateTime |@ - Set to true if you want the current date and time to be included in output messages. Default is false%n"
            + "@|bold org.slf4j.simpleLogger.dateTimeFormat |@ - The date and time format to be used in the output messages. The pattern describing the date and time format is defined by SimpleDateFormat.%n"
            + "  If the format is not specified or is invalid, the number of milliseconds since start up will be output.%n"
            + "@|bold org.slf4j.simpleLogger.showThreadName |@ - Set to true if you want to output the current thread name. Defaults to true.%n"
            + "@|bold org.slf4j.simpleLogger.showLogName |@ - Set to true if you want the Logger instance name to be included in output messages. Defaults to true.%n"
            + "@|bold org.slf4j.simpleLogger.showShortLogName |@ - Set to true if you want the last component of the name to be included in output messages. Defaults to false.%n"
            + "@|bold org.slf4j.simpleLogger.levelInBrackets |@ - Should the level string be output in brackets? Defaults to false.%n"
            + "@|bold org.slf4j.simpleLogger.warnLevelString |@ - The string value output for the warn level. Defaults to WARN.%n"
            + "%n"
            + "In addition to looking for system properties with the names specified above, this implementation also checks for a class loader resource named \"simplelogger.properties\", and includes any matching definitions from this resource (if it exists).%n"
            + "%n"
            + "With no configuration, the default output includes the relative time in milliseconds, thread name, the level, logger name, and the message followed by the line separator for the host.%n"
            + "In log4j terms it amounts to the \"%%r [%%t] %%level %%logger - %%m%%n\" pattern.%n"
            + "%n";

    @Override
    public Integer call() throws Exception {
        final String descriptionTextAnsi = CommandLine.Help.Ansi.AUTO.string(descriptionText);
        System.err.printf(descriptionTextAnsi);
        return 0;
    }

}
