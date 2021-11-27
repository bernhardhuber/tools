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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pi
 */
public class EntryKeyComparatorTest {

    public EntryKeyComparatorTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of compare method, of class EntryKeyComparator.
     */
    @Test
    public void testCompare() {
        final Map<Object, String> m = new HashMap<>();
        m.put("a", "av");
        m.put("c", "cv");
        m.put("b", "bv");
        final EntryKeyComparator<Object, String> instance = new EntryKeyComparator<>();
        final List<Map.Entry<Object, String>> l = new ArrayList<>();
        l.addAll(m.entrySet());
        l.sort(instance);

        assertEquals("a:av", l.get(0).getKey() + ":" + l.get(0).getValue());
        assertEquals("b:bv", l.get(1).getKey() + ":" + l.get(1).getValue());
        assertEquals("c:cv", l.get(2).getKey() + ":" + l.get(2).getValue());
    }

    /**
     * Test of compare method, of class EntryKeyComparator.
     */
    @Test
    public void testCompareUsingStreams() {
        final Map<Object, String> m = new HashMap<>();
        m.put("a", "av");
        m.put("c", "cv");
        m.put("b", "bv");
        final EntryKeyComparator<Object, String> instance = new EntryKeyComparator<>();

        final List<Map.Entry<Object, String>> l = m.entrySet()
                .stream().collect(Collectors.toList())
                .stream().sorted(instance).collect(Collectors.toList());

        assertEquals("a:av", l.get(0).getKey() + ":" + l.get(0).getValue());
        assertEquals("b:bv", l.get(1).getKey() + ":" + l.get(1).getValue());
        assertEquals("c:cv", l.get(2).getKey() + ":" + l.get(2).getValue());
    }

}
