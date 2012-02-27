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
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
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
 *
 * @author Michael
 */
public class SelectLyricsPanel extends ContainedPanel {

    private final SelectLyricsList lyricsList;
    private final LivePreviewPanel containerPanel;
    private final LyricCanvas previewCanvas;
    private boolean stopUpdate;

    /**
     * Create a new lyrics panel.
     *
     * @param containerPanel the container panel this panel is contained within.
     */
    public SelectLyricsPanel(LivePreviewPanel containerPanel) {
        this.containerPanel = containerPanel;
        setPreferredSize(new Dimension(300, 600));
        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        lyricsList = new SelectLyricsList();
        previewCanvas = new LyricCanvas(false, false);
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

            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateCanvases();
            }
        });
        lyricsList.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                check(ke);
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                check(ke);
            }
        });
        lyricsList.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent me) {
                check(me);
            }

            @Override
            public void mousePressed(MouseEvent me) {
                check(me);
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                check(me);
            }

            @Override
            public void mouseEntered(MouseEvent me) {
                check(me);
            }

            @Override
            public void mouseExited(MouseEvent me) {
                check(me);
            }
        });
        lyricsList.getModel().addListDataListener(new ListDataListener() {

            @Override
            public void intervalAdded(ListDataEvent e) {
                updateCanvases();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                updateCanvases();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                updateCanvases();
            }
        });
    }
    
    /**
     * Check based on the current input event whether to set stopUpdate.
     * @param ie the input event.
     */
    private void check(InputEvent ie) {
        if(ie.isAltDown()) {
            setStopUpdate(true);
        }
        else {
            setStopUpdate(false);
        }
    }

    /**
     * Set stop update. If set to true then selection events on this list won't
     * update any of the attached canvases.
     *
     * @param stopUpdate true if no updates should occur on the canvases, false
     * otherwise.
     */
    private void setStopUpdate(boolean stopUpdate) {
        this.stopUpdate = stopUpdate;
    }

    /**
     * Set one line mode on or off.
     *
     * @param on if one line mode should be turned on, false otherwise.
     */
    public void setOneLineMode(boolean on) {
        lyricsList.setOneLineMode(on);
    }

    /**
     * Show a given text displayable on this panel.
     *
     * @param displayable the displayable to show.
     * @param index the index of the displayable to show.
     */
    public void showDisplayable(TextDisplayable displayable, int index) {
        clear();
        for(TextSection section : displayable.getSections()) {
            lyricsList.getModel().addElement(section);
        }
        lyricsList.setSelectedIndex(index);
        lyricsList.ensureIndexIsVisible(index);
    }

    /**
     * Get the current displayed index.
     *
     * @return the current displayed index.
     */
    public int getIndex() {
        return lyricsList.getSelectedIndex();
    }

    /**
     * Get the lyrics list on this panel.
     *
     * @return the select lyrics list.
     */
    public SelectLyricsList getLyricsList() {
        return lyricsList;
    }

    /**
     * Clear the current panel.
     */
    @Override
    public void clear() {
        lyricsList.getModel().clear();
        updateCanvases();
    }

    /**
     * Focus on this panel.
     */
    @Override
    public void focus() {
        lyricsList.requestFocus();
    }

    /**
     * Add a key listener to the list on this panel (and this panel.)
     *
     * @param l the key listener to add.
     */
    @Override
    public void addKeyListener(KeyListener l) {
        super.addKeyListener(l);
        lyricsList.addKeyListener(l);
    }

    /**
     * Called to update the contents of the canvases when the list selection
     * changes.
     */
    private void updateCanvases() {
        if(stopUpdate) {
            return;
        }
        int selectedIndex = lyricsList.getSelectedIndex();
        HashSet<LyricCanvas> canvases = new HashSet<>();
        canvases.addAll(containerPanel.getCanvases());
        for(LyricCanvas canvas : canvases) {
            if(selectedIndex == -1 || selectedIndex >= lyricsList.getModel().getSize()) {
                canvas.setTheme(null);
                canvas.eraseText();
                continue;
            }
            TextSection currentSection = lyricsList.getModel().getElementAt(selectedIndex);
            if(currentSection.getTempTheme() != null) {
                canvas.setTheme(currentSection.getTempTheme());
            }
            else {
                canvas.setTheme(currentSection.getTheme());
            }
            canvas.setCapitaliseFirst(currentSection.shouldCapitaliseFirst());
            if(canvas.isStageView()) {
                canvas.setText(currentSection.getText(true, false), currentSection.getSmallText());
            }
            else {
                canvas.setText(currentSection.getText(false, false), currentSection.getSmallText());
            }
        }
    }
}
