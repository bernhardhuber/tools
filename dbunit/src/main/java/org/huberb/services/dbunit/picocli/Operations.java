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
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.ext.mssql.InsertIdentityOperation;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.TransactionOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/**
 * Sub-command running a DbUnit operation, like CLEAN_INSERT, etc.
 *
 * @author pi
 */
@CommandLine.Command(name = "operations",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        description = "Import into database data from the supplied filename.")
public class Operations implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(Operations.class);
    @CommandLine.ParentCommand
    private MainDbUnit mainDbUnit; // picocli injects reference to parent command
    //---
    @CommandLine.Option(names = {"--type"},
            paramLabel = "TYPE",
            defaultValue = "CLEAN_INSERT",
            required = true,
            description = "Type of Database operation to perform. "
            + "Supported types are UPDATE, INSERT, DELETE, DELETE_ALL, REFRESH, CLEAN_INSERT, "
            + "MSSQL_INSERT, MSSQL_REFRESH, MSSQL_CLEAN_INSERT.")
    private String _type = "CLEAN_INSERT";
    @CommandLine.Option(names = {"--src"},
            paramLabel = "SOURCE",
            required = true,
            description = "The source-file upon which the opertation is to be performed")
    private File _src;
    @CommandLine.Option(names = {"--format"},
            paramLabel = "FORMAT",
            defaultValue = "FLAT",
            required = false,
            description = "Format type of supplied source file (FLAT,XML,DTD,CSV,XLS).")
    private String _format;
    @CommandLine.Option(names = {"--ordered"},
            required = false,
            description = "If set to \"true\" the tables of the exported dataset are ordered using a database key. "
            + "Therefore dbunit uses the FilteredDataSet combined with the DatabaseSequenceFilter. "
            + "Defaults to \"false\"")
    private boolean ordered;
    @CommandLine.Option(names = {"--transaction"},
            required = false,
            description = "Boolean indicating if this operation should be wrapped in a transaction, "
            + "ensuring that the entire operation completes or is rolled back. "
            + "This may also dramatically improve performance of large operations. "
            + "Possible values are \"true\" or \"false\". Defaults to \"false\".")
    private boolean _transaction;
    @CommandLine.Option(names = {"--nulltoken"},
            paramLabel = "NULLTOKEN",
            required = false, description = "A String used to replace all occurrences of this String in a dataset with <null>. "
            + "A common value for this is \"[NULL]\". "
            + "Defaults to null which means that no replacement occurrs.")
    private String _nullToken;
    private DatabaseOperation _operation;
    private boolean _forwardOperation = true;

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
        final OperationMode operationMode = calcOperatonForwardOperationFrom(_type);
        if (operationMode == OperationMode.NONE) {
            return;
        }
        try {
            final DatabaseOperation operation = _transaction ? new TransactionOperation(_operation) : _operation;
            // TODO This is not very nice and the design should be reviewed but it works for now (gommma)
            final boolean useForwardOnly = _forwardOperation && ordered;
            IDataSet dataset = new DbUnitSupports1().getSrcDataSet(_src, _format, useForwardOnly);
            if (_nullToken != null) {
                dataset = new ReplacementDataSet(dataset);
                ((ReplacementDataSet) dataset).addReplacementObject(_nullToken, null);
            }
            if (ordered) {
                final DatabaseSequenceFilter databaseSequenceFilter = new DatabaseSequenceFilter(connection);
                dataset = new FilteredDataSet(databaseSequenceFilter, dataset);
            }
            operation.execute(connection, dataset);
            logger.info("Successfully excuted operation {} on tables {}", _type, Arrays.asList(dataset.getTableNames()));
        } catch (SQLException e) {
            throw new DatabaseUnitException(e);
        }
    }

    enum OperationMode {
        UPDATE(DatabaseOperation.UPDATE, true),
        INSERT(DatabaseOperation.INSERT, true),
        REFRESH(DatabaseOperation.REFRESH, true),
        DELETE(DatabaseOperation.DELETE, false),
        DELETE_ALL(DatabaseOperation.DELETE_ALL, false),
        CLEAN_INSERT(DatabaseOperation.CLEAN_INSERT, false),
        NONE(DatabaseOperation.NONE, true),
        MSSQL_CLEAN_INSERT(InsertIdentityOperation.CLEAN_INSERT, false),
        MSSQL_INSERT(InsertIdentityOperation.INSERT, true),
        MSSQL_REFRESH(InsertIdentityOperation.REFRESH, true);

        private OperationMode(DatabaseOperation _operation, boolean forwardOperation) {
            this._operation = _operation;
            this._forwardOperation = forwardOperation;
        }
        private final DatabaseOperation _operation;
        private final boolean _forwardOperation;

        public DatabaseOperation getOperation() {
            return _operation;
        }

        public boolean isForwardOperation() {
            return _forwardOperation;
        }

        static Optional<OperationMode> findOperationFor(String type) {
            final Optional<OperationMode> findResult = Arrays.asList(OperationMode.values())
                    .stream()
                    .filter(om -> om.name().equals(type))
                    .findFirst();
            return findResult;
        }
    }

    OperationMode calcOperatonForwardOperationFrom(String type) {
        final Optional<OperationMode> operationModeOpt = OperationMode.findOperationFor(type);
        final OperationMode operationMode = operationModeOpt
                .orElseThrow(() -> {
                    final String m = String.format("Type must be one of: %s but was: %s", Arrays.asList(OperationMode.values()), type);
                    return new IllegalArgumentException(m);
                });
        _type = operationMode.name();
        _operation = operationMode.getOperation();
        _forwardOperation = operationMode.isForwardOperation();
        return operationMode;
    }

}
