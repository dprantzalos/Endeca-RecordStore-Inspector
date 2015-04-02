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

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A convenience wrapper around {@link FXMLLoader} that only accepts <code>FXMLResource</code> resources.
 *
 * @param <T> the controller and root type.
 *
 * @author Jim Prantzalos
 * @version $Revision$ $Date$
 * @since R1.0
 */
public class FXMLControl<T> {
    private transient static final Logger log = LoggerFactory.getLogger(FXMLControl.class);
    private FXMLLoader loader;
    private Parent parent;

    /**
     * Constructs a <code>FXMLControl</code> from the specified resource.
     *
     * @param fxmlResource the xml resource to load from.
     */
    public FXMLControl(final FXMLResource fxmlResource) {
        this(fxmlResource, null);
    }

    public FXMLControl(final FXMLResource fxmlResource, final T controller) {
        this(fxmlResource, controller, null);
    }

    public FXMLControl(final FXMLResource fxmlResource, final T controller, final T root) {
        loader = new FXMLLoader(getClass().getResource(fxmlResource.getPath()));
        if (controller != null) {
            loader.setController(controller);
        }
        if (root != null) {
            loader.setRoot(root);
        }
        try {
            Object obj = loader.load();
            if (obj instanceof Parent) {
                parent = (Parent) obj;
            }
        }
        catch (IOException e) {
            log.error("Unable to load scene from resource '" + fxmlResource + "'", e);
        }
    }

    /**
     * Returns the loader used to load the resource.
     *
     * @return the loader.
     */
    public FXMLLoader getLoader() {
        return loader;
    }

    /**
     * Returns the controller assigned to the loader.
     *
     * @return the controller.
     */
    public T getController() {
        return loader.getController();
    }

    /**
     * Returns the root object assigned to the loader.
     *
     * @return the root object.
     */
    public T getRoot() {
        return loader.getRoot();
    }

    public Scene buildScene() {
        return (parent != null) ? new Scene(parent) : null;
    }

    public Stage buildStage() {
        Stage stage = null;
        if (parent != null) {
            stage = loader.getController();
            stage.setScene(new Scene(parent));
        }
        return stage;
    }

    public static <T> FXMLControl<T> load(final FXMLResource fxmlResource) {
        return new FXMLControl<>(fxmlResource);
    }

    public static <T> FXMLControl<T> load(final FXMLResource fxmlResource, final T controller) {
        return new FXMLControl<>(fxmlResource, controller);
    }

    public static <T> FXMLControl<T> load(final FXMLResource fxmlResource, final T controller, final T root) {
        return new FXMLControl<>(fxmlResource, controller, root);
    }
}
