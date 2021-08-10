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
package org.huberb.services.h2.support;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.h2.tools.Csv;
import static org.huberb.services.h2.support.OutputResultSet.OutputMode.CSV;
import static org.huberb.services.h2.support.OutputResultSet.OutputMode.JSON;
import static org.huberb.services.h2.support.OutputResultSet.OutputMode.JSON_ARRAYS;
import static org.huberb.services.h2.support.OutputResultSet.OutputMode.JSON_MAPS;
import static org.huberb.services.h2.support.OutputResultSet.OutputMode.RAW;
import static org.huberb.services.h2.support.OutputResultSet.OutputMode.TABULAR;
import static org.huberb.services.h2.support.OutputResultSet.OutputMode.YAML;
import org.huberb.services.h2.support.OutputResultSet.ResultSetIterator.DefaultResulSetConsumer;

/**
 *
 * @author pi
 */
public class OutputResultSet {

    static String encodeJson(String s) {
        if (s == null) {
            return "";
        }
        final String encoded = s
                .replace("\\", "\\" + "\\")
                .replace("\"", "\\" + "\"")
                .replace("/", "\\" + "/")
                .replace("\b", "\\" + "b")
                .replace("\f", "\\" + "f")
                .replace("\n", "\\" + "n")
                .replace("\r", "\\" + "r")
                .replace("\t", "\\" + "t");
        return encoded;
    }

    static String encodeYaml(String s) {
        if (s == null) {
            return "";
        }
        final String encoded = s
                .replace("\\", "\\" + "\\")
                .replace("\"", "\\" + "\"")
                .replace("/", "\\" + "/")
                .replace("\b", "\\" + "b")
                .replace("\f", "\\" + "f")
                .replace("\n", "\\" + "n")
                .replace("\r", "\\" + "r")
                .replace("\t", "\\" + "t");
        return encoded;
    }

    public static interface OutputBy {

        void output(ResultSet rs, PrintStream out) throws Exception;

    }

    static class OutputByRaw implements OutputBy {

        @Override
        public void output(ResultSet rs, PrintStream out) throws SQLException {
            final Consumer<Map<String, String>> c = (Map<String, String> m) -> {
                m.entrySet().
                        stream().
                        forEach((e) -> {
                            out.printf("%s: %s%n", e.getKey(), e.getValue());
                        });
                out.println();
            };
            final DefaultResulSetConsumer drsc = new DefaultResulSetConsumer(c);
            new ResultSetIterator().iterate(rs, drsc);
        }
    }

    static class OutputByCsv implements OutputBy {

        @Override
        public void output(ResultSet rs, PrintStream out) throws SQLException, IOException {
            final StringWriter sw = new StringWriter();
            try (sw) {
                final Csv csv = new Csv();
                csv.write(sw, rs);
            }
            out.println(sw.toString());
        }
    }

    static class OutputByJson implements OutputBy {

        @Override
        public void output(ResultSet rs, PrintStream out) throws SQLException {
            final Consumer<Map<String, String>> c = new TabularMapArrayOfMapsConsumer(out);
            final String jsonStart = "[\n";
            out.print(jsonStart);
            final DefaultResulSetConsumer drsc = new DefaultResulSetConsumer(c);
            new ResultSetIterator().iterate(rs, drsc);

            final String jsonEnd = "\n]\n";
            out.print(jsonEnd);

        }

        static class TabularMapArrayOfMapsConsumer implements Consumer<Map<String, String>> {

            int rowCount = 0;
            private final PrintStream out;

            public TabularMapArrayOfMapsConsumer(PrintStream out) {
                this.out = out;
            }

