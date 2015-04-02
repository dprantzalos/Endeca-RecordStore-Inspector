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
package com.oracle.ateam.endeca.tools.rsi.util;

import com.oracle.ateam.endeca.tools.rsi.component.ProgressDialog;
import com.oracle.ateam.endeca.tools.rsi.javafx.FXMLControl;
import com.oracle.ateam.endeca.tools.rsi.javafx.WrappableTableCell;
import javafx.concurrent.Service;
import javafx.scene.control.TableColumn;
import javafx.stage.Window;

import static com.oracle.ateam.endeca.tools.rsi.javafx.FXMLResource.FXML_PROGRESS_BAR;

/**
 * A utility class for common JavaFX operations.
 *
 * @author Jim Prantzalos
 * @version $Revision$ $Date$
 * @since R1.0
 */
public class FXHelper {

    /**
     * Creates a dialog window for displaying service progress.
     *
     * @param title the title of the dialog window.
     * @param service the service to monitor progress of.
     * @param owner the window that owns the progress dialog.
     * @return the progress dialog.
     */
    public static ProgressDialog createProgressDialog(final String title, final Service<?> service, final Window owner) {
        ProgressDialog dialog = new ProgressDialog(service, owner);
        dialog = FXMLControl.load(FXML_PROGRESS_BAR, dialog, dialog).getRoot();
        dialog.setTitle(title);
        return dialog;
    }

    /**
     * Overrides the cell factory of a column to support text wrapping using the class {@link WrappableTableCell}.
     *
     * @param column the column to be made wrappable.
     * @param initialWidth the initial width of the column.
     * @param <T> the tableview type used by this column.
     */
    public static <T> void makeWrappable(final TableColumn<T, String> column, final double initialWidth) {
        column.setCellFactory(col -> {
            final WrappableTableCell<T> cell = new WrappableTableCell<>(initialWidth);
            col.widthProperty().addListener(cell);
            return cell;
        });
    }
}
