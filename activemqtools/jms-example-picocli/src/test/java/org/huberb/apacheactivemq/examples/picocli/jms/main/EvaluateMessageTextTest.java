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
package org.huberb.apacheactivemq.examples.picocli.jms.main;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pi
 */
public class EvaluateMessageTextTest {

    EvaluateMessageText instance;

    public EvaluateMessageTextTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of createForCapturingStdin method, of class EvaluateMessageText.
     */
    @Test
    public void testCreateForCapturingStdin() throws UnsupportedEncodingException {
        final String s = "abc";
        final ByteArrayInputStream newSytemIn = new ByteArrayInputStream(s.getBytes("UTF-8"));
        InputStream systemInSave = System.in;
        try {
            System.setIn(newSytemIn);
            instance = EvaluateMessageText.createForCapturingStdin();
            String result = instance.build();
            assertEquals(s, result.trim());
        } finally {
            System.setIn(systemInSave);
        }
    }

    /**
     * Test of createForCapturingString method, of class EvaluateMessageText.
     */
    @Test
    public void testCreateForCapturingString() {
        String s = "abc";
        instance = EvaluateMessageText.createForCapturingString(s);
        String result = instance.build();
        assertEquals(s, result);
    }

    /**
     * Test of createForCapturingFile method, of class EvaluateMessageText.
     */
    @Test
    @Disabled(value = "not implemented yet")
    public void testCreateForCapturingFile() {
    }

    /**
     * Test of captureFromStdin method, of class EvaluateMessageText.
     */
    @Test
    @Disabled(value = "not implemented yet")
    public void testCaptureFromStdin() throws Exception {
    }

    /**
     * Test of captureFromStdinViaScanner method, of class EvaluateMessageText.
     */
    @Test
    @Disabled(value = "not implemented yet")
    public void testCaptureFromStdinViaScanner() {
    }

    /**
     * Test of captureFromStdinViaBufferedReader method, of class
     * EvaluateMessageText.
     */
    @Test
    @Disabled(value = "not implemented yet")
    public void testCaptureFromStdinViaBufferedReader() throws Exception {
    }

    /**
     * Test of captureFromString method, of class EvaluateMessageText.
     */
    @Test
    @Disabled(value = "not implemented yet")
    public void testCaptureFromString() {
    }

    /**
     * Test of captureFromFile method, of class EvaluateMessageText.
     */
    @Test
    @Disabled(value = "not implemented yet")
    public void testCaptureFromFile() throws Exception {
    }

}
