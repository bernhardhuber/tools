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

import org.huberb.services.dbunit.support.DatabaseConnectionFactory;
import org.huberb.services.dbunit.support.ConnectionFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSetWriter;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlWriter;
import org.dbunit.dataset.xml.XmlDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/**
 * Sub-command running DbUnit export.
 *
 * @author pi
 */
@CommandLine.Command(name = "export",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        description = "Export the database to the supplied filename")
public class Export implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(Export.class);
    @CommandLine.ParentCommand
    private MainDbUnit mainDbUnit; // picocli injects reference to parent command
    @CommandLine.Option(names = {"--dest"},
            paramLabel = "DEST",
            required = true,
            description = "The xml destination filename")
    private File dest;
    @CommandLine.Option(names = {"--format"},
            paramLabel = "FORMAT",
            defaultValue = "FLAT",
            required = false,
            description = "export format (FLAT,XML,DTD,CSV,XLS)")
    private String format;
    @CommandLine.Option(names = {"--tables"},
            paramLabel = "TABLES",
            defaultValue = "",
            required = false,
            description = "Name of the database tables to export.")
    private String tables;
    @CommandLine.Option(names = {"--ordered"},
            required = false,
            description = "Exported datasets are ordered using a database key. Therefore dbunit uses the FilteredDataSet combined with the DatabaseSequenceFilter.")
    private boolean ordered;
    @CommandLine.Option(names = {"--encoding"},
            paramLabel = "ENCODING",
            defaultValue = "UTF-8",
            required = false,
            description = "Encoding creating DEST file")
    private String encoding;
    @CommandLine.Option(names = {"--doctype"},
            paramLabel = "DOCTYPE",
            required = false,
            description = "Xml doctype for creating DEST xml file")
    private String doctype = null;

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
    void process(Connection conn, String schema) throws SQLException, DatabaseUnitException {
        try (conn) {
            //conn.setAutoCommit(false);
            //conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            //---
            final Savepoint savepoint = conn.setSavepoint();
            //--
            final String theSchema = schema;
            final Properties dbConfigProperties = this.mainDbUnit.createPropertiesForDbConfig();
            final DatabaseConnectionFactory databaseConnectionFactory = new DatabaseConnectionFactory(dbConfigProperties, theSchema);
            final IDatabaseConnection databaseConnection = databaseConnectionFactory.createDatabaseConnection(conn);
            execute(databaseConnection);
            conn.commit();
        }
    }

    void execute(IDatabaseConnection connection) throws DatabaseUnitException {
        logger.debug("execute(connection={}) - start", connection);
        try {
            if (this.dest == null) {
                throw new DatabaseUnitException("'_dest' is a required attribute of the <export> step.");
            }
            final List<String> theTables = new ArrayList<>();
            final String[] tablesSplittedAsArray = this.tables.split("[ ,]");
            for (int i = 0; i < tablesSplittedAsArray.length; i++) {
                final String aTableName = tablesSplittedAsArray[i];
                if (aTableName == null) {
                    continue;
                }
                final String normalizedTableName = aTableName.trim();
                if (!normalizedTableName.isBlank()) {
                    theTables.add(normalizedTableName);
                }
            }
            final IDataSet dataset = new DbUnitSupports1().getExportDataSet(connection, theTables, this.ordered);
            logger.info("Export following tables: {}", Arrays.asList(dataset.getTableNames()));
            // Write the dataset
            final Formats.Format formatEnum = Formats.Format.findFormatBy(this.format).orElse(null);
            if (formatEnum == Formats.Format.CSV) {
                CsvDataSetWriter.write(dataset, this.dest);
            } else {
                try (final OutputStream out = new FileOutputStream(this.dest)) {
                    if (formatEnum == Formats.Format.FLAT) {
                        FlatXmlWriter writer = new FlatXmlWriter(out, encoding);
                        writer.setDocType(this.doctype);
                        writer.write(dataset);
                    } else if (formatEnum == Formats.Format.XML) {
                        XmlDataSet.write(dataset, out, encoding);
                    } else if (formatEnum == Formats.Format.DTD) {
                        //TODO Should DTD also support encoding? It is basically an XML file...
                        FlatDtdDataSet.write(dataset, out); //, getEncoding());
                    } else if (formatEnum == Formats.Format.XLS) {
                        XlsDataSet.write(dataset, out);
                    } else {
                        throw new IllegalArgumentException("The given format '" + this.format + "' is not supported.");
                    }
                }
            }
            logger.info("Successfully wrote file '{}' using format {}", this.dest, this.format);
        } catch (SQLException e) {
            throw new DatabaseUnitException(e);
        } catch (IOException e) {
            throw new DatabaseUnitException(e);
        }
    }

}
