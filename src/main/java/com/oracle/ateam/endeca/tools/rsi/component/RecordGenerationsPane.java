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

import com.endeca.itl.recordstore.GenerationId;
import com.endeca.itl.recordstore.GenerationInfo;
import com.endeca.itl.recordstore.GenerationStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Record Generations pane.
 *
 * @author Jim Prantzalos
 * @version $Revision$ $Date$
 * @since R1.0
 */
public class RecordGenerationsPane extends BorderPane {
    public static final String TITLE = "Select Generation";

    private Scene priorScene;

    @FXML
    private TableView<GenerationInfo> tableView;

    @FXML
    private TableColumn<GenerationInfo, GenerationId> genId;

    @FXML
    private TableColumn<GenerationInfo, Date> createdOn;

    @FXML
    private TableColumn<GenerationInfo, GenerationStatus> status;

    @FXML
    private CheckBox useBaselineRead;

    @FXML
    private Button selectGenerationButton;

    public RecordGenerationsPane() {
    }

    public void setPriorScene(Scene priorScene) {
        this.priorScene = priorScene;
    }

    public GenerationId getSelectedGenerationId() {
        GenerationInfo selectedItem = tableView.getSelectionModel().getSelectedItem();
        return (selectedItem != null) ? tableView.getSelectionModel().getSelectedItem().getGenerationId() : null;
    }

    public boolean isUseBaselineReadSelected() {
        return useBaselineRead.isSelected();
    }

    public void resetState() {
        tableView.getSelectionModel().clearSelection();
        selectGenerationButton.requestFocus();
    }

    public void populateTableView(List<GenerationInfo> generations) {
        ObservableList<GenerationInfo> data = FXCollections.observableArrayList();
        data.addAll(generations.stream().collect(Collectors.toList()));
        tableView.setItems(data);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    protected void updateSelectedGeneration() {
        Stage stage = (Stage) selectGenerationButton.getScene().getWindow();
        stage.setScene(priorScene);
        stage.close();
    }

    @FXML
    protected void onGenerationSelected(final ActionEvent event) {
        if (selectGenerationButton.equals(event.getSource())) {
            updateSelectedGeneration();
        }
    }

    @FXML
    protected void onCancelButtonClicked(final ActionEvent event) {
        if (event.getSource() instanceof Button) {
            resetState();
            // return to previous scene
            Stage stage = (Stage) selectGenerationButton.getScene().getWindow();
            stage.setTitle(FileOpenDialog.TITLE);
            stage.setScene(priorScene);
            stage.sizeToScene();
        }
    }

    @FXML
    protected void initialize() {
        // Set cell handlers for view data
        genId.setCellValueFactory(new PropertyValueFactory<>("generationId"));
        createdOn.setCellValueFactory(new PropertyValueFactory<>("creationTime"));
        status.setCellValueFactory(new PropertyValueFactory<>("generationStatus"));

        // Allow double-click on selected row
        tableView.setRowFactory(view -> {
            final TableRow<GenerationInfo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !row.isEmpty()) {
                        updateSelectedGeneration();
                    }
            });
            return row;
        });
    }
}
