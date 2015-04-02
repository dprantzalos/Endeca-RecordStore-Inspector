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

import com.endeca.itl.recordstore.GenerationId;
import com.endeca.itl.recordstore.RecordStore;
import com.oracle.ateam.endeca.tools.rsi.component.FileOpenDialog;
import com.oracle.ateam.endeca.tools.rsi.component.RecordGenerationsPane;
import com.oracle.ateam.endeca.tools.rsi.component.RecordStoreTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Common RecordStore data used by services.
 *
 * @author Jim Prantzalos
 * @version $Revision$ $Date$
 * @since R1.0
 */
public class ServiceInfo {
    private transient static final Logger log = LoggerFactory.getLogger(ServiceInfo.class);
    private RecordStore recordStore;
    private RecordStoreTab recordStoreTab;
    private Map<String, Integer> columnIndexMap = new HashMap<>();
    private String recordStoreName;
    private GenerationId generationId;
    private boolean isBaselineRead;
    private File recordStoreDataFile;
    private Pattern columnFilterPattern, valueFilterPattern;

    public ServiceInfo(final RecordStoreTab recordStoreTab) {
        this.recordStoreTab = recordStoreTab;
    }

    public void setExtraInfo(final RecordStore recordStore, final FileOpenDialog fileOpenDialog) {
        final RecordGenerationsPane recGenPane = fileOpenDialog.getRecordGenerationsPane();
        this.recordStore = recordStore;
        this.recordStoreName = fileOpenDialog.getSelectedRecordStoreName();
        this.generationId = recGenPane.getSelectedGenerationId();
        this.isBaselineRead = recGenPane.isUseBaselineReadSelected();
    }

    public void setExtraInfo(final File dataFile) {
        this.recordStoreDataFile = dataFile;
    }

    public RecordStore getRecordStore() {
        return recordStore;
    }

    public RecordStoreTab getRecordStoreTab() {
        return recordStoreTab;
    }

    public Map<String, Integer> getColumnIndexMap() {
        return columnIndexMap;
    }

    public String getRecordStoreName() {
        return recordStoreName;
    }

    public GenerationId getGenerationId() {
        return generationId;
    }

    public boolean isBaselineRead() {
        return isBaselineRead;
    }

    public File getRecordStoreDataFile() {
        return recordStoreDataFile;
    }

    public Pattern getColumnFilterPattern() {
        return columnFilterPattern;
    }

    public Pattern getValueFilterPattern() {
        return valueFilterPattern;
    }

    public boolean setFilterPatterns(String columnFilter, String valueFilter) {
        boolean colFilterChanged, valFilterChanged;
        if (columnFilter != null && columnFilter.trim().length() > 0) {
            colFilterChanged = columnFilterPattern == null || !columnFilter.equals(columnFilterPattern.pattern());
            if (colFilterChanged) {
                columnFilterPattern = Pattern.compile(columnFilter);
                log.debug("Column filter set: " + columnFilterPattern);
            }
        } else {
            colFilterChanged = columnFilterPattern != null;
            if (colFilterChanged) {
                columnFilterPattern = null;
                log.debug("Column filter cleared");
            }
        }
        if (valueFilter != null && valueFilter.trim().length() > 0) {
            valFilterChanged = valueFilterPattern == null || !valueFilter.equals(valueFilterPattern.pattern());
            if (valFilterChanged) {
                valueFilterPattern = Pattern.compile(valueFilter);
                log.debug("Value filter set: " + valueFilterPattern);
            }
        } else {
            valFilterChanged = valueFilterPattern != null;
            if (valFilterChanged) {
                valueFilterPattern = null;
                log.debug("Value filter cleared");
            }
        }
        return colFilterChanged || valFilterChanged;
    }
}
