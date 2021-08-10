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

import java.util.concurrent.Callable;
import picocli.CommandLine;

/**
 * Help sub-command showing dbconfig details.
 *
 * @author pi
 */
@CommandLine.Command(name = "about-dbconfig",
        helpCommand = true,
        description = "Display dbconfig options")
class AboutDbConfig implements Callable<Integer> {

    private final String descriptionText = "DbConfig key=value definitions%n"
            + "%n" + "Following features are supported:%n"
            + "" + "@|bold qualifiedTableNames|@=[true|*false*]%n"
            + "@|bold batchedStatements|@=[true|*false*]%n"
            + "@|bold caseSensitiveTableNames|@=[true|*false*%n"
            + "@|bold allowEmptyFields|@=[true|*false*]%n"
            + "@|bold datatypeWarning|@=[*true*|false]%n"
            + "%n"
            + "Following properties are supported%n"
            + ""
            + "@|bold tableType|@=TABLE%n"
            + "  Used to configure the list of table types recognized by DbUnit. See java.sql.DatabaseMetaData.getTables() for possible values.%n"
            + "@|bold batchSize|@=100%n"
            + "  Integer object giving the size of batch updates.%n"
            + "@|bold statementFactory|@=org.dbunit.database.statement.PreparedStatementFactory%n"
            + "  Used to configure the statement factory. The Object must implement org.dbunit.database.statement.IStatementFactory. %n"
            + "@|bold datatypeFactory|@=...%n"
            + "  Used to configure the DataType factory.%n" + "  You can replace the default factory to add support for non-standard database vendor data types.%n"
            + "  The Object must implement org.dbunit.dataset.datatype.IDataTypeFactory.%n"
            + "  The following factories are currently available:\n"
            + "%n"
            + "  org.dbunit.ext.db2.Db2DataTypeFactory%n"
            + "  org.dbunit.ext.h2.H2DataTypeFactory%n"
            + "  org.dbunit.ext.hsqldb.HsqldbDataTypeFactory%n"
            + "  org.dbunit.ext.mckoi.MckoiDataTypeFactory%n"
            + "  org.dbunit.ext.mssql.MsSqlDataTypeFactory%n"
            + "  org.dbunit.ext.mysql.MySqlDataTypeFactory%n"
            + "  org.dbunit.ext.oracle.OracleDataTypeFactory%n"
            + "  org.dbunit.ext.oracle.Oracle10DataTypeFactory%n"
            + "  org.dbunit.ext.postgresql.PostgresqlDataTypeFactory%n"
            + "  org.dbunit.ext.netezza.NetezzaDataTypeFactor%n"
            + "%n"
            + "@|bold metadataHandler|@=org.dbunit.database.DefaultMetadataHandler%n"
            + "  Used to configure the handler used to control database metadata related methods.%n"
            + "  The Object must implement org.dbunit.database.IMetadataHandler.%n"
            + "@|bold fetchSize|@=100%n" + "  Integer object giving the statement fetch size for loading data into a result set table.%n"
            + "@|bold resultSetTableFactory|@=org.dbunit.database.CachedResultSetTableFactory%n"
            + "  Used to configure the ResultSet table factory.%n"
            + "  The Object must implement org.dbunit.database.IResultSetTableFactory.%n"
            + "@|bold escapePattern|@=null%n"
            + "  Allows schema, table and column names escaping. The property value is an escape pattern where the ? is replaced by the name.%n"
            + "  For example, the pattern \"[?]\" is expanded as \"[MY_TABLE]\" for a table named \"MY_TABLE\".%n"
            + "  The most common escape pattern is \"?\" which surrounds the table name with quotes (for the above example it would result in \"MY_TABLE\").%n"
            + "  As a fallback if no questionmark is in the given String and its length is one it is used to surround the table name on the left and right side.%n"
            + "  For example the escape pattern \" will have the same effect as the escape pattern \"?\".%n"
            + "@|bold allowVerifytabledefinitionExpectedtableCountMismatch|@=false%n"
            + "%n";

    @Override
    public Integer call() throws Exception {
        final String descriptionTextAnsi = CommandLine.Help.Ansi.AUTO.string(descriptionText);
        System.err.printf(descriptionTextAnsi);
        return 0;
    }

}
