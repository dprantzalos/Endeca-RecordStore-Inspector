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

import com.endeca.itl.recordstore.RecordStore;
import com.oracle.ateam.endeca.tools.rsi.javafx.FXMLControl;
import com.oracle.ateam.endeca.tools.rsi.service.RecordReaderService;
import com.oracle.ateam.endeca.tools.rsi.service.ServiceInfo;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;

import static com.oracle.ateam.endeca.tools.rsi.javafx.FXMLResource.FXML_RECORD_DATA;
import static com.oracle.ateam.endeca.tools.rsi.util.FXHelper.createProgressDialog;

/**
 * Controller for RecordStore tabs.
 *
 * @author Jim Prantzalos
 * @version $Revision$ $Date$
 * @since R1.0
 */
public class RecordStoreTab extends Tab {

    @FXML
    private BorderPane borderPane;

    @FXML
    private Label recordCountLabel;

    @FXML
    private TextField filterColumnText;

    @FXML
    private TextField filterValueText;

    @FXML
    private Button filterButton;

    @FXML
    private TableView<ObservableList<StringProperty>> tableView;

    private RecordDetailsDialog recordDetailsDialog;

    private RecordReaderService service;

    public RecordStoreTab(final String title) {
        setText(title);
    }

    public RecordReaderService getService() {
        return service;
    }

    protected void applyFilters() {
        if (service.getServiceInfo().setFilterPatterns(filterColumnText.getText(), filterValueText.getText())) {
            populateTableView(service);
        }
    }

    public void populateTableView(final RecordStore recordStore, final FileOpenDialog fileOpenDialog) {
        ServiceInfo serviceInfo = new ServiceInfo(this);
        serviceInfo.setExtraInfo(recordStore, fileOpenDialog);
        populateTableView(new RecordReaderService(serviceInfo));
    }

    public void populateTableView(final File dataFile) {
        ServiceInfo serviceInfo = new ServiceInfo(this);
        serviceInfo.setExtraInfo(dataFile);
        populateTableView(new RecordReaderService(serviceInfo));
    }

    public void populateTableView(final RecordReaderService service) {
        this.service = service;
        recordCountLabel.setText("0");
        tableView.getColumns().clear();
        tableView.getItems().clear();

        // Set event handlers for filtering
        this.service.setOnSucceeded(event -> onServiceCompleted(tableView = service.getValue()));

        // Show progress dialog and start service
        Stage progressDialog = createProgressDialog("Loading ...", service, getTabPane().getScene().getWindow());
        progressDialog.show();
        service.reset();
        service.start();
    }

    protected void onServiceCompleted(TableView<ObservableList<StringProperty>> tableView) {
        this.tableView = tableView;
        borderPane.setCenter(tableView);
        recordCountLabel.setText(String.valueOf(tableView.getItems().size()));

        // Allow double-click on selected row
        tableView.setRowFactory(view -> {
            final TableRow<ObservableList<StringProperty>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    onRowSelected(row.getItem());
                }
            });
            return row;
        });
    }

    protected void onRowSelected(ObservableList<StringProperty> selectedRow) {
        if (selectedRow != null) {
            if (recordDetailsDialog == null) {
                recordDetailsDialog = (RecordDetailsDialog) FXMLControl.load(FXML_RECORD_DATA).buildStage();
            }
            recordDetailsDialog.updateTableData(service.transposeRowData(selectedRow));
            recordDetailsDialog.showAndWait();
        }
    }

    @FXML
    protected void onApplyFilterButtonClicked(final ActionEvent event) {
        if (filterButton.equals(event.getSource())) {
            applyFilters();
        }
    }

    @FXML
    protected void initialize() {
        // Apply filters if Enter key is pressed from text fields
        filterColumnText.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                applyFilters();
            }
        });
        filterValueText.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                applyFilters();
            }
        });
    }
}
