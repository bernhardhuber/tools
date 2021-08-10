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

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create a single {@link Connection} to a database.
 *
 * @author pi
 */
public class ConnectionFactory {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);

    private final Properties connectionProps;

    public ConnectionFactory(Properties connProperties) {
        this.connectionProps = connProperties;
    }

    public Properties getConnectionProps() {
        return connectionProps;
    }

    /**
     * Main entry for creating a jdbc {@link Connection}.
     *
     * @return
     * @throws GeneralWrappingRuntimeException
     */
    public Connection createConnection() throws GeneralWrappingRuntimeException {
        try {
            logger.debug("createConnection from connection-properties {}",
                    this.connectionProps);

            final String driver = this.connectionProps.getProperty("driver");
            final Class<Driver> clazz = (Class<Driver>) Class.forName(driver);
            final Driver driverInstance = clazz.getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(driverInstance);

            final String url = this.connectionProps.getProperty("url");
            final Connection connection = DriverManager.getConnection(url, this.connectionProps);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            logger.debug("created connection {}", new DatabaseMetaDataInfo(connection).buildFromUrlPasswordAndDatabaseInfoAndDriverInfo());
            return connection;
        } catch (SQLException
                | ClassNotFoundException
                | NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            final String msg = String.format("Cannot create a connection: %s", this.connectionProps);
            throw new GeneralWrappingRuntimeException(msg, ex);
        }
    }

}
