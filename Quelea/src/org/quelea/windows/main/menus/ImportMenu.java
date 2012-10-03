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
package org.quelea.windows.main.menus;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.quelea.importexport.*;
import org.quelea.languages.LabelGrabber;

/**
 * Quelea's import menu.
 * @author Michael
 */
public class ImportMenu extends Menu {
    
    private final ImportDialog sImportDialog;
    private final ImportDialog qspImportDialog;
    private final ImportDialog sourceImportDialog;
    private final ImportDialog kingswayImportDialog;
    
    private final MenuItem qspItem;
    private final MenuItem ssItem;
    private final MenuItem sourceItem;
    private final Menu kingswayItem;
    
    /**
     * Create the import menu.
     */
    public ImportMenu() {
        super(LabelGrabber.INSTANCE.getLabel("import.heading"), new ImageView(new Image("file:icons/left.png", 16, 16, false,true)));
        
        qspImportDialog = new QSPImportDialog(null);
        sImportDialog = new SurvivorImportDialog(null);
        sourceImportDialog = new SourceImportDialog(null);
        kingswayImportDialog = new KingswayImportDialog(null, null);
        
        qspItem = new MenuItem(LabelGrabber.INSTANCE.getLabel("qsp.button"), new ImageView(new Image("file:icons/logo.png", 16, 16, false, true)));
        qspItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                qspImportDialog.setLocationRelativeTo(qspImportDialog.getOwner());
                qspImportDialog.setVisible(true);
            }
        });
        getItems().add(qspItem);
        
        ssItem = new MenuItem(LabelGrabber.INSTANCE.getLabel("ss.button"), new ImageView(new Image("file:icons/survivor.jpg", 16, 16, false, true)));
        ssItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                sImportDialog.setLocationRelativeTo(sImportDialog.getOwner());
                sImportDialog.setVisible(true);
            }
        });
        getItems().add(ssItem);
        
        sourceItem = new MenuItem(LabelGrabber.INSTANCE.getLabel("source.button"), new ImageView(new Image("file:icons/source.jpg", 16, 16, false, true)));
        sourceItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                sourceImportDialog.setLocationRelativeTo(sourceImportDialog.getOwner());
                sourceImportDialog.setVisible(true);
            }
        });
        getItems().add(sourceItem);
        
        MenuItem kingswayAll, kingswayOne; 
        
        kingswayItem = new Menu(LabelGrabber.INSTANCE.getLabel("kingsway.button"),new ImageView(new Image("file:icons/kingsway.png", 16, 16, false, true)));
        kingswayAll = new MenuItem(LabelGrabber.INSTANCE.getLabel("kingsway.button.all"));
        kingswayAll.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                kingswayImportDialog.setAll(true);
                kingswayImportDialog.setLocationRelativeTo(kingswayImportDialog.getOwner());
                kingswayImportDialog.setVisible(true);
            }
        });
        
        kingswayOne = new MenuItem(LabelGrabber.INSTANCE.getLabel("kingsway.button.one"),new ImageView(new Image("file:icons/kingsway.png", 16, 16, false, true)));
        kingswayOne.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                kingswayImportDialog.setAll(false);
                kingswayImportDialog.setLocationRelativeTo(kingswayImportDialog.getOwner());
                kingswayImportDialog.setVisible(true);
            }
        });
        
        getItems().add(kingswayItem);
        kingswayItem.getItems().add(kingswayAll);
        kingswayItem.getItems().add(kingswayOne);
        
    }
    
}
