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

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Wrap DbUnit supported data formats as enum.
 *
 * @author pi
 */
class Formats {

    //    public static final String FORMAT_FLAT = "flat";
    //    public static final String FORMAT_XML = "xml";
    //    public static final String FORMAT_DTD = "dtd";
    //    public static final String FORMAT_CSV = "csv";
    //    public static final String FORMAT_XLS = "xls";
    //
    public enum Format {
        FLAT, XML, DTD, CSV, XLS;

        static Optional<Format> findFormatBy(String format) {
            final Optional<Format> foundFormat = Arrays.asList(Format.values())
                    .stream()
                    .filter((Format f) -> f.name().equalsIgnoreCase(format))
                    .findFirst();
            return foundFormat;
        }

        static Optional<Format> findFormatAndConsume(String format, Consumer<Format> c) {
            final Optional<Format> formatOpt = findFormatBy(format);
            if (formatOpt.isPresent()) {
                c.accept(formatOpt.get());
            }
            return formatOpt;
        }
    }

}
