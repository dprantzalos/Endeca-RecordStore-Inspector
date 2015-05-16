/**
 * Copyright (c) 2015, Oracle Corporation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 *     * Neither the name of Oracle nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ORACLE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.ateam.endeca.tools.rsi.service;

import com.endeca.itl.record.PropertyValue;
import com.endeca.itl.record.Record;
import com.endeca.itl.record.io.RecordIOFactory;
import com.endeca.itl.recordstore.*;
import com.oracle.ateam.endeca.tools.rsi.data.NameValueData;
import com.oracle.ateam.endeca.tools.rsi.util.RecordReader;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static com.oracle.ateam.endeca.tools.rsi.util.EndecaHelper.close;
import static com.oracle.ateam.endeca.tools.rsi.util.EndecaHelper.makeString;
import static com.oracle.ateam.endeca.tools.rsi.util.EndecaHelper.rollbackTransaction;
import static com.oracle.ateam.endeca.tools.rsi.util.JavaHelper.sortByValue;

/**
 * Service for reading and displaying RecordStores.
 *
 * @author Jim Prantzalos
 * @version 1.0
 * @since R1.0
 */
public class RecordReaderService extends Service<TableView<ObservableList<StringProperty>>> {
    private transient static final Logger log = LoggerFactory.getLogger(RecordReaderService.class);
    private ServiceInfo serviceInfo;
    private TableView<ObservableList<StringProperty>> tableView;

    /**
     * Constructs a RecordReaderService instance with the data required to run the service.
     *
     * @param serviceInfo the data associated with this service instance.
     */
    public RecordReaderService(final ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    /**
     * Returns the data associated with this service instance. This data can be shared across services.
     *
     * @return the data associated with this service instance.
     */
    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public ObservableList<NameValueData> transposeRowData(ObservableList<StringProperty> row) {
        ObservableList<NameValueData> rowData = FXCollections.observableArrayList();
        Set<Map.Entry<String, Integer>> columns = sortByValue(serviceInfo.getColumnIndexMap());
        Iterator<StringProperty> i = row.iterator();
        StringProperty rowprop;
        for (Map.Entry<String, Integer> entry: columns) {
            if (i.hasNext()) {
                rowprop = i.next();
                if (rowprop != null) {
                    rowData.add(new NameValueData(entry.getKey(), rowprop.getValue()));
                }
            }
            else break;
        }
        return rowData;
    }

    @Override
    protected Task<TableView<ObservableList<StringProperty>>> createTask() {
        return new Task<TableView<ObservableList<StringProperty>>>() {
            @Override
            protected TableView<ObservableList<StringProperty>> call() throws InterruptedException {
                final RecordStore recStore = serviceInfo.getRecordStore();
                final GenerationId genId = serviceInfo.getGenerationId();
                final File recStoreDataFile = serviceInfo.getRecordStoreDataFile();
                final ObservableList<ObservableList<StringProperty>> rows = FXCollections.observableArrayList();
                ObservableList<StringProperty> row;
                RecordReader reader = null;
                TransactionId txId = null;
                boolean incomplete;

                updateMessage(serviceInfo.getRecordStoreTab().getText() + " (0)");
                tableView = new TableView<>();
                serviceInfo.getColumnIndexMap().clear();
                try {
                    if (recStoreDataFile != null) {
                        reader = new RecordReader(RecordIOFactory.createRecordReader(recStoreDataFile));
                    } else {
                        txId = recStore.startTransaction(TransactionType.READ);
                        if (serviceInfo.isBaselineRead()) {
                            reader = new RecordReader(RecordStoreReader.createBaselineReader(recStore, txId, genId));
                        } else {
                            reader = new RecordReader(RecordStoreReader.createDeltaReader(recStore, txId, genId.getPreviousGenerationId(), genId));
                        }
                    }
                    while (reader.hasNext()) {
                        row = createRow(reader.next());
                        if (row != null) {
                            rows.add(row);
                        }
                        if (isCancelled()) {
                            break;
                        }
                        updateMessage(serviceInfo.getRecordStoreTab().getText() + " (" + reader.getCount() + ")");
                    }
                    incomplete = isCancelled();
                    log.info(reader.getCount() + " records read" + ((incomplete) ? " (incomplete)." : "."));
                }
                catch (IOException | RecordStoreException e) {
                    log.error("Unable to process all records (count=" + (reader != null ? reader.getCount() : 0) + ").", e);
                }
                finally {
                    rollbackTransaction(recStore, txId);
                    close(reader);
                }
                tableView.setItems(rows);
                return tableView;
            }
        };
    }

    protected ObservableList<StringProperty> createRow(final Record record) {
        final ObservableList<StringProperty> rowData = FXCollections.observableArrayList();
        final Set<String> propertyNames = record.getAllPropertyNames();
        final Pattern columnFilter = serviceInfo.getColumnFilterPattern();
        final Pattern valueFilter = serviceInfo.getValueFilterPattern();
        final Map<String, Integer> columnIndexMap = serviceInfo.getColumnIndexMap();
        int columnIndex = columnIndexMap.size();
        boolean foundMatchingValue = false;
        while (rowData.size() < columnIndex) {
            rowData.add(null); // ensure size
        }
        Integer index;
        String valueString;
        PropertyValue[] propertyValues;
        for (String name: propertyNames) {
            if (columnFilter == null || columnFilter.matcher(name).matches()) {
                propertyValues = record.getPropertyValues(name);
                valueString = makeString(propertyValues, '|');
                index = columnIndexMap.get(name);
                if (index == null) {
                    index = columnIndex++;
                    columnIndexMap.put(name, index);
                    tableView.getColumns().add(createColumn(index, name));
                    rowData.add(new SimpleStringProperty(valueString));
                }
                else {
                    rowData.set(index, new SimpleStringProperty(valueString));
                }
                if (valueFilter == null || valueFilter.matcher(valueString).matches()) {
                    foundMatchingValue = true;
                }
            }
        }
        return (foundMatchingValue) ? rowData : null;
    }

    protected TableColumn<ObservableList<StringProperty>, String> createColumn(final int index, final String title) {
        String columnTitle;
        if (title == null || title.trim().length() == 0) {
            columnTitle = "column_" + (index + 1);
        }
        else {
            columnTitle = title;
        }
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
        column.setCellValueFactory(cellDataFeatures -> {
            final ObservableList<StringProperty> values = cellDataFeatures.getValue();
            if (index >= values.size()) {
                return new SimpleStringProperty("");
            } else {
                return values.get(index);
            }
        });
        column.setText(columnTitle);
        log.debug("Column[" + index + "] created: " + columnTitle);
        return column;
    }
}
