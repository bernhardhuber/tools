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
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pi
 */
public class DatabaseMetaDataInfoTest {

    public DatabaseMetaDataInfoTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of buildFromUrlAndPassword method, of class DatabaseMetaDataInfo.
     */
    @Test
    public void testBuildFromUrlAndPassword() throws SQLException {
        final ConnectionFactory connectionFactory = new ConnectionFactoryTest.CreateMemTestConnectionFactory().create();
        try (Connection connection = connectionFactory.createConnection()) {
            DatabaseMetaDataInfo instance = new DatabaseMetaDataInfo(connection);
            String result = instance.buildFromUrlAndPassword();
            assertTrue(!result.isBlank(), result);
            assertTrue(result.contains("jdbc:h2"), result);
        }

    }

    /**
     * Test of buildFromUrlPasswordAndDatabaseInfoAndDriverInfo method, of class
     * DatabaseMetaDataInfo.
     */
    @Test
    public void testBuildFromUrlPasswordAndDatabaseInfoAndDriverInfo() throws SQLException {
        final ConnectionFactory connectionFactory = new ConnectionFactoryTest.CreateMemTestConnectionFactory().create();
        try (Connection connection = connectionFactory.createConnection()) {
            DatabaseMetaDataInfo instance = new DatabaseMetaDataInfo(connection);
            String result = instance.buildFromUrlPasswordAndDatabaseInfoAndDriverInfo();
            assertTrue(!result.isBlank(), result);
            assertTrue(result.contains("jdbc:h2"), result);
        }
    }

}
