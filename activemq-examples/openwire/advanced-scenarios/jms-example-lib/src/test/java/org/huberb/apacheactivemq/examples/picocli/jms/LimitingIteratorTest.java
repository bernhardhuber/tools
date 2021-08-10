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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pi
 */
public class LimitingIteratorTest {

    private final List<String> data = Arrays.asList("a", "b", "c");

    public LimitingIteratorTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of hasNext method, of class LimitingIterator.
     */
    @Test
    public void testHasNext() {
        LimitingIterator<String> instance;
        instance = new LimitingIterator<>(data.iterator(), 5);
        assertTrue(instance.hasNext());
        assertEquals(0, instance.currentIterationCount());

    }

    /**
     * Test of next method, of class LimitingIterator.
     */
    @Test
    public void testNext() {
        LimitingIterator<String> instance;
        instance = new LimitingIterator<>(data.iterator(), 5);

        assertTrue(instance.hasNext());
        assertEquals(0, instance.currentIterationCount());
        assertEquals("a", instance.next());
        assertTrue(instance.hasNext());
        assertEquals(1, instance.currentIterationCount());
    }

    /**
     * Test of currentIterationCount method, of class LimitingIterator.
     */
    @Test
    public void testForEachRemaining() {
        LimitingIterator<String> instance;

        final Object[] dataMaxCountExpectedIterationsCount = {
            -2, 3, "a b c",
            -1, 3, "a b c",
            0, 0, "",
            1, 1, "a",
            2, 2, "a b",
            3, 3, "a b c",
            4, 3, "a b c",
            5, 3, "a b c",};
        for (int i = 0; i < dataMaxCountExpectedIterationsCount.length; i += 3) {
            int maxCount = (int) dataMaxCountExpectedIterationsCount[i];
            int expectedIterationsCount = (int) dataMaxCountExpectedIterationsCount[i + 1];
            String expectedResult = (String) dataMaxCountExpectedIterationsCount[i + 2];
            final String m = String.format("i %d, maxCount %d, expectedIterationsCount %d, expectedResult %s", i,
                    maxCount, expectedIterationsCount, expectedResult);
            //---
            instance = new LimitingIterator<>(data.iterator(), maxCount);
            final AtomicInteger atomicInteger = new AtomicInteger(0);
            final StringBuilder sb = new StringBuilder();
            instance.forEachRemaining((String s) -> {
                atomicInteger.incrementAndGet();
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(s);
            });

            assertEquals(expectedIterationsCount, atomicInteger.intValue(), m);
            assertEquals(expectedResult, sb.toString(), m);
        }
    }

}
