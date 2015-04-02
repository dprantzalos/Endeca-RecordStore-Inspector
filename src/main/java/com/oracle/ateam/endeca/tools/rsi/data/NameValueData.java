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
package com.oracle.ateam.endeca.tools.rsi.data;

import javafx.beans.property.SimpleStringProperty;

/**
 * A data holder for name/value pairs.
 *
 * @author Jim Prantzalos
 * @version $Revision$ $Date$
 * @since R1.0
 */
public class NameValueData implements Comparable<NameValueData> {
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty value = new SimpleStringProperty();

    /**
     * Construct a data object using the specified name and value.
     */
    public NameValueData(String name, String value) {
        this.name.setValue(name);
        this.value.setValue(value);
    }

    /**
     * Returns the name.
     */
    public String getName() {
        return name.get();
    }

    /**
     * Returns the value.
     */
    public String getValue() {
        return value.get();
    }

    /**
     * Performs a string comparison based on name attribute.
     *
     * @param data the <code>NameValueData</code> object to compare against
     * @return 0 if the names are equal; less than 0 if name is lexicographically less than data.name; greater than 0 otherwise.
     */
    @Override
    public int compareTo(NameValueData data) {
        return getName().compareTo(data.getName());
    }
}
