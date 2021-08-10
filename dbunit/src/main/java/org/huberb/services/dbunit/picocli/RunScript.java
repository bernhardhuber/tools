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

import org.huberb.services.dbunit.support.DelegatingNonClosingInputStream;
import org.huberb.services.dbunit.support.ConnectionFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.h2.util.ScriptReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/**
 * Subcommand for processing general sql commands.
 *
 * @author pi
 */
@CommandLine.Command(name = "run-script",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        description = "Run sql commands")
public class RunScript implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(RunScript.class);
    @CommandLine.ParentCommand
    private MainDbUnit mainDbUnit; // picocli injects reference to parent command

    @CommandLine.ArgGroup(exclusive = true, multiplicity = "1")
    Exclusive exclusive;
    @CommandLine.Option(names = {"--charset"},
            defaultValue = "UTF-8",
            paramLabel = "CHARSET",
            required = false,
            description = "input charset, eg UTF-8, ISO-8859-1")
    private String charset; // (for example 'UTF-8'),

    // make --src, and --stdin mutual exclusive
    static class Exclusive {

        @CommandLine.Option(names = {"--src"},
                paramLabel = "SOURCE",
                required = false,
                description = "Input file for reading sql commands")
        File src;
        @CommandLine.Option(names = {"--stdin"}, required = true)
        boolean stdin;
    }

    @Override
    public Integer call() throws Exception {
        //---
        final Properties jdbcProperties = this.mainDbUnit.createPropertiesJdbcConnection();
        final String schema = jdbcProperties.getProperty("schema", null);

        final Connection connection = new ConnectionFactory(jdbcProperties).createConnection();
        process(connection, schema);
        return 0;
    }

    //---
    void process(Connection conn, String schema) throws SQLException, FileNotFoundException, IOException {
        try (conn) {
            //conn.setAutoCommit(false);
            //conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            //---
            final Savepoint savepoint = conn.setSavepoint();
            //--
            final Reader reader = new ReaderFactory(this.charset, this.exclusive).createReaderFromSrcOrSystemIn();
            try (reader) {
                processSqlCommandsFromReader(reader, conn);
            }
            conn.commit();
        }
    }

    void processSqlCommandsFromReader(Reader r, Connection conn) throws SQLException {
        try (final ScriptReader scriptReader = new ScriptReader(r)) {
            scriptReader.setSkipRemarks(true);

            for (String sql; (sql = scriptReader.readStatement()) != null;) {
                final String sqlTrimmed = sql.trim();
                if (sqlTrimmed.isBlank()) {
                    continue;
                }
                logger.info("Execute sql '{}'", sqlTrimmed);
                try (final Statement stat = conn.createStatement()) {
                    final boolean hasResultSet = stat.execute(sqlTrimmed);
                    if (hasResultSet) {
                        try (ResultSet rs = stat.getResultSet()) {
                            resultSetConsumer(rs);
                        }
                    } else {
                        int updateCount = stat.getUpdateCount();
                        updateCountConsumer(updateCount);
                    }
                }
            }
        }
    }

    void resultSetConsumer(ResultSet rs) throws SQLException {
        final ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int columns = rs.getMetaData().getColumnCount();
        int rows = 0;

        while (rs.next()) {
            if (rows == 0) {
                final StringBuilder sb = new StringBuilder();
                for (int i = 0; i < columns; i++) {
                    String colLabel = resultSetMetaData.getColumnLabel(i + 1);
                    sb.append(colLabel).append(",");
                }
                System.out.printf("%3d: %s%n", rows, sb.toString());
            }
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < columns; i++) {
                String colValue = rs.getString(i + 1);
                sb.append(colValue).append(",");
            }
            rows += 1;
            System.out.printf("%3d: %s%n", rows, sb.toString());
        }
    }

    void updateCountConsumer(int updateCount) {
        System.out.printf("Update count %d%n", updateCount);
    }

    /**
     * Create a {@link Reader}, reading from a file or stdin.
     */
    static class ReaderFactory {

        private final Charset charset;
        private final Exclusive exclusive;

        public ReaderFactory(String charset, Exclusive exclusive) {
            if (charset == null) {
                charset = "UTF-8";
            }
            this.charset = Charset.forName(charset);
            this.exclusive = exclusive;
        }

        /**
         * Entry point for creating the reader.
         *
         * @return
         * @throws IOException
         */
        Reader createReaderFromSrcOrSystemIn() throws IOException {
            final Reader reader;
            if (this.exclusive.src != null) {
                final FileReader fr = new FileReader(this.exclusive.src, charset);
                reader = fr;
            } else {
                final InputStreamReader isr = createSystemInReader();
                reader = isr;
            }
            return reader;
        }

        InputStreamReader createSystemInReader() {
            InputStream is = new DelegatingNonClosingInputStream(System.in);
            InputStreamReader isr = new InputStreamReader(is, this.charset);
            return isr;
        }

    }

}
