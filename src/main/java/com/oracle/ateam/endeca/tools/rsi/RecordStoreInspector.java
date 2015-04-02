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
package com.oracle.ateam.endeca.tools.rsi;

import com.endeca.itl.recordstore.GenerationId;
import com.endeca.itl.recordstore.RecordStore;
import com.oracle.ateam.endeca.tools.rsi.component.FileOpenDialog;
import com.oracle.ateam.endeca.tools.rsi.component.RecordStoreTab;
import com.oracle.ateam.endeca.tools.rsi.javafx.FXMLControl;
import com.oracle.ateam.endeca.tools.rsi.service.ExportService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.oracle.ateam.endeca.tools.rsi.javafx.FXMLResource.*;
import static com.oracle.ateam.endeca.tools.rsi.util.FXHelper.createProgressDialog;

/**
 * The main driver for the Endeca RecordStore Inspector application.
 *
 * @author Jim Prantzalos
 * @version 1.0
 * @since R1.0
 */
public class RecordStoreInspector extends Application {
    private transient static final Logger log = LoggerFactory.getLogger(RecordStoreInspector.class);
    private Stage primaryStage;
    private FileOpenDialog fileOpenDialog;
    private Stage settingsDialog;
    private Stage aboutDialog;

    @FXML
    private TabPane mainTabPane;

    /**
     * The main entry point for this JavaFX application.
     *
     * @param primaryStage the primary stage for this application.
     * @throws Exception
     */
    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Scene scene = FXMLControl.load(FXML_MAIN).buildScene();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Record Store Inspector");
        primaryStage.setMinWidth(780);
        primaryStage.setMinHeight(490);
        primaryStage.show();
    }

    /**
     * Called when the "File -> Open Record Store" menu option is selected.
     */
    @FXML
    protected void onFileOpen(final ActionEvent event) {
        if (event.getSource() instanceof MenuItem) {
            if (fileOpenDialog == null) {
                fileOpenDialog = (FileOpenDialog) FXMLControl.load(FXML_FILE_OPEN).buildStage();
            }
            fileOpenDialog.showAndWait();

            if (fileOpenDialog.getRecordGenerationsPane() != null) {
                final RecordStore recStore = fileOpenDialog.locateRecordStore();
                final String recStoreName = fileOpenDialog.getSelectedRecordStoreName();
                final GenerationId genId = fileOpenDialog.getRecordGenerationsPane().getSelectedGenerationId();

                // Read in from selected record store
                if (genId != null) {
                    log.info("FileOpen: recordStore=" + recStoreName + ", generationId=" + genId);
                    RecordStoreTab tab = new RecordStoreTab(recStoreName);
                    tab = FXMLControl.load(FXML_MAIN_TAB, tab, tab).getRoot();
                    mainTabPane.getTabs().add(0, tab);
                    mainTabPane.getSelectionModel().select(tab);
                    tab.populateTableView(recStore, fileOpenDialog);
                }
            }
        }
    }

    /**
     * Called when the "File -> Open Record XML" menu option is selected.
     */
    @FXML
    protected void onFileOpenXML(final ActionEvent event) {
        if (event.getSource() instanceof MenuItem) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open XML Records File");

            // Set allowable extensions and open dialog
            FileChooser.ExtensionFilter extFilterGz = new FileChooser.ExtensionFilter("GZipped XML Records (*.gz)", "*.gz");
            FileChooser.ExtensionFilter extFilterXml = new FileChooser.ExtensionFilter("XML Records File (*.xml)", "*.xml");
            fileChooser.getExtensionFilters().addAll(extFilterGz, extFilterXml);
            File dataFile = fileChooser.showOpenDialog(primaryStage);

            // Read in from selected file
            if (dataFile != null) {
                log.info("FileOpenXML: dataFile=" + dataFile);
                final String fileName = dataFile.getName().substring(0, dataFile.getName().indexOf('.'));
                RecordStoreTab tab = new RecordStoreTab(fileName);
                tab = FXMLControl.load(FXML_MAIN_TAB, tab, tab).getRoot();
                mainTabPane.getTabs().add(0, tab);
                mainTabPane.getSelectionModel().select(tab);
                tab.populateTableView(dataFile);
            }
        }
    }

    /**
     * Called when the "File -> Export To File" menu option is selected.
     */
    @FXML
    protected void onFileExport(final ActionEvent event) {
        if (event.getSource() instanceof MenuItem && mainTabPane.getTabs().size() > 0) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export To File");

            // Set allowable extensions and open save dialog
            FileChooser.ExtensionFilter extFilterCsv = new FileChooser.ExtensionFilter("CSV Text File (*.csv)", "*.csv");
            FileChooser.ExtensionFilter extFilterXls = new FileChooser.ExtensionFilter("Microsoft Excel File (*.xls)", "*.xls");
            FileChooser.ExtensionFilter extFilterXml = new FileChooser.ExtensionFilter("Record XML File (*.xml)", "*.xml");
            fileChooser.getExtensionFilters().addAll(extFilterXls, extFilterCsv, extFilterXml);
            File exportFile = fileChooser.showSaveDialog(primaryStage);

            // Export to specified file
            if (exportFile != null) {
                log.info("FileExport: exportFile=" + exportFile);
                Tab firstTab = mainTabPane.getTabs().get(0);
                if (firstTab instanceof RecordStoreTab) {
                    RecordStoreTab tab = (RecordStoreTab) firstTab;
                    ExportService service = new ExportService(exportFile, tab.getService().getServiceInfo());

                    // Show progress dialog and start service
                    Stage progressDialog = createProgressDialog("Saving ...", service, primaryStage);
                    progressDialog.show();
                    service.reset();
                    service.start();
                }
            }
        }
    }

    /**
     * Called when the "File -> Exit" menu option is selected.
     */
    @FXML
    protected void onFileExit(final ActionEvent event) {
        if (event.getSource() instanceof MenuItem) {
            Platform.exit();
        }
    }

    /**
     * Called when the "View -> Settings" menu option is selected.
     */
    @FXML
    protected void onViewSettings(final ActionEvent event) {
        if (event.getSource() instanceof MenuItem) {
            if (settingsDialog == null) {
                settingsDialog = FXMLControl.load(FXML_SETTINGS).buildStage();
            }
            settingsDialog.showAndWait();
        }
    }

    /**
     * Called when the "Help -> About" menu option is selected.
     */
    @FXML
    protected void onHelpAbout(final ActionEvent event) {
        if (event.getSource() instanceof MenuItem) {
            if (aboutDialog == null) {
                aboutDialog = FXMLControl.load(FXML_ABOUT).buildStage();
            }
            aboutDialog.showAndWait();
        }
    }

    /**
     * Launch the app.
     */
    public static void main(final String[] args) {
        launch(args);
    }
}
