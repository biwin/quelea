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
package org.quelea.windows.main;

import javax.swing.JComponent;
import org.quelea.Application;
import org.quelea.displayable.Song;
import org.quelea.windows.library.LibrarySongList;

/**
 * Responsible for checking, enabling, disabling etc. buttons.
 *
 * @author Michael
 */
public class ButtonChecker {

    public static final ButtonChecker INSTANCE = new ButtonChecker();

    /**
     * Only private.
     */
    private ButtonChecker() {
    }
    
    /**
     * Check whether the edit or remove buttons should be set to enabled or
     * disabled.
     *
     * @param editSongButton the edit button to check.
     * @param removeSongButton the remove button to check.
     */
    public void checkEditRemoveButtons(JComponent editSongButton, JComponent removeSongButton) {
        final MainPanel mainPanel = Application.get().getMainWindow().getMainPanel();
        final ScheduleList scheduleList = mainPanel.getSchedulePanel().getScheduleList();
        if(!scheduleList.isFocusOwner()) {
            editSongButton.setEnabled(false);
            removeSongButton.setEnabled(false);
            return;
        }
        if(scheduleList.getSelectedIndex() == -1) {
            editSongButton.setEnabled(false);
            removeSongButton.setEnabled(false);
        }
        else {
            if(scheduleList.getSelectedValue() instanceof Song) {
                editSongButton.setEnabled(true);
            }
            else {
                editSongButton.setEnabled(false);
            }
            removeSongButton.setEnabled(true);
        }
    }

    /**
     * Check whether the add to schedule button should be set enabled or
     * disabled.
     *
     * @param addSongButton the button to check.
     */
    public void checkAddButton(JComponent addSongButton) {
        final MainPanel mainPanel = Application.get().getMainWindow().getMainPanel();
        final LibrarySongList songList = mainPanel.getLibraryPanel().getLibrarySongPanel().getSongList();
        if(!songList.isFocusOwner()) {
            addSongButton.setEnabled(false);
            return;
        }
        if(songList.getSelectedIndex() == -1) {
            addSongButton.setEnabled(false);
        }
        else {
            addSongButton.setEnabled(true);
        }
    }
}
