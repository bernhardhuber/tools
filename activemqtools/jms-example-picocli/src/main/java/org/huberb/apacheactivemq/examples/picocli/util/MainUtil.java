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
package org.huberb.apacheactivemq.examples.picocli.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.huberb.apacheactivemq.examples.picocli.util.EntryKeyComparator.EntryKeySorter;
import org.huberb.apacheactivemq.examples.picocli.util.MainUtil.ActiveMqConnectionFactorySubCommand;
import org.huberb.apacheactivemq.examples.picocli.util.MainUtil.EnvPropertiesSubCommand;
import org.huberb.apacheactivemq.examples.picocli.util.MainUtil.LongToDateSubCommand;
import org.huberb.apacheactivemq.examples.picocli.util.MainUtil.SystemPropertiesSubCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 *
 * @author pi
 */
@Command(name = "MainUtil",
        subcommands = {
            LongToDateSubCommand.class,
            SystemPropertiesSubCommand.class,
            EnvPropertiesSubCommand.class,
            ActiveMqConnectionFactorySubCommand.class
        },
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        version = "MainUtil 0.1-SNAPSHOT",
        description = "some utility commands")
public class MainUtil implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(MainUtil.class);

    public static void main(String[] args) {

        final int exitCode = new CommandLine(new MainUtil()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }

    /**
     * Convert long value to Date and print it.
     *
     */
    @Command(name = "longToDate",
            description = "Convert long values to Date value and print both values")
    public static class LongToDateSubCommand implements Callable<Integer> {

        @Parameters(arity = "1..*", paramLabel = "<long>", description = "long value to be converted to Date")
        private Long[] longValues;

        @Override
        public Integer call() throws Exception {
            if (longValues != null && longValues.length > 0) {
                for (int i = 0; i < longValues.length; i++) {
                    Long l = longValues[i];
                    if (l != null) {
                        String lAsDateFormatted = convertLongToDate(l);
                        logger.info(String.format("#%d, long %d, date %s", i, l, lAsDateFormatted));
                    }
                }
            }
            return 0;
        }

        String convertLongToDate(long l) {
            Date d = new Date(l);
            String pattern = "yyyy-MM-dd HH:mm:ss,S";
            Locale locale = Locale.getDefault();
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
            String result = sdf.format(d);
            return result;
        }
    }

    /**
     * Print java system properties.
     */
    @Command(name = "systemProperties",
            description = "print system properties")
    public static class SystemPropertiesSubCommand implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            final Properties props = System.getProperties();
            // sort properties an print them
            final List<Entry<Object, Object>> propsSortedList = new EntryKeySorter().sortProperties(props);
            propsSortedList.forEach((Entry<Object, Object> e) -> {
                logger.info(String.format("%s: %s", e.getKey(), e.getValue()));
            });
            return 0;
        }
    }

    /**
     * Print java environment variables.
     */
    @Command(name = "envProperties",
            description = "print env properties")
    public static class EnvPropertiesSubCommand implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            final Map<String, String> envMap = System.getenv();
            // sort properties an print them
            final List<Entry<String, String>> propsSortedList = new EntryKeySorter().sortMapEntries(envMap);
            propsSortedList.forEach((Entry<String, String> e) -> {
                logger.info(String.format("%s: %s", e.getKey(), e.getValue()));
            });
            return 0;
        }
    }

    /**
     * Print default ActiveMQConnectionFactory properties.
     */
    @Command(name = "activeMqConnectionFactory",
            description = "print activemq properties")
    public static class ActiveMqConnectionFactorySubCommand implements Callable<Integer> {

        final String userName = "admin";
        final String password = "password";
        final String brokerURL = "tcp://localhost:61616";

        @Override
        public Integer call() throws Exception {
            //---
            final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(userName, password, brokerURL);

            final Properties props = new Properties();
            activeMQConnectionFactory.populateProperties(props);

            // sort properties an print them
            final List<Entry<Object, Object>> propsSortedList = new EntryKeySorter().sortProperties(props);
            propsSortedList.forEach((Entry<Object, Object> e) -> {
                logger.info(String.format("%s: %s", e.getKey(), e.getValue()));
            });
            return 0;
        }

    }

}
