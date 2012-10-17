/* 
 * This file is part of Quelea, free projection software for churches.
 * Copyright (C) 2011 Michael Berry
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.quelea.windows.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.javafx.dialog.Dialog;
import org.quelea.Theme;
import org.quelea.languages.LabelGrabber;
import org.quelea.utils.LoggerUtils;
import org.quelea.windows.newsong.EditThemeDialog;
import org.quelea.windows.newsong.ThemePanel;

/**
 * Panel that displays a preview of a particular theme. This is part of the
 * theme select popup window.
 * <p/>
 * @author Michael
 */
public class ThemePreviewPanel extends VBox {

    private static final Logger LOGGER = LoggerUtils.getLogger();
    private Theme theme;
    private LyricCanvas canvas;
    private RadioButton selectButton;
    private Button removeButton;
    private Button editButton;
    private EditThemeDialog themeDialog;

    /**
     * Create a new theme preview panel.
     * <p/>
     * @param theme the theme to preview.
     */
    public ThemePreviewPanel(Theme theme) {
        this.theme = theme;
        if(theme == null) {
            theme = Theme.DEFAULT_THEME;
        }
        canvas = new LyricCanvas(false, false);
        canvas.setTheme(theme);
        canvas.setPrefSize(200, 200);
        canvas.setText(ThemePanel.SAMPLE_LYRICS, new String[0], false);
        String name;
        if(theme == Theme.DEFAULT_THEME) {
            name = LabelGrabber.INSTANCE.getLabel("default.theme.text");
        }
        else {
            name = theme.getThemeName();
        }
        themeDialog = new EditThemeDialog();
        selectButton = new RadioButton(name);
        if(theme != Theme.DEFAULT_THEME) {
            editButton = new Button("", new ImageView(new Image("file:icons/edit32.png", 16, 16, false, true)));
            editButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("edit.theme.tooltip")));
            editButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
                @Override
                public void handle(javafx.event.ActionEvent t) {
                    themeDialog.setTheme(ThemePreviewPanel.this.theme);
                    themeDialog.show();
                    Theme ret = themeDialog.getTheme();
                    if(ret != null) {
                        try(PrintWriter pw = new PrintWriter(ret.getFile())) {
                            pw.println(ret.toDBString());
                        }
                        catch(IOException ex) {
                            LOGGER.log(Level.WARNING, "Couldn't edit theme", ex);
                        }
                    }
                }
            });

            removeButton = new Button("", new ImageView(new Image("file:icons/delete.png", 16, 16, false, true)));
            removeButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("remove.theme.tooltip")));
            removeButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
                @Override
                public void handle(javafx.event.ActionEvent t) {
                    Dialog.buildConfirmation(LabelGrabber.INSTANCE.getLabel("delete.theme.confirm.title"), LabelGrabber.INSTANCE.getLabel("delete.theme.question"), null).addYesButton(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent t) {
                            ThemePreviewPanel.this.theme.getFile().delete();
                        }
                    }).addNoButton(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent t) {
                            //Nothing needed here
                        }
                    }).build().showAndWait();
                }
            });
        }
        HBox buttonPanel = new HBox();
        if(canvas != null) {
            canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    t.consume();
                    selectButton.fire();
                }
            });
        }
        buttonPanel.getChildren().add(selectButton);
        if(theme != Theme.DEFAULT_THEME) {
            buttonPanel.getChildren().add(editButton);
            buttonPanel.getChildren().add(removeButton);
        }
        HBox canvasPanel = new HBox();
        canvasPanel.getChildren().add(canvas);
        getChildren().add(canvasPanel);
        getChildren().add(buttonPanel);
    }

    /**
     * Get the select radio button used to select this theme.
     * <p/>
     * @return the select radio button.
     */
    public RadioButton getSelectButton() {
        return selectButton;
    }

    /**
     * Get the theme in use on this preview panel.
     * <p/>
     * @return the theme in use on this preview panel.
     */
    public Theme getTheme() {
        return theme;
    }

    public static void main(String[] args) {
        new JFXPanel();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = new Stage();
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent t) {
                        System.exit(0);
                    }
                });
                stage.setScene(new Scene(new ScheduleThemeNode(null)));
                stage.show();
            }
        });
    }
}
