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

import org.huberb.services.dbunit.picocli.Formats.Format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pi
 */
public class FormatsTest {

    @Test
    public void test_Format_findFormatBy() {
        assertEquals(Format.CSV, Format.findFormatBy(Format.CSV.name()).get());
        assertEquals(Format.DTD, Format.findFormatBy(Format.DTD.name()).get());
        assertEquals(Format.FLAT, Format.findFormatBy(Format.FLAT.name()).get());
        assertEquals(Format.XLS, Format.findFormatBy(Format.XLS.name()).get());
        assertEquals(Format.XML, Format.findFormatBy(Format.XML.name()).get());
        assertEquals(false, Format.findFormatBy("XXX").isPresent());
    }

    @Test
    public void test_Format_findFormatAndConsume() {
        assertEquals(Format.CSV, Format.findFormatAndConsume(Format.CSV.name(), (Format t) -> assertEquals(Format.CSV, t)).get());
        assertEquals(Format.DTD, Format.findFormatAndConsume(Format.DTD.name(), (Format t) -> assertEquals(Format.DTD, t)).get());
        assertEquals(Format.FLAT, Format.findFormatAndConsume(Format.FLAT.name(), (Format t) -> assertEquals(Format.FLAT, t)).get());
        assertEquals(Format.XLS, Format.findFormatAndConsume(Format.XLS.name(), (Format t) -> assertEquals(Format.XLS, t)).get());
        assertEquals(Format.XML, Format.findFormatAndConsume(Format.XML.name(), (Format t) -> assertEquals(Format.XML, t)).get());

        assertEquals(false, Format.findFormatAndConsume("XXX", (Format t) -> fail("Not expected to consume Format XXX")).isPresent());
    }

}
