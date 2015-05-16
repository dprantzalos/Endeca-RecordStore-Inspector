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

import com.endeca.itl.component.manager.ComponentInstanceManager;
import com.endeca.itl.component.manager.ComponentInstanceManagerLocator;
import com.endeca.itl.record.PropertyValue;
import com.endeca.itl.recordstore.*;
import com.oracle.ateam.endeca.tools.rsi.AppSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A utility class for common Endeca operations.
 *
 * @author Jim Prantzalos
 * @version 1.0
 * @since R1.0
 */
public class EndecaHelper {
    private transient static final Logger log = LoggerFactory.getLogger(EndecaHelper.class);

    /**
     * Locate and return the specified RecordStore. If the RecordStore is not found, a new one is created.
     */
    public static RecordStore locateRecordStore(final String instanceName) throws IOException {
        String host = AppSettings.getCasServerHost();
        int port = AppSettings.getCasServerPort();
        boolean useSSL = AppSettings.isCasServerSSLEnabled();
        RecordStoreLocator locator = RecordStoreLocator.create(host, port, instanceName);
        locator.setPortSsl(useSSL);
        log.debug("Locating RecordStore '" + instanceName + "' on CAS Server " + host + ":" + port + " (ssl=" + useSSL + ")");
        locator.ping();
        return locator.getService();
    }

    /**
     * Locate and return the component instance manager.
     */
    public static ComponentInstanceManager locateComponentInstanceManager() throws IOException {
        String host = AppSettings.getCasServerHost();
        int port = AppSettings.getCasServerPort();
        boolean useSSL = AppSettings.isCasServerSSLEnabled();
        ComponentInstanceManagerLocator locator = ComponentInstanceManagerLocator.create(host, port);
        locator.setPortSsl(useSSL);
        log.debug("Locating Component Instance Manager on " + host + ":" + port + " (ssl=" + useSSL + ")");
        locator.ping();
        return locator.getService();
    }

    /**
     * Close the <code>RecordStoreReader</code>, and log any errors.
     *
     * @param reader the <code>RecordStoreReader</code> to be closed.
     */
    public static void close(final RecordStoreReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (RecordStoreException e) {
                log.error("Unable to close record store reader.", e);
            }
        }
    }

    /**
     * Close the <code>RecordReader</code>, and log any errors.
     *
     * @param reader the <code>RecordReader</code> to be closed.
     */
    public static void close(final com.endeca.itl.record.io.RecordReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Unable to close record reader.", e);
            }
        }
    }

    /**
     * Close the <code>RecordReader</code>, and log any errors.
     *
     * @param reader the <code>RecordReader</code> to be closed.
     */
    public static void close(final RecordReader reader) {
        if (reader != null) {
            reader.close();
        }
    }

    /**
     * Rollback the transaction for the specified RecordStore, and log any errors.
     */
    public static void rollbackTransaction(final RecordStore recordStore, final TransactionId txId) {
        if (recordStore !=null && txId != null) {
            try {
                log.debug("Rolling back transaction.");
                recordStore.rollbackTransaction(txId);
            } catch (RecordStoreException e) {
                log.error("Failed to roll back transaction.", e);
            }
        }
    }

    /**
     * Flattens out the given array of <code>PropertyValue</code> objects into a delimited string,
     * using the specified delimiter symbol to separate values.
     *
     * @param propertyValues the array of <code>PropertyValue</code> objects to turn into a string.
     * @param delimiter the property value separator to use in the string.
     * @return a flattened out delimited string.
     */
    public static String makeString(final PropertyValue[] propertyValues, final char delimiter) {
        StringBuilder buf = null;
        for (PropertyValue pval: propertyValues) {
            if (buf == null) {
                buf = new StringBuilder(pval.getValue());
            }
            else {
                buf.append(delimiter).append(pval.getValue());
            }
            if (buf.length() > 256) break;
        }
        if (buf == null) {
            buf = new StringBuilder();
        }
        // cap length of string
        int len = buf.length();
        if (len > 256) {
            buf.replace(252, len, " ...");
        }
        return buf.toString();
    }
}
