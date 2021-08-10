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

import org.huberb.services.dbunit.support.ConnectionFactory;
import org.huberb.services.dbunit.support.DatabaseConnectionFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.huberb.services.dbunit.support.ConnectionFactoryTest.CreateMemTestConnectionFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pi
 */
public class DatabaseConnectionFactoryTest {

    /**
     * Test of createDatabaseConnection method, of class
     * DatabaseConnectionFactory.
     */
    @Test
    public void testCreateDatabaseConnection() throws SQLException, DatabaseUnitException {
        final ConnectionFactory connectionFactory = new CreateMemTestConnectionFactory().create();

        try (Connection connection = connectionFactory.createConnection()) {
            final Properties dbConfigProperties = new Properties();
            dbConfigProperties.setProperty("datatypeFactory", "org.dbunit.ext.h2.H2DataTypeFactory");
            final String schema = null;

            final DatabaseConnectionFactory databaseConnectionFactory = new DatabaseConnectionFactory(dbConfigProperties, schema);
            final IDatabaseConnection databaseConnection = databaseConnectionFactory.createDatabaseConnection(connection);

            assertEquals( schema, databaseConnection.getSchema());
            assertEquals(org.dbunit.ext.h2.H2DataTypeFactory.class, databaseConnection.getConfig().getProperty("http://www.dbunit.org/properties/datatypeFactory").getClass());
            assertEquals(connection, databaseConnection.getConnection());
            assertEquals(true, databaseConnection.getConnection().isValid(0));
            
            databaseConnection.close();
        }
    }

}
