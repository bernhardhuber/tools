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

import java.util.Map;
import static java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/**
 *
 * @author pi
 */
@CommandLine.Command(name = "MainH2",
        subcommands = {
            AboutLogging.class,
            AboutDbConfig.class,
            Compare.class,
            Export.class,
            Operations.class,
            RunScript.class,
        },
        mixinStandardHelpOptions = true,
        showAtFileInUsageHelp = true,
        showDefaultValues = true,
        version = "MainH2 0.1-SNAPSHOT",
        description = "Run DbUnit Tools%n"
)
public class MainDbUnit implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(MainDbUnit.class);

    //---
    @CommandLine.Option(names = {"--driver"},
            defaultValue = "org.h2.Driver",
            paramLabel = "DRIVER",
            required = false,
            description = "database jdbc drivr")
    private String driver;
    @CommandLine.Option(names = {"--user"},
            defaultValue = "sa",
            paramLabel = "USER",
            required = false,
            description = "database user")
    private String user;
    @CommandLine.Option(names = {"--password"},
            defaultValue = "",
            paramLabel = "PASSWORD",
            required = false,
            description = "database password")
    private String password;
    @CommandLine.Option(names = {"--url"},
            defaultValue = "jdbc:h2:mem:/test1",
            paramLabel = "URL",
            required = false,
            description = "database jdbc url")
    private String jdbcUrl;
    @CommandLine.Option(names = {"--schema"},
            paramLabel = "SCHEMA",
            required = false,
            description = "database schema")
    private String schema;

    //http://www.dbunit.org/features/qualifiedTableNames=false, 
    //http://www.dbunit.org/features/batchedStatements=false, 
    //http://www.dbunit.org/features/caseSensitiveTableNames=false, 
    //http://www.dbunit.org/features/allowEmptyFields=false, 
    //http://www.dbunit.org/features/datatypeWarning=true, 
    //http://www.dbunit.org/properties/tableType=[Ljava.lang.String;@7ddf94, 
    //http://www.dbunit.org/properties/batchSize=100, 
    //http://www.dbunit.org/properties/statementFactory=org.dbunit.database.statement.PreparedStatementFactory@1e2d654, 
    //http://www.dbunit.org/properties/datatypeFactory=org.dbunit.ext.h2.H2DataTypeFactory[_toleratedDeltaMap=org.dbunit.dataset.datatype.ToleratedDeltaMap@1bd4fdd], 
    //http://www.dbunit.org/properties/metadataHandler=org.dbunit.database.DefaultMetadataHandler@1183b20, 
    //http://www.dbunit.org/properties/fetchSize=100, 
    //http://www.dbunit.org/properties/resultSetTableFactory=org.dbunit.database.CachedResultSetTableFactory@183df68, 
    //http://www.dbunit.org/properties/escapePattern=null, 
    //http://www.dbunit.org/properties/allowVerifytabledefinitionExpectedtableCountMismatch=false}], 
    @CommandLine.Option(names = {"--dbconfig", "-C"},
            mapFallbackValue = CommandLine.Option.NULL_VALUE,
            description = "Sets dbConfig key=value features, and properties."
    )
    private Map<String, Optional<String>> dbConfigMap;

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new MainDbUnit()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {

        return 0;
    }
//
//    String getUserName() {
//        return user;
//    }
//
//    String getPassword() {
//        return password;
//    }
//
//    String getJdbcURL() {
//        return jdbcUrl;
//    }
//
//    String getSchema() {
//        return this.schema;
//    }
//

    Properties createPropertiesJdbcConnection() {
        final Properties properties = new Properties();
        if (this.driver != null) {
            properties.setProperty("driver", this.driver);
        }
        if (this.jdbcUrl != null) {
            properties.setProperty("url", this.jdbcUrl);
        }
        if (this.user != null) {
            properties.setProperty("user", this.user);
        }
        if (this.password != null) {
            properties.setProperty("password", this.password);
        }
        if (this.schema != null) {
            properties.setProperty("schema", this.schema);
        }
        return properties;
    }

    Properties createPropertiesForDbConfig() {
        final Properties properties = new Properties();
        if (dbConfigMap != null) {
            dbConfigMap.entrySet().forEach((Entry< String, Optional<String>> entry) -> {
                final String k = entry.getKey();
                final String v = entry.getValue().orElse(null);
                if (k != null && v != null) {
                    properties.setProperty(k, v);
                }
            });
        }
        return properties;
    }

}
