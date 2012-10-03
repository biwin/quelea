/*
 * This file is part of Quelea, free projection software for churches.
 * 
 * Copyright (C) 2012 Michael Berry
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
package org.quelea.windows.newsong;

import java.io.File;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.quelea.Theme;
import org.quelea.languages.LabelGrabber;
import org.quelea.utils.QueleaProperties;

/**
 * A modal dialog where a theme can be edited.
 *
 * @author Michael
 */
public class EditThemeDialog extends Stage {

    private ThemePanel panel;
    private Theme theme;
    private File themeFile;
    private Button confirmButton;
    private Button cancelButton;
    private TextField nameField;

    /**
     * Create a new edit theme dialog.
     */
    public EditThemeDialog() {
        initModality(Modality.WINDOW_MODAL);
        setTitle(LabelGrabber.INSTANCE.getLabel("edit.theme.heading"));
        setResizable(false);
        
        BorderPane mainPane = new BorderPane();
        HBox northPanel = new HBox();
        mainPane.setTop(northPanel);
        northPanel.getChildren().add(new Label(LabelGrabber.INSTANCE.getLabel("theme.name.label")));
        nameField = new TextField();
        nameField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                confirmButton.setDisable(nameField.getText().trim().isEmpty());
            }
        });
        northPanel.getChildren().add(nameField);
        panel = new ThemePanel();
        mainPane.setCenter(panel);
        confirmButton = new Button(LabelGrabber.INSTANCE.getLabel("ok.button"), new ImageView(new Image("file:icons/tick.png")));
        confirmButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent t) {
                theme = panel.getTheme();
                theme.setFile(themeFile);
                theme.setThemeName(nameField.getText());
                hide();
            }
        });
        cancelButton = new Button(LabelGrabber.INSTANCE.getLabel("cancel.button"), new ImageView(new Image("file:icons/cross.png")));
        cancelButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent t) {
                theme = null;
                hide();
            }
        });
        
        HBox southPanel = new HBox();
        southPanel.setAlignment(Pos.CENTER);
        southPanel.getChildren().add(confirmButton);
        southPanel.getChildren().add(cancelButton);
        mainPane.setBottom(southPanel);
        
        setScene(new Scene(mainPane));
    }

    /**
     * Get the theme from this dialog.
     *
     * @return the theme.
     */
    public Theme getTheme() {
        return theme;
    }

    /**
     * Set the theme on this dialog.
     *
     * @param theme the theme.
     */
    public void setTheme(Theme theme) {
        if(theme == null) {
            theme = new Theme(Theme.DEFAULT_FONT, Theme.DEFAULT_FONT_COLOR, Theme.DEFAULT_BACKGROUND);
            theme.setThemeName("");
            File file;
            int filenum = 1;
            do {
                file = new File(new File(QueleaProperties.getQueleaUserHome(), "themes"), "theme" + filenum + ".th");
                filenum++;
            } while(file.exists());
            theme.setFile(file);
        }
        themeFile = theme.getFile();
        nameField.setText(theme.getThemeName());
        confirmButton.setDisable(nameField.getText().isEmpty());
        panel.setTheme(theme);
    }
}