            @Override
            public void accept(Map<String, String> m) {
                final Function<Map.Entry<String, String>, String> f = (Map.Entry<String, String> mapEntry) -> {
                    final String keyValueAsYaml = String.format("\"%s\": \"%s\"",
                            encodeJson(mapEntry.getKey()),
                            encodeJson(mapEntry.getValue())
                    );
                    return keyValueAsYaml;
                };

                final StringBuilder sb = new StringBuilder();
                final String joiningDelimiter = ", ";
                if (rowCount > 0) {
                    sb.append(", \n");
                }
                sb.append("{");
                sb.append(m.entrySet()
                        .stream()
                        .map(f)
                        .collect(Collectors.joining(joiningDelimiter)));
                sb.append("}");
                out.print(sb.toString());
                rowCount += 1;
            }

        }
    }

    static class OutputByArrayOfArraysJson implements OutputBy {

        @Override
        public void output(ResultSet rs, PrintStream out) throws SQLException {
            final Consumer<Map<String, String>> c = new TabularMapArrayOfArraysConsumer(out);
            final String jsonStart = "[\n";
            out.print(jsonStart);
            final DefaultResulSetConsumer drsc = new DefaultResulSetConsumer(c);
            new ResultSetIterator().iterate(rs, drsc);

            final String jsonEnd = "\n]\n";
            out.print(jsonEnd);

        }

        static class TabularMapArrayOfArraysConsumer implements Consumer<Map<String, String>> {

            int rowCount = 0;
            private final PrintStream out;

            public TabularMapArrayOfArraysConsumer(PrintStream out) {
                this.out = out;
            }

            @Override
            public void accept(Map<String, String> m) {
                final Function<Map.Entry<String, String>, String> f = (Map.Entry<String, String> mapEntry) -> {
                    final String keyValueAsYaml = String.format("\"%s\": \"%s\"",
                            encodeJson(mapEntry.getKey()),
                            encodeJson(mapEntry.getValue())
                    );
                    return keyValueAsYaml;
                };

                final StringBuilder sb = new StringBuilder();
                final String joiningDelimiter = ", ";
                if (rowCount == 0) {
                    final String firstRow = m.keySet()
                            .stream()
                            .map((k) -> "\"" + encodeJson(k) + "\"")
                            .collect(Collectors.joining(joiningDelimiter)
                            );
                    sb.append("[");
                    sb.append(firstRow);
                    sb.append("]");
                }
                sb.append(", \n");
                final String valuesRow = m.values()
                        .stream()
                        .map((k) -> "\"" + encodeJson(k) + "\"")
                        .collect(Collectors.joining(joiningDelimiter)
                        );
                sb.append("[");
                sb.append(valuesRow);
                sb.append("]");

                out.print(sb.toString());
                rowCount += 1;
            }
        }
    }

    static class OutputByYaml implements OutputBy {

        @Override
        public void output(ResultSet rs, PrintStream out) throws SQLException {
            final TabularMapConsumer c = new TabularMapConsumer(out);
            final String yamlStart = ""
                    + "## YAML\n"
                    + "---\n"
                    + "[\n" + "";
            out.print(yamlStart);
            final DefaultResulSetConsumer drsc = new DefaultResulSetConsumer(c);
            new ResultSetIterator().iterate(rs, drsc);

            final String yamlEnd = "" + "\n]\n";
            out.print(yamlEnd);
        }

        static class TabularMapConsumer implements Consumer<Map<String, String>> {

            int rowCount = 0;
            private final PrintStream out;

            public TabularMapConsumer(PrintStream out) {
                this.out = out;
            }

            @Override
            public void accept(Map<String, String> m) {
                final Function<Map.Entry<String, String>, String> f = (Map.Entry<String, String> mapEntry) -> {
                    final String keyValueAsYaml = String.format("\"%s\": \"%s\"",
                            encodeYaml(mapEntry.getKey()),
                            encodeYaml(mapEntry.getValue())
                    );
                    return keyValueAsYaml;
                };

                final StringBuilder sb = new StringBuilder();
                final String joiningDelimiter = ", ";
                if (rowCount > 0) {
                    sb.append(", \n");
                }
                sb.append("{");
                sb.append(m.entrySet()
                        .stream()
                        .map(f)
                        .collect(Collectors.joining(joiningDelimiter)));
                sb.append("}");
                out.print(sb.toString());
                rowCount += 1;
            }

        }

    }

