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
package org.quelea.windows.main.actionlisteners;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javax.swing.JOptionPane;
import name.antonsmirnov.javafx.dialog.Dialog;
import org.quelea.QueleaApp;
import org.quelea.SongDatabase;
import org.quelea.displayable.Song;
import org.quelea.languages.LabelGrabber;
import org.quelea.windows.library.LibrarySongList;
import org.quelea.windows.main.MainWindow;

/**
 * Action listener that removes the selected song from the database.
 * <p/>
 * @author Michael
 */
public class RemoveSongDBActionListener implements EventHandler<ActionEvent> {

    private boolean yes = false;

    /**
     * Remove the selected song from the database.
     * <p/>
     * @param e the action event.
     */
    @Override
    public void handle(ActionEvent t) {
        MainWindow mainWindow = QueleaApp.get().getMainWindow();
        LibrarySongList songList = mainWindow.getMainPanel().getLibraryPanel().getLibrarySongPanel().getSongList();
        Song song = songList.itemsProperty().get().get(songList.getSelectionModel().getSelectedIndex());
        if(song == null) {
            return;
        }
        yes = false;
        Dialog.buildConfirmation(LabelGrabber.INSTANCE.getLabel("confirm.remove.text"),
                LabelGrabber.INSTANCE.getLabel("confirm.remove.question").replace("$1", song.getTitle()))
                .addYesButton(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                yes = true;
            }
        }).addNoButton(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            }
        }).build().showAndWait();
        if(yes) {
            if(!SongDatabase.get().removeSong(song)) {
                Dialog.showError(LabelGrabber.INSTANCE.getLabel("error.text"), LabelGrabber.INSTANCE.getLabel("error.removing.song.db"));
            }
            song.setID(-1);
            songList.itemsProperty().get().remove(song);
        }
    }
}
