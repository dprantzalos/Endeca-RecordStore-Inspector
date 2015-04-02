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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Controller for Record Details dialog.
 *
 * @author Jim Prantzalos
 * @version 1.0
 * @since R1.0
 */
public class RecordDetailsDialog extends PopupDialog {
    private transient static final Logger log = LoggerFactory.getLogger(RecordDetailsDialog.class);
    public static final String TITLE = "View Record";

    private ObservableList<NameValueData> tableData;

    @FXML
    private TableView<NameValueData> tableView;

    @FXML
    private TableColumn<NameValueData, String> nameId;

    @FXML
    private TableColumn<NameValueData, String> valueId;

    @FXML
    private Button closeButton;

    @FXML
    private Label columnCountLabel;

    @FXML
    private TextField filterText;

    @FXML
    private Button filterButton;

    private Pattern filterTextPattern;

    public RecordDetailsDialog() {
        super(TITLE);
    }

    @Override
    public void resetState() {
        filterText.setText("");
        filterTextPattern = null;
        tableView.getSelectionModel().clearSelection();
        filterButton.requestFocus();
    }

    @Override
    public void showAndWait() {
        resetState();
        super.showAndWait();
    }

    public void updateTableData(ObservableList<NameValueData> data) {
        tableData = data;
        tableView.getItems().setAll(data);
        columnCountLabel.setText(String.valueOf(data.size()));
    }

    @FXML
    protected void onApplyFilterButtonClicked(final ActionEvent event) {
        if (filterButton.equals(event.getSource())) {
            String text = filterText.getText();
            boolean filterChanged;
            if (text.trim().length() > 0) {
                filterChanged = filterTextPattern == null || !text.equals(filterTextPattern.pattern());
                if (filterChanged) {
                    filterTextPattern = Pattern.compile(text);
                    log.debug("Row text filter set: " + filterTextPattern);
                }
            }
            else {
                filterChanged = filterTextPattern != null;
                if (filterChanged) {
                    filterTextPattern = null;
                    log.debug("Row text filter cleared");
                }
            }
            ObservableList<NameValueData> filteredData = FXCollections.observableArrayList();
            for (NameValueData nv: tableData) {
                if (filterTextPattern == null) {
                    filteredData.add(nv);
                } else if (nv.getName() != null && filterTextPattern.matcher(nv.getName()).matches()) {
                    filteredData.add(nv);
                } else if (nv.getValue() != null && filterTextPattern.matcher(nv.getValue()).matches()) {
                    filteredData.add(nv);
                }
            }
            tableView.getItems().setAll(filteredData);
            columnCountLabel.setText(String.valueOf(filteredData.size()));
        }
    }

    @FXML
    protected void onCloseButtonClicked(final ActionEvent event) {
        if (event.getSource() instanceof Button) {
            Button closeButton = (Button) event.getSource();
            if (closeButton.getScene().getWindow() instanceof Stage) {
                Stage stage = (Stage) closeButton.getScene().getWindow();
                stage.close();
            }
        }
    }

    @FXML
    protected void initialize() {
        // Set cell handlers for view data
        nameId.setCellValueFactory(new PropertyValueFactory<>("name"));
        valueId.setCellValueFactory(new PropertyValueFactory<>("value"));
    }
}
