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
package org.huberb.services.dbunit.support;

import java.sql.Connection;
import java.util.Properties;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create a DBUnit {@link IDatabaseConnection}.
 *
 * @author pi
 */
public class DatabaseConnectionFactory {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionFactory.class);

    private final Properties dbConfigProperties;
    private final String dbSchema;

    public DatabaseConnectionFactory(Properties dbConfigProperties, String dbSchema) {
        this.dbConfigProperties = dbConfigProperties;
        this.dbSchema = dbSchema;
    }

    public IDatabaseConnection createDatabaseConnection(Connection jdbcConnection) throws DatabaseUnitException {
        final IDatabaseConnection databaseConnection = new DatabaseConnection(jdbcConnection, dbSchema);

        logger.debug("createDatabaseConnection from jdbc-connection {}, dbconfig {}",
                new DatabaseMetaDataInfo(jdbcConnection).buildFromUrlAndPassword(),
                this.dbSchema);

        databaseConnection.getConfig().setPropertiesByString(dbConfigProperties);
        return databaseConnection;
    }

}
