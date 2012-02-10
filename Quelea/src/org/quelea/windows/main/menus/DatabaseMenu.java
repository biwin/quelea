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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.quelea.Application;
import org.quelea.languages.LabelGrabber;
import org.quelea.tags.TagDialog;
import org.quelea.utils.Utils;
import org.quelea.windows.library.LibrarySongList;
import org.quelea.windows.main.EditSongDBActionListener;
import org.quelea.windows.main.NewSongActionListener;
import org.quelea.windows.main.RemoveSongDBActionListener;

/**
 * Quelea's database menu
 * @author Michael
 */
public class DatabaseMenu extends JMenu {

    private final TagDialog tagDialog;
    private final JMenuItem newSongItem;
    private final JMenuItem editSongItem;
    private final JMenuItem deleteSongItem;
    private final JMenuItem tagsItem;
    
    private final ImportMenu importMenu;
    private final ExportMenu exportMenu;

    /**
     * Create the database menu.
     */
    public DatabaseMenu() {
        super(LabelGrabber.INSTANCE.getLabel("database.heading"));

        newSongItem = new JMenuItem(LabelGrabber.INSTANCE.getLabel("new.song.button"), Utils.getImageIcon("icons/newsong.png", 16, 16));
        newSongItem.addActionListener(new NewSongActionListener());
        add(newSongItem);

        editSongItem = new JMenuItem(LabelGrabber.INSTANCE.getLabel("edit.song.button"), Utils.getImageIcon("icons/edit.png", 16, 16));
        editSongItem.addActionListener(new EditSongDBActionListener());
        editSongItem.setEnabled(false);
        add(newSongItem);

        deleteSongItem = new JMenuItem(LabelGrabber.INSTANCE.getLabel("delete.song.button"), Utils.getImageIcon("icons/remove 2.png", 16, 16));
        deleteSongItem.addActionListener(new RemoveSongDBActionListener());
        deleteSongItem.setEnabled(false);
        add(newSongItem);

        tagDialog = new TagDialog();
        tagsItem = new JMenuItem(LabelGrabber.INSTANCE.getLabel("tags.button"), Utils.getImageIcon("icons/tag.png", 16, 16));
        tagsItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                tagDialog.reloadTags();
                tagDialog.setVisible(true);
            }
        });
        add(tagsItem);

        final LibrarySongList libraryList = Application.get().getMainWindow().getMainPanel().getLibraryPanel().getLibrarySongPanel().getSongList();

        libraryList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                checkEditDeleteItems(editSongItem, deleteSongItem);
            }
        });
        libraryList.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                checkEditDeleteItems(editSongItem, deleteSongItem);
            }

            @Override
            public void focusLost(FocusEvent e) {
                checkEditDeleteItems(editSongItem, deleteSongItem);
            }
        });
        
        addSeparator();
        importMenu = new ImportMenu();
        add(importMenu);
        exportMenu = new ExportMenu();
        add(exportMenu);
    }

    /**
     * Check whether the edit / delete buttons should be set to enabled or not.
     */
    private void checkEditDeleteItems(JMenuItem editSongButton, JMenuItem deleteSongButton) {
        final LibrarySongList libraryList = Application.get().getMainWindow().getMainPanel().getLibraryPanel().getLibrarySongPanel().getSongList();
        if(!libraryList.isFocusOwner()) {
            deleteSongButton.setEnabled(false);
            editSongButton.setEnabled(false);
            return;
        }
        if(libraryList.getSelectedIndex() == -1) {
            deleteSongButton.setEnabled(false);
            editSongButton.setEnabled(false);
        }
        else {
            deleteSongButton.setEnabled(true);
            editSongButton.setEnabled(true);
        }
    }
}
