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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.NoSuchTableException;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/**
 * Sub-command running DbUnit compare.
 *
 * @author pi
 */
@CommandLine.Command(name = "compare",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        description = "Validate the content of the database against the specified dataset file.")
public class Compare implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(Compare.class);
    @CommandLine.ParentCommand
    private MainDbUnit mainDbUnit; // picocli injects reference to parent command
    @CommandLine.Option(names = {"--src"},
            paramLabel = "SOURCE",
            required = true,
            description = "The source-file upon which the comparison is to be performed")
    private File src;
    @CommandLine.Option(names = {"--format"},
            paramLabel = "FORMAT", defaultValue = "FLAT",
            required = false,
            description = "Format type of supplied source file (FLAT,XML,DTD,CSV,XLS).")
    private String format;
    @CommandLine.Option(names = {"--tables"},
            paramLabel = "TABLES", defaultValue = "",
            required = false,
            description = "Name of the database tables to compare.")
    private String tables;
    @CommandLine.Option(names = {"--sort"},
            required = false,
            description = "Sorts tables prior comparison. Defaults to \"false\".")
    private boolean _sort;

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
        final IDataSet expectedDataset = new DbUnitSupports1().getSrcDataSet(src, format, false);
        final IDataSet actualDataset = new DbUnitSupports1().getDatabaseDataSet(connection, theTables);
        final String[] tableNames;
        if (theTables.isEmpty()) {
            // No tables specified, assume must compare all tables from
            // expected dataset
            tableNames = expectedDataset.getTableNames();
        } else {
            tableNames = actualDataset.getTableNames();
        }
        for (int i = 0; i < tableNames.length; i++) {
            final String tableName = tableNames[i];
            ITable expectedTable;
            try {
                expectedTable = expectedDataset.getTable(tableName);
            } catch (NoSuchTableException e) {
                throw new DatabaseUnitException("Did not find table in source file '" + src + "' using format '" + format + "'", e);
            }
            final ITableMetaData expectedMetaData = expectedTable.getTableMetaData();
            ITable actualTable;
            try {
                actualTable = actualDataset.getTable(tableName);
            } catch (NoSuchTableException e) {
                throw new DatabaseUnitException("Did not find table in actual dataset '" + actualDataset + "' via db connection '" + connection + "'", e);
            }
            // Only compare columns present in expected table. Extra columns
            // are filtered out from actual database table.
            actualTable = DefaultColumnFilter.includedColumnsTable(actualTable, expectedMetaData.getColumns());
            if (_sort) {
                expectedTable = new SortedTable(expectedTable);
                actualTable = new SortedTable(actualTable);
            }
            Assertion.assertEquals(expectedTable, actualTable);
            logger.info("Successfully compared expected table '{}' against actualTable {}", expectedTable.getTableMetaData().getTableName(), actualTable.getTableMetaData().getTableName());
        }
    }

}
