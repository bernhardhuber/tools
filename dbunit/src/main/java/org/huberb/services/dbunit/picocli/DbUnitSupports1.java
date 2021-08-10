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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.ForwardOnlyDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvProducer;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.dbunit.dataset.stream.StreamingDataSet;
import org.dbunit.dataset.xml.FlatDtdProducer;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.dataset.xml.XmlProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 *
 * @author pi
 */
class DbUnitSupports1 {

    private static final Logger logger = LoggerFactory.getLogger(DbUnitSupports1.class);

    /**
     * Creates the dataset that is finally used for the export
     *
     * @param connection
     * @return The final dataset used for the export
     * @throws DatabaseUnitException
     * @throws SQLException
     */
    protected IDataSet getExportDataSet(IDatabaseConnection connection, List<String> tables, boolean ordered) throws DatabaseUnitException, SQLException {
        IDataSet dataset = getDatabaseDataSet(connection, tables);
        if (ordered) {
            // Use topologically sorted database
            ITableFilter filter = new DatabaseSequenceFilter(connection);
            dataset = new FilteredDataSet(filter, dataset);
        }
        return dataset;
    }

    protected IDataSet getSrcDataSet(File src, String format, boolean forwardonly) throws DatabaseUnitException {
        if (logger.isDebugEnabled()) {
            logger.debug("getSrcDataSet(src={}, format={}, forwardonly={}) - start", new Object[]{src, format, String.valueOf(forwardonly)});
        }
        try {
            IDataSetProducer producer = null;

            final Formats.Format formatEnum = Formats.Format.findFormatBy(format).orElse(null);
            if (formatEnum == Formats.Format.XML) {
                producer = new XmlProducer(getInputSource(src));
            } else if (formatEnum == Formats.Format.CSV) {
                producer = new CsvProducer(src);
            } else if (formatEnum == Formats.Format.FLAT) {
                producer = new FlatXmlProducer(getInputSource(src), true, true);
            } else if (formatEnum == Formats.Format.DTD) {
                producer = new FlatDtdProducer(getInputSource(src));
            } else if (formatEnum == Formats.Format.XLS) {
                return new CachedDataSet(new XlsDataSet(src));
            } else {
                throw new IllegalArgumentException("Type must be either 'flat'(default), 'xml', 'csv', 'xls' or 'dtd' but was: " + format);
            }
            if (forwardonly) {
                return new StreamingDataSet(producer);
            }
            return new CachedDataSet(producer);
        } catch (IOException e) {
            throw new DatabaseUnitException(e);
        }
    }

    InputSource getInputSource(File file) throws DatabaseUnitException {
        try {
            final String uri = /*.getAbsoluteFile()*/ file.toURI().toURL().toString();
            final InputSource source = new InputSource(uri);
            return source;
        } catch (MalformedURLException ex) {
            throw new DatabaseUnitException(String.format("Cannot create InputSource from '%s'", file.getAbsolutePath()));
        }
    }

    protected IDataSet getDatabaseDataSet(IDatabaseConnection connection, List<String> tables) throws DatabaseUnitException {
        if (logger.isDebugEnabled()) {
            logger.debug("getDatabaseDataSet(connection={}, tables={}) - start", new Object[]{connection, tables});
        }
        try {
            final DatabaseConfig config = connection.getConfig();
            // Retrieve the complete database if no tables or queries specified.
            if (tables.isEmpty()) {
                logger.debug("Retrieving the whole database because tables/queries have not been specified");
                return connection.createDataSet();
            }
            final List<QueryDataSet> queryDataSets = createQueryDataSet(tables, connection);
            final IDataSet[] dataSetsArray;
            if (config.getProperty(DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY).getClass().getName().equals("org.dbunit.database.ForwardOnlyResultSetTableFactory")) {
                dataSetsArray = (IDataSet[]) createForwardOnlyDataSetArray(queryDataSets);
            } else {
                dataSetsArray = (IDataSet[]) queryDataSets.toArray(new IDataSet[queryDataSets.size()]);
            }
            return new CompositeDataSet(dataSetsArray);
        } catch (SQLException e) {
            throw new DatabaseUnitException(e);
        }
    }

    private ForwardOnlyDataSet[] createForwardOnlyDataSetArray(List<QueryDataSet> dataSets) throws DataSetException, SQLException {
        ForwardOnlyDataSet[] forwardOnlyDataSets = new ForwardOnlyDataSet[dataSets.size()];
        for (int i = 0; i < dataSets.size(); i++) {
            forwardOnlyDataSets[i] = new ForwardOnlyDataSet(dataSets.get(i));
        }
        return forwardOnlyDataSets;
    }

    private List<QueryDataSet> createQueryDataSet(List<String> tables, IDatabaseConnection connection) throws DataSetException, SQLException {
        logger.debug("createQueryDataSet(tables={}, connection={})", tables, connection);
        final List<QueryDataSet> queryDataSets = new ArrayList<>();
        final QueryDataSet queryDataSet = new QueryDataSet(connection);
        for (String tableName : tables) {
            final String sql = "SELECT * from " + tableName;
            queryDataSet.addTable(tableName, sql);
        }
        if (queryDataSet.getTableNames().length > 0) {
            queryDataSets.add(queryDataSet);
        }
        return queryDataSets;
    }

}
