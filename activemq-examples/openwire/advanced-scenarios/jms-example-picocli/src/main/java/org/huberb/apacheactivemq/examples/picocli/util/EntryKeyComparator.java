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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Compare {@link Entry} key values as String.
 */
class EntryKeyComparator<K extends Object, V> implements Comparator<Map.Entry<K, V>> {

    @Override
    public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
        final int result;
        final boolean o1NotNullAndString_class = o1 != null && o1.getKey() instanceof String;
        final boolean o2NotNullAndString_class = o2 != null && o2.getKey() instanceof String;
        if (o1NotNullAndString_class && o2NotNullAndString_class) {
            final String co1 = (String) o1.getKey();
            final String co2 = (String) o2.getKey();
            result = co1.compareTo(co2);
        } else {
            result = 0;
        }
        return result;
    }

    static class EntryKeySorter {

        List<Map.Entry<Object, Object>> sortProperties(Properties props) {
            final Comparator<Map.Entry<Object, Object>> comparator = new EntryKeyComparator<>();
            final List<Map.Entry<Object, Object>> propsSortedList = props.entrySet()
                    .stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
            return propsSortedList;
        }

        List<Map.Entry<String, String>> sortMapEntries(Map<String, String> props) {
            final Comparator<Map.Entry<String, String>> comparator = new EntryKeyComparator<String, String>();
            final List<Map.Entry<String, String>> mapSortedList = props.entrySet()
                    .stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
            return mapSortedList;
        }
    }
}
