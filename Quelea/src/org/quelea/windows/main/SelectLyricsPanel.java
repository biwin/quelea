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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.quelea.displayable.TextDisplayable;
import org.quelea.displayable.TextSection;

/**
 * The panel where the lyrics for different songs can be selected.
 * @author Michael
 */
public class SelectLyricsPanel extends ContainedPanel {

    private final SelectLyricsList lyricsList;
    private final LivePreviewPanel containerPanel;
    private final LyricCanvas previewCanvas;

    /**
     * Create a new lyrics panel.
     */
    public SelectLyricsPanel(LivePreviewPanel containerPanel) {
        this.containerPanel = containerPanel;
        setPreferredSize(new Dimension(300, 600));
        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        lyricsList = new SelectLyricsList();
        previewCanvas = new LyricCanvas(false);
        splitPane.add(new JScrollPane(lyricsList) {

            {
                setBorder(new EmptyBorder(0, 0, 0, 0));
                setPreferredSize(lyricsList.getPreferredSize());
            }
        });
        splitPane.setOneTouchExpandable(true);
        splitPane.add(previewCanvas);
        add(splitPane, BorderLayout.CENTER);
        containerPanel.registerLyricCanvas(previewCanvas);
        lyricsList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                updateCanvases();
            }
        });
        lyricsList.getModel().addListDataListener(new ListDataListener() {

            public void intervalAdded(ListDataEvent e) {
                updateCanvases();
            }

            public void intervalRemoved(ListDataEvent e) {
                updateCanvases();
            }

            public void contentsChanged(ListDataEvent e) {
                updateCanvases();
            }
        });
    }

    public void showDisplayable(TextDisplayable displayable, int index) {
        clear();
        for (TextSection section : displayable.getSections()) {
            lyricsList.getModel().addElement(section);
        }
        lyricsList.setSelectedIndex(index);
        lyricsList.ensureIndexIsVisible(index);
    }

    public int getIndex() {
        return lyricsList.getSelectedIndex();
    }

    @Override
    public void clear() {
        lyricsList.getModel().clear();
        updateCanvases();
    }

    @Override
    public void focus() {
        lyricsList.requestFocus();
    }

    @Override
    public void addKeyListener(KeyListener l) {
        super.addKeyListener(l);
        lyricsList.addKeyListener(l);
    }

    /**
     * Called to update the contents of the canvases when the list selection changes.
     */
    private void updateCanvases() {
        int selectedIndex = lyricsList.getSelectedIndex();
        for (LyricCanvas canvas : containerPanel.getCanvases()) {
            if (selectedIndex == -1 || selectedIndex >= lyricsList.getModel().getSize()) {
                canvas.setTheme(null);
                canvas.eraseText();
                continue;
            }
            TextSection currentSection = lyricsList.getModel().getElementAt(selectedIndex);
            if (currentSection.getTempTheme() != null) {
                canvas.setTheme(currentSection.getTempTheme());
            }
            else {
                canvas.setTheme(currentSection.getTheme());
            }
            canvas.setCapitaliseFirst(currentSection.shouldCapitaliseFirst());
            canvas.setText(currentSection.getText(false, false), currentSection.getSmallText());
        }
    }
}
