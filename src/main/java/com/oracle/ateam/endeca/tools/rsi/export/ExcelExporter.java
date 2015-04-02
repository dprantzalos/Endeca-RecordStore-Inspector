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
import jxl.CellView;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.oracle.ateam.endeca.tools.rsi.util.JavaHelper.sortByValue;

/**
 * A export writer for Microsoft Excel files.
 *
 * @author Jim Prantzalos
 * @version 1.0
 * @since R1.0
 */
public class ExcelExporter implements Exporter {
    private transient static final Logger log = LoggerFactory.getLogger(ExcelExporter.class);
    private ExportService exportService;
    private final WritableCellFormat headerFormat = new WritableCellFormat(
            new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD)
    );
    private WritableWorkbook workbook;
    private WritableSheet sheet;
    private int rowCount = 1;

    public ExcelExporter(final File exportFile, final ExportService service) {
        this.exportService = service;
        try {
            workbook = Workbook.createWorkbook(exportFile);
            sheet = workbook.createSheet(exportService.getServiceInfo().getRecordStoreName(), 0);
        } catch (IOException e) {
            log.error("Unable to create Excel workbook for file '" + exportFile + "'", e);
        }
    }

    @Override
    public void writeHeader(final Map<String, Integer> columnIndexMap) throws WriteException {
        Set<Map.Entry<String, Integer>> headers = sortByValue(columnIndexMap);
        int index = 0;
        CellView cell;
        for (Map.Entry<String, Integer> entry: headers) {
            sheet.addCell(new Label(index++, 0, entry.getKey(), headerFormat));
            cell = sheet.getColumnView(index);
            cell.setAutosize(true);
            sheet.setColumnView(index, cell);
        }
    }

    @Override
    public void writeRecord(final Record record) throws IOException, WriteException {
        int index = 0;
        List<String> row = exportService.createRow(record);
        if (row != null) {
            for (String cell: row) {
                if (cell == null || cell.trim().length() < 1) {
                    sheet.addCell(new Label(index++, rowCount, ""));
                } else if (cell.matches("-?\\d+(\\.\\d+)?")) { // match number with optional '-' and decimal
                    sheet.addCell(new Number(index++, rowCount, Double.parseDouble(cell)));
                } else {
                    sheet.addCell(new Label(index++, rowCount, cell));
                }
            }
            rowCount++;
        }
    }

    @Override
    public void close() throws IOException, WriteException {
        workbook.write();
        workbook.close();
    }
}
