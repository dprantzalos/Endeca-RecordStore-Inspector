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
package com.oracle.ateam.endeca.tools.rsi.component;

import com.oracle.ateam.endeca.tools.rsi.data.NameValueData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import static com.oracle.ateam.endeca.tools.rsi.util.FXHelper.makeWrappable;

/**
 * Controller for Help/About dialog.
 *
 * @author Jim Prantzalos
 * @version $Revision$ $Date$
 * @since R1.0
 */
public class AboutDialog extends PopupDialog {
    public static final String TITLE = "About";
    private static final String[] systemProperties = new String[] {
            "java.vm.vendor",
            "java.vm.name",
            "java.vm.version",
            "java.runtime.name",
            "java.runtime.version",
            "java.home",
            "java.version",
            "java.class.path",
            "os.name",
            "os.arch",
    };

    @FXML
    private TableView<NameValueData> aboutTable;

    @FXML
    private TableColumn<NameValueData, String> propertyId;

    @FXML
    private TableColumn<NameValueData, String> valueId;

    @FXML
    private Button okButton;

    public AboutDialog() {
        super(TITLE);
    }

    @Override
    public void resetState() {
        okButton.requestFocus();
    }

    @Override
    public void showAndWait() {
        resetState();
        super.showAndWait();
    }

    @FXML
    protected void onOkButtonClicked(final ActionEvent event) {
        if (okButton.equals(event.getSource())) {
            close();
        }
    }

    @FXML
    protected void initialize() {
        // Set cell handlers for view data
        propertyId.setCellValueFactory(new PropertyValueFactory<>("name"));
        valueId.setCellValueFactory(new PropertyValueFactory<>("value"));

        // Set listeners to allow for text wrapping on column
        makeWrappable(propertyId, propertyId.getPrefWidth() - 5);
        makeWrappable(valueId, valueId.getPrefWidth() - 5);

        // Load some data into the view
        ObservableList<NameValueData> data = FXCollections.observableArrayList();
        for (String systemProperty: systemProperties) {
            data.add(new NameValueData(systemProperty, System.getProperty(systemProperty)));
        }
        aboutTable.setItems(data);
    }
}