    static class OutputByTabular implements OutputBy {

        @Override
        public void output(ResultSet rs, PrintStream out) throws SQLException {
            final Consumer<Map<String, String>> c = new TabularMapConsumer(out);
            final DefaultResulSetConsumer drsc = new DefaultResulSetConsumer(c);
            new ResultSetIterator().iterate(rs, drsc);
        }

        static class TabularMapConsumer implements Consumer<Map<String, String>> {

            int rowCount = 0;
            private final PrintStream out;

            public TabularMapConsumer(PrintStream out) {
                this.out = out;
            }

            @Override
            public void accept(Map<String, String> m) {
                final StringBuilder sb = new StringBuilder();
                final String joiningDelimiter = ", ";
                if (rowCount == 0) {
                    sb.append(m.keySet().stream().collect(Collectors.joining(joiningDelimiter)));
                    sb.append("\n");
                }
                sb.append(m.values().stream().collect(Collectors.joining(joiningDelimiter)));
                sb.append("\n");
                out.print(sb.toString());
                rowCount += 1;
            }

        }
    }

    static class ResultSetIterator {

        void iterate(ResultSet rs, Consumer<ResultSet> c) {
            try (rs) {
                while (rs.next()) {
                    c.accept(rs);
                }

            } catch (SQLException sqlex) {
                throw new ResultSetProcessingRuntimeException("iterate", sqlex);
            }
        }

        static class DefaultResulSetConsumer implements Consumer<ResultSet> {

            final Consumer<Map<String, String>> c;

            public DefaultResulSetConsumer(Consumer<Map<String, String>> c) {
                this.c = c;
            }

            @Override
            public void accept(ResultSet rs) {
                try {
                    final Map<String, String> m = new TreeMap<>();
                    final ResultSetMetaData meta = rs.getMetaData();
                    for (int i = 0; i < meta.getColumnCount(); i++) {
                        m.put(meta.getColumnLabel(i + 1), rs.getString(i + 1));
                    }
                    c.accept(m);

                } catch (SQLException ex) {
                    throw new ResultSetProcessingRuntimeException("accept single ResultSet", ex);
                }
            }

        }

        static class ResultSetProcessingRuntimeException extends RuntimeException {

            public ResultSetProcessingRuntimeException(String message, Throwable cause) {
                super(message, cause);
            }

        }
    }

    public enum OutputMode {
        RAW, CSV, JSON, JSON_ARRAYS, JSON_MAPS, YAML, TABULAR;

        public static Optional<OutputMode> findOutputMode(String outputModeAsString) {
            Optional<OutputMode> foundOutputModeOpt = Arrays.asList(OutputMode.values())
                    .stream()
                    .filter((OutputMode om) -> {
                        return om.name().equals(outputModeAsString);
                    })
                    .findFirst();
            return foundOutputModeOpt;
        }

        public static OutputBy createOutputBy(OutputMode theOutputFormat) {
            final OutputBy result;
            if (RAW == theOutputFormat) {
                result = new OutputResultSet.OutputByRaw();
            } else if (CSV == theOutputFormat) {
                result = new OutputResultSet.OutputByCsv();
            } else if (JSON == theOutputFormat) {
                result = new OutputResultSet.OutputByJson();
            } else if (JSON_ARRAYS == theOutputFormat) {
                result = new OutputResultSet.OutputByArrayOfArraysJson();
            } else if (JSON_MAPS == theOutputFormat) {
                result = new OutputResultSet.OutputByJson();
            } else if (YAML == theOutputFormat) {
                result = new OutputResultSet.OutputByYaml();
            } else if (TABULAR == theOutputFormat) {
                result = new OutputResultSet.OutputByTabular();
            } else {
                result = new OutputResultSet.OutputByTabular();
            }

            return result;
        }
    }
}
