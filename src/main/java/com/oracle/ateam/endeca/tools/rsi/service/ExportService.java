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
import com.oracle.ateam.endeca.tools.rsi.export.CsvExporter;
import com.oracle.ateam.endeca.tools.rsi.export.ExcelExporter;
import com.oracle.ateam.endeca.tools.rsi.export.Exporter;
import com.oracle.ateam.endeca.tools.rsi.export.XmlExporter;
import com.oracle.ateam.endeca.tools.rsi.util.EndecaHelper;
import com.oracle.ateam.endeca.tools.rsi.util.RecordReader;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import jxl.write.WriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.oracle.ateam.endeca.tools.rsi.util.EndecaHelper.makeString;

/**
 * Service for exporting RecordStores to file.
 *
 * @author Jim Prantzalos
 * @version 1.0
 * @since R1.0
 */
public class ExportService extends Service<Void> {
    private transient static final Logger log = LoggerFactory.getLogger(ExportService.class);
    private File exportFile;
    private ServiceInfo serviceInfo;
    private Exporter writer;

    /**
     * Export the contents of the <code>RecordStore</code> to one of the supported file types.
     * If the file extension is "csv", a {@link CsvExporter} is used.
     * If the file extension is "xls", a {@link ExcelExporter} is used.
     * If the file extension is "xml", a {@link XmlExporter} is used.
     *
     * @param exportFile the file to export to.
     * @param serviceInfo the record store and associated data.
     */
    public ExportService(final File exportFile, final ServiceInfo serviceInfo) {
        this.exportFile = exportFile;
        this.serviceInfo = serviceInfo;
        if (exportFile != null) {
            int lastDot = exportFile.getName().lastIndexOf('.');
            String extension = exportFile.getName().substring(lastDot + 1);
            if ("csv".equals(extension.toLowerCase())) {
                writer = new CsvExporter(exportFile, this);
            }
            else if ("xls".equals(extension.toLowerCase())) {
                writer = new ExcelExporter(exportFile, this);
            }
            else if ("xml".equals(extension.toLowerCase())) {
                writer = new XmlExporter(exportFile);
            }
        }
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws InterruptedException {
                RecordReader reader = null;
                final RecordStore recStore = serviceInfo.getRecordStore();
                final GenerationId genId = serviceInfo.getGenerationId();
                final File recStoreDataFile = serviceInfo.getRecordStoreDataFile();
                boolean incomplete;

                updateMessage(exportFile.getName() + " (0)");
                try {
                    if (recStoreDataFile != null) {
                        reader = new RecordReader(RecordIOFactory.createRecordReader(recStoreDataFile));
                    }
                    else {
                        TransactionId txId = recStore.startTransaction(TransactionType.READ);
                        if (serviceInfo.isBaselineRead()) {
                            reader = new RecordReader(RecordStoreReader.createBaselineReader(recStore, txId, genId));
                        } else {
                            reader = new RecordReader(RecordStoreReader.createDeltaReader(recStore, txId, genId.getPreviousGenerationId(), genId));
                        }
                    }
                    // Read records and write to exporter
                    writer.writeHeader(serviceInfo.getColumnIndexMap());
                    while (reader.hasNext()) {
                        writer.writeRecord(reader.next());
                        if (isCancelled()) {
                            break;
                        }
                        updateMessage(exportFile.getName() + " (" + reader.getCount() + ")");
                    }
                    incomplete = isCancelled();
                    log.info(reader.getCount() + " records exported" + ((incomplete) ? " (incomplete)." : "."));
                }
                catch (IOException | RecordStoreException | WriteException e) {
                    log.error("Unable to process all records (count=" + (reader != null ? reader.getCount() : 0) + ").", e);
                }
                finally {
                    EndecaHelper.close(reader);
                    try {
                        writer.close();
                    } catch (IOException | WriteException e) {
                        log.error("Unable to close export writer.", e);
                    }
                }
                return null;
            }
        };
    }

    public List<String> createRow(final Record record) {
        final List<String> rowData = new ArrayList<>();
        final Set<String> propertyNames = record.getAllPropertyNames();
        final java.util.regex.Pattern columnFilter = serviceInfo.getColumnFilterPattern();
        final java.util.regex.Pattern valueFilter = serviceInfo.getValueFilterPattern();
        final Map<String, Integer> columnIndexMap = serviceInfo.getColumnIndexMap();
        int size = columnIndexMap.size();
        boolean foundMatchingValue = false;
        while (rowData.size() < size) {
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
                rowData.set(index, valueString);
                if (valueFilter == null || valueFilter.matcher(valueString).matches()) {
                    foundMatchingValue = true;
                }
            }
        }
        return (foundMatchingValue) ? rowData : null;
    }
}
