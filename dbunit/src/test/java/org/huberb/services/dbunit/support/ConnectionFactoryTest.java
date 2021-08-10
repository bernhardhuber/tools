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
import java.sql.SQLException;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pi
 */
public class ConnectionFactoryTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of createConnection method, of class ConnectionFactory.
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testCreateConnection() throws SQLException {
        final ConnectionFactory instance = new CreateMemTestConnectionFactory().create();

        try (Connection connection = instance.createConnection()) {
            String m = String.format("connection %s, connectionPropeerties %s", connection, instance.getConnectionProps());
            assertNotNull(connection, m);
            assertFalse(connection.isClosed(), m);
            assertTrue(connection.isValid(0), m);
        }
    }

    static class CreateMemTestConnectionFactory {

        ConnectionFactory create() {
            final Properties connectionProperties = new Properties();
            connectionProperties.setProperty("driver", "org.h2.Driver");
            connectionProperties.setProperty("url", "jdbc:h2:mem:test");
            connectionProperties.setProperty("user", "memTest");
            connectionProperties.setProperty("password", "memTest");

            final ConnectionFactory instance = new ConnectionFactory(connectionProperties);
            return instance;
        }
    }

    /**
     * Test of getConnectionProps method, of class ConnectionFactory.
     */
    @org.junit.Test
    public void testGetConnectionProps() {
        final ConnectionFactory instance = new CreateMemTestConnectionFactory().create();
        assertEquals("org.h2.Driver", instance.getConnectionProps().get("driver"));
    }
}
