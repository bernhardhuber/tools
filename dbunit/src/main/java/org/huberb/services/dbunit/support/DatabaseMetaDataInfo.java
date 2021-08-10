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
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Wrapper for producing some info from {@link DatabaseMetaData}.
 *
 * @author pi
 */
public class DatabaseMetaDataInfo {

    final DatabaseMetaData jdbcDatabaseMetaData; // = jdbcConnection.getMetaData();

    /**
     * @param jdbcConnection
     */
    public DatabaseMetaDataInfo(Connection jdbcConnection) {
        try {
            jdbcDatabaseMetaData = jdbcConnection.getMetaData();
        } catch (SQLException ex) {
            throw new GeneralWrappingRuntimeException("construct DatabaseMetaDataInfo", ex);
        }
    }

    /**
     * Produce info about connection's url and username.
     *
     * @return
     */
    public String buildFromUrlAndPassword() {
        try {
            final String result = String.format(""
                    + "url %s, username %s",
                    jdbcDatabaseMetaData.getURL(),
                    jdbcDatabaseMetaData.getUserName()
            );
            return result;
        } catch (SQLException ex) {
            throw new GeneralWrappingRuntimeException("buildFromUrlAndPassword", ex);
        }
    }

    /**
     * Produce info about connection's url, username, database and driver.
     *
     * @return
     */
    public String buildFromUrlPasswordAndDatabaseInfoAndDriverInfo() {
        try {
            final String result = String.format(""
                    + "url %s, username %s; "
                    + ""
                    + "databaseProductName %s,"
                    + "databaseProductVersion %s,"
                    + "databaseMajorVersion %s, "
                    + "databaseMinorVersion %s;"
                    + ""
                    + "driverName %s,"
                    + "driverMajorVersion %s,"
                    + "driverMinorVersion %s"
                    + "", jdbcDatabaseMetaData.getURL(), jdbcDatabaseMetaData.getUserName(),
                    //---
                    jdbcDatabaseMetaData.getDatabaseProductName(),
                    jdbcDatabaseMetaData.getDatabaseProductVersion(),
                    jdbcDatabaseMetaData.getDatabaseMajorVersion(),
                    jdbcDatabaseMetaData.getDatabaseMinorVersion(),
                    //---
                    jdbcDatabaseMetaData.getDriverName(),
                    jdbcDatabaseMetaData.getDriverMajorVersion(),
                    jdbcDatabaseMetaData.getDriverMinorVersion()
            );
            return result;
        } catch (SQLException ex) {
            throw new GeneralWrappingRuntimeException("buildFromUrlPasswordAndDatabaseInfoAndDriverInfo", ex);
        }
    }

}
