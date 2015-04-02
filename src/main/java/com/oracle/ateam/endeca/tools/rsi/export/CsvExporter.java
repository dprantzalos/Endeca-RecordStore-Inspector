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
package com.oracle.ateam.endeca.tools.rsi.export;

import com.endeca.itl.record.Record;
import com.oracle.ateam.endeca.tools.rsi.service.ExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.oracle.ateam.endeca.tools.rsi.util.JavaHelper.sortByValue;

/**
 * A export writer for Comma-Separated Value (CSV) text files.
 *
 * @author Jim Prantzalos
 * @version $Revision$ $Date$
 * @since R1.0
 */
public class CsvExporter implements Exporter {
    private transient static final Logger log = LoggerFactory.getLogger(CsvExporter.class);
    private ExportService exportService;
    private BufferedWriter writer;

    public CsvExporter(final File exportFile, final ExportService service) {
        this.exportService = service;
        try {
            writer = new BufferedWriter(new FileWriter(exportFile));
        } catch (IOException e) {
            log.error("Unable to create export file '" + exportFile + "'", e);
        }
    }

    @Override
    public void writeHeader(final Map<String, Integer> columnIndexMap) throws IOException {
        StringBuilder buf = null;
        Set<Map.Entry<String, Integer>> headers = sortByValue(columnIndexMap);
        for (Map.Entry<String, Integer> entry: headers) {
            if (buf == null) {
                buf = new StringBuilder(entry.getKey());
            }
            else {
                buf.append(",").append(entry.getKey());
            }
        }
        if (buf != null) {
            writer.write(buf.append('\n').toString());
        }
    }

    @Override
    public void writeRecord(final Record record) throws IOException {
        StringBuilder buf = null;
        List<String> row = exportService.createRow(record);
        if (row != null) {
            for (String cell: row) {
                if (buf == null) {
                    buf = new StringBuilder();
                } else {
                    buf.append(",");
                }
                if (cell == null || cell.trim().length() < 1) {
                    // do nothing
                } else if (cell.matches("-?\\d+(\\.\\d+)?")) { // match number with optional '-' and decimal
                    buf.append(cell);
                } else {
                    buf.append("\"").append(cell).append("\"");
                }
            }
            if (buf != null) {
                writer.write(buf.append('\n').toString());
            }
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}

