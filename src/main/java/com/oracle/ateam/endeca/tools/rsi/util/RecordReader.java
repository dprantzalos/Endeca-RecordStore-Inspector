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
package com.oracle.ateam.endeca.tools.rsi.util;

import com.endeca.itl.record.Record;
import com.endeca.itl.recordstore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

/**
 * An iterable RecordStore reader that supports both RecordReader and RecordStoreReader.
 *
 * @author Jim Prantzalos
 * @version $Revision$ $Date$
 * @since R1.0
 */
public class RecordReader implements Iterator<Record> {
    private transient static final Logger log = LoggerFactory.getLogger(RecordReader.class);
    private com.endeca.itl.record.io.RecordReader recordReader;
    private RecordStoreReader recordStoreReader;
    private int count = 0;

    /**
     * Constructs a record reader using an Endeca <code>RecordReader</code>.
     *
     * @param reader the Endeca <code>RecordReader</code>.
     */
    public RecordReader(com.endeca.itl.record.io.RecordReader reader) {
        this.recordReader = reader;
    }

    /**
     * Constructs a record reader using an Endeca <code>RecordStoreReader</code>.
     *
     * @param reader the Endeca <code>RecordStoreReader</code>.
     */
    public RecordReader(RecordStoreReader reader) {
        this.recordStoreReader = reader;
    }

    /**
     * Returns a count of how many records have been iterated over.
     *
     * @return a count of records read.
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns <code>true</code> if there are more records to read.
     *
     * @return <code>true</code> if more records exist; <code>false</code> otherwise.
     */
    @Override
    public boolean hasNext() {
        boolean hasNext = false;
        try {
            if (recordReader == null) {
                hasNext = recordStoreReader.hasNext();
            }
            else if (recordStoreReader == null) {
                hasNext = recordReader.hasNext();
            }
        }
        catch (IOException | RecordStoreException e) {
            log.error("Unable to test for more records.", e);
        }
        return hasNext;
    }

    /**
     * Returns the next record in the record reader.
     *
     * @return the next record, or <code>null</code> if no more records exist.
     */
    @Override
    public Record next() {
        Record next = null;
        try {
            if (recordReader == null) {
                next = recordStoreReader.next();
            }
            else if (recordStoreReader == null) {
                next = recordReader.next();
            }
            count++;
        }
        catch (IOException | RecordStoreException e) {
            log.error("Unable to read next record.", e);
        }
        return next;
    }

    /**
     * Close the record reader to free resources.
     */
    public void close() {
        if (recordReader == null) {
            EndecaHelper.close(recordStoreReader);
        }
        else if (recordStoreReader == null) {
            EndecaHelper.close(recordReader);
        }
    }
}
