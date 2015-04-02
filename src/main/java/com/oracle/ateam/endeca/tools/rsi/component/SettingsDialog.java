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

import com.oracle.ateam.endeca.tools.rsi.AppSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

/**
 * Controller for View/Settings dialog.
 *
 * @author Jim Prantzalos
 * @version $Revision$ $Date$
 * @since R1.0
 */
public class SettingsDialog extends PopupDialog {
    public static final String TITLE = "Settings";

    @FXML
    private TextField casServerHost;

    @FXML
    private TextField casServerPort;

    @FXML
    private CheckBox casServerSSLEnabled;

    @FXML
    private Button cancelButton;

    public SettingsDialog() {
        super(TITLE);
        setWidth(320);
    }

    @Override
    public void resetState() {
        casServerHost.requestFocus();
        casServerHost.setText(AppSettings.getCasServerHost());
        casServerPort.setText(String.valueOf(AppSettings.getCasServerPort()));
        casServerSSLEnabled.setSelected(AppSettings.isCasServerSSLEnabled());
    }

    @Override
    public void showAndWait() {
        resetState();
        super.showAndWait();
    }

    @FXML
    protected void onOkButtonClicked(final ActionEvent event) {
        if (event.getSource() instanceof Button) {
            AppSettings.setCasServerHost(casServerHost.getText());
            AppSettings.setCasServerPort(Integer.parseInt(casServerPort.getText()));
            AppSettings.setCasServerSSLEnabled(casServerSSLEnabled.isSelected());
            close();
        }
    }

    @FXML
    protected void onCancelButtonClicked(final ActionEvent event) {
        if (event.getSource() instanceof Button) {
            close();
        }
    }
}
