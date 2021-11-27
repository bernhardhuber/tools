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
package org.huberb.apacheactivemq.examples.picocli.jms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple converter, from string of format "key1=value1;key2=value2;..." to a
 * java {@link Map}.
 *
 */
public class MessagePropertyAsStringConverter {

    /**
     * Convert String having format "key1=value1;key2=value2;.." to a map of
     * {key1:value1, key2:value2,...} .
     *
     * @param singleMessagPropertyString
     * @return a map of key-value entries
     */
    public Map<String, Object> messagePropertyFromStringConverter(String singleMessagPropertyString) {
        final Map<String, Object> m = new HashMap<>();
        if (singleMessagPropertyString != null && !singleMessagPropertyString.isBlank()) {
            final String[] singleMessagPropertyStringSplitted = singleMessagPropertyString.split(";");
            if (singleMessagPropertyStringSplitted != null && singleMessagPropertyStringSplitted.length > 0) {
                for (String keyValueAsString : Arrays.asList(singleMessagPropertyStringSplitted)) {
                    final String[] keyValueArray = keyValueAsString.split("=");
                    if (keyValueArray != null && keyValueArray.length == 2) {
                        final String key = keyValueArray[0];
                        final String value = keyValueArray[1];
                        final boolean keyIsValid = key != null && !key.isBlank();
                        final boolean valueIsValid = value != null && !value.isBlank();
                        if (keyIsValid && valueIsValid) {
                            m.put(key.trim(), value.trim());
                        }
                    }
                }
            }
        }
        return m;
    }

}
