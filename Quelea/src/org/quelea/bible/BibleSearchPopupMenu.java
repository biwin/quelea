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
package org.quelea.bible;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.quelea.Application;
import org.quelea.languages.LabelGrabber;

/**
 * The popup menu that appears on a "searched" bible chapter.
 *
 * @author Michael
 */
public class BibleSearchPopupMenu extends JPopupMenu {

    private JMenuItem viewVerseItem;
    private BibleChapter currentChapter;

    /**
     * Create the bible search popup menu.
     */
    public BibleSearchPopupMenu() {
        viewVerseItem = new JMenuItem(LabelGrabber.INSTANCE.getLabel("open.in.browser"));
        viewVerseItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                trigger();
            }
        });
        add(viewVerseItem);
    }

    public void trigger() {
        if (currentChapter != null) {
            BibleBrowseDialog dialog = Application.get().getMainWindow().getBibleBrowseDialog();
            dialog.setChapter(currentChapter);
            dialog.setVisible(true);
        }
    }

    /**
     * Set the current chapter the menu should jump to.
     *
     * @param currentChapter the current chapter.
     */
    public void setCurrentChapter(BibleChapter currentChapter) {
        this.currentChapter = currentChapter;
    }
}