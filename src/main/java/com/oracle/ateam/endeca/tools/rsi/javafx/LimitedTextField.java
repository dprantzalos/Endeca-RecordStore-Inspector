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
package com.oracle.ateam.endeca.tools.rsi.javafx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;

/**
 * A specialization of JavaFX TextField that limits the size and types of characters allowed.
 *
 * @author Jim Prantzalos
 * @version 1.0
 * @since R1.0
 */
public class LimitedTextField extends TextField {
    private IntegerProperty maxLength = new SimpleIntegerProperty(this, "maxLength", 0);
    private StringProperty constraint = new SimpleStringProperty(this, "constraint");

    public LimitedTextField() {
        textProperty().addListener((observableValue, oldval, newval) -> {
            if (newval != null) {
                synchronized (maxLength) {
                    if (maxLength.get() > 0 && newval.length() > maxLength.get()) {
                        setText(newval.substring(0, maxLength.get()));
                    }
                    if (constraint.get() != null && !constraint.get().equals("") && !newval.matches(constraint.get())) {
                        setText(oldval);
                    }
                }
            }
        });
    }

    /**
     * Returns the max length property.
     *
     * @return the max length property.
     */
    public IntegerProperty maxLengthProperty() {
        return maxLength;
    }

    /**
     * Returns the max allowable length of the text field.
     *
     * @return the max length allowed.
     */
    public int getMaxLength() {
        return maxLength.get();
    }

    /**
     * Sets the max allowable length of the text field.
     *
     * @param maxLength the max length allowed.
     */
    public void setMaxLength(int maxLength) {
        this.maxLength.set(maxLength);
    }

    /**
     * Returns the constraint property.
     *
     * @return the constraint property.
     */
    public StringProperty constraintProperty() {
        return constraint;
    }

    /**
     * Returns the regular expression used to constrain user input.
     *
     * @return the regular expression for constraining input.
     */
    public String getConstraint() {
        return constraint.get();
    }

    /**
     * Sets a regular expression used to constrain user input. For example, [0-9]* to allow numeric values only.
     *
     * @param constraint the regular expression for constraining input.
     */
    public void setConstraint(String constraint) {
        this.constraint.set(constraint);
    }
}
