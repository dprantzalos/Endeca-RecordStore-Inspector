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

import com.endeca.itl.component.manager.ComponentInstanceDescriptor;
import com.endeca.itl.component.manager.ComponentManagerException;
import com.endeca.itl.recordstore.GenerationInfo;
import com.endeca.itl.recordstore.RecordStore;
import com.endeca.itl.recordstore.RecordStoreException;
import com.oracle.ateam.endeca.tools.rsi.data.NameValueData;
import com.oracle.ateam.endeca.tools.rsi.javafx.FXMLControl;
import com.oracle.ateam.endeca.tools.rsi.util.EndecaHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static com.oracle.ateam.endeca.tools.rsi.javafx.FXMLResource.FXML_GENERATIONS;
import static com.oracle.ateam.endeca.tools.rsi.util.EndecaHelper.locateComponentInstanceManager;
import static com.oracle.ateam.endeca.tools.rsi.util.FXHelper.makeWrappable;

/**
 * Controller for File/Open dialog.
 *
 * @author Jim Prantzalos
 * @version $Revision$ $Date$
 * @since R1.0
 */
public class FileOpenDialog extends PopupDialog {
    private transient static final Logger log = LoggerFactory.getLogger(FileOpenDialog.class);
    public static final String TITLE = "Select RecordStore";
    public static final String COMPONENT_TYPE_RECORDSTORE = "RecordStore";

    private RecordGenerationsPane recordGenerationsPane;

    @FXML
    private TableView<NameValueData> tableView;

    @FXML
    private TableColumn<NameValueData, String> name;

    @FXML
    private TableColumn<NameValueData, String> status;

    @FXML
    private Button openRecordStoreButton;

    public FileOpenDialog() {
        super(TITLE);
    }

    public String getSelectedRecordStoreName() {
        NameValueData selectedItem = tableView.getSelectionModel().getSelectedItem();
        return (selectedItem != null) ? selectedItem.getName() : null;
    }

    public RecordStore locateRecordStore() {
        RecordStore recordStore = null;
        String selectedRecStoreName = getSelectedRecordStoreName();
        if (selectedRecStoreName != null) {
            try {
                recordStore = EndecaHelper.locateRecordStore(selectedRecStoreName);
            } catch (IOException e) {
                log.error("Unable to open RecordStore '"+selectedRecStoreName+"'", e);
            }
        }
        return recordStore;
    }

    public RecordGenerationsPane getRecordGenerationsPane() {
        return recordGenerationsPane;
    }

    @Override
    public void resetState() {
        recordGenerationsPane = null;
        tableView.getSelectionModel().clearSelection();
        openRecordStoreButton.requestFocus();
    }

    @Override
    public void showAndWait() {
        resetState();
        populateTableView();
        sizeToScene();
        openRecordStoreButton.requestFocus();
        super.showAndWait();
    }

    protected void populateTableView() {
        if (tableView.getItems().size() > 0) {
            tableView.getItems().clear();
        }
        List<ComponentInstanceDescriptor> componentInstances = null;
        try {
            componentInstances = locateComponentInstanceManager().listComponentInstances();
        }
        catch (ComponentManagerException | IOException e) {
            log.error("Unable to locate Component Instance Manager.", e);
        }
        if (componentInstances != null) {
            ObservableList<NameValueData> data = FXCollections.observableArrayList();
            for (ComponentInstanceDescriptor descriptor: componentInstances) {
                if (COMPONENT_TYPE_RECORDSTORE.equals(descriptor.getTypeId().getId())) {
                    //if (descriptor.getInstanceStatus() == ComponentInstanceStatus.RUNNING)
                    data.add(new NameValueData(descriptor.getInstanceId().getId(), descriptor.getInstanceStatus().name()));
                }
            }
            tableView.setItems(data);
        }
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    protected void onOpenRecordStoreClicked(final ActionEvent event) {
        if (event.getSource() instanceof Button) {
            // Get selected RecordStore
            NameValueData selectedItem = tableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Stage stage = (Stage) openRecordStoreButton.getScene().getWindow();
                stage.setTitle(RecordGenerationsPane.TITLE);
                onRecordStoreSelected(selectedItem.getName());
            }
        }
    }

    @FXML
    protected void onCancelButtonClicked(final ActionEvent event) {
        if (event.getSource() instanceof Button) {
            resetState();
            Stage stage = (Stage) openRecordStoreButton.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Load the generations for selected RecordStore and change the scene.
     */
    protected void onRecordStoreSelected(final String selectedRecStoreName) {
        final RecordStore recordStore = locateRecordStore();
        if (recordStore != null) {
            List<GenerationInfo> generations = null;
            try {
                generations = recordStore.listGenerations();
            } catch (RecordStoreException e) {
                log.error("Unable to retrieve generations for RecordStore '" + selectedRecStoreName + "'", e);
            }
            // Switch scene to RecordStore Generation selection
            FXMLControl<RecordGenerationsPane> fxml = FXMLControl.load(FXML_GENERATIONS);
            fxml.getController().setPriorScene(getScene());
            setScene(fxml.buildScene());
            sizeToScene();

            // Make loaded generations visible
            recordGenerationsPane = fxml.getController();
            recordGenerationsPane.populateTableView(generations);
        }
    }

    @FXML
    protected void initialize() {
        // Set cell handlers for view data
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        status.setCellValueFactory(new PropertyValueFactory<>("value"));

        // Set listeners to allow for text wrapping on column
        makeWrappable(name, name.getPrefWidth() - 5);
        makeWrappable(status, status.getPrefWidth() - 5);

        // Allow double-click on selected row
        tableView.setRowFactory(view -> {
            final TableRow<NameValueData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    onRecordStoreSelected(row.getItem().getName());
                }
            });
            return row;
        });
    }
}
