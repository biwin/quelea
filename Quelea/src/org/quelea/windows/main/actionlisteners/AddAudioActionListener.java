/* 
 * This file is part of Quelea, free projection software for churches.
 * Copyright (C) 2012 Ben Goodwin and Michael Berry
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.quelea.Application;
import org.quelea.displayable.AudioDisplayable;
import org.quelea.languages.LabelGrabber;
import org.quelea.sound.AudioTrack;
import org.quelea.utils.FileFilters;

/**
 * Selects a file and verifies it's a correct audio file, and if so, adds it into
 * the schedule as a displayable 
 *
 * @author Ben Goodwin
 * @version 19-May-2012
 */
public class AddAudioActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(FileFilters.AUDIO);
        fileChooser.setMultiSelectionEnabled(false);
        int val = fileChooser.showOpenDialog(Application.get().getMainWindow());
        if(val == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if(file != null) {
                AudioTrack track = new AudioTrack(fileChooser.getSelectedFile().getAbsolutePath());
                if(track.checkOK()) {
                    AudioDisplayable displayable = new AudioDisplayable(track);
                    Application.get().getMainWindow().getMainPanel().getSchedulePanel().getScheduleList().getModel().addElement(displayable);
                }
                else {
                    JOptionPane.showMessageDialog(Application.get().getMainWindow(), LabelGrabber.INSTANCE.getLabel("audio.error"), LabelGrabber.INSTANCE.getLabel("audio.error.title"), JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }
}
