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

/**
 * An enumeration of supported FXML resources.
 *
 * @author Jim Prantzalos
 * @version 1.0
 * @since R1.0
 */
public enum FXMLResource {
    FXML_MAIN("/fxml/main.fxml"),
    FXML_MAIN_TAB("/fxml/main_tab.fxml"),
    FXML_FILE_OPEN("/fxml/dialogs/file_open.fxml"),
    FXML_GENERATIONS("/fxml/dialogs/gen_select.fxml"),
    FXML_PROGRESS_BAR("/fxml/dialogs/progress_bar.fxml"),
    FXML_RECORD_DATA("/fxml/dialogs/record_data.fxml"),
    FXML_SETTINGS("/fxml/dialogs/view_settings.fxml"),
    FXML_ABOUT("/fxml/dialogs/help_about.fxml");

    private String path;

    FXMLResource(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        return path;
    }
}
