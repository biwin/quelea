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
package org.quelea.windows.main.ribbon;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

/**
 *
 * @author Michael
 */
public class ProjectorTask extends RibbonTask {
    
    public ProjectorTask() {
        super("Projector", getControlBand());
    }

    private static JRibbonBand getControlBand() {
        JRibbonBand controlBand = new JRibbonBand("Controls", RibbonUtils.getRibbonIcon("icons/projector.png", 100, 100));
        RibbonUtils.applyStandardResizePolicies(controlBand);
        JCommandButton onButton = new JCommandButton("On", RibbonUtils.getRibbonIcon("icons/poweron.png", 100, 100));
        controlBand.addCommandButton(onButton, RibbonElementPriority.TOP);
        onButton.setEnabled(false);
        JCommandButton offButton = new JCommandButton("Off", RibbonUtils.getRibbonIcon("icons/exit.png", 100, 100));
        controlBand.addCommandButton(offButton, RibbonElementPriority.TOP);
        offButton.setEnabled(false);
        JCommandButton inputButton = new JCommandButton("Switch Input", RibbonUtils.getRibbonIcon("icons/projectorinput.png", 100, 100));
        controlBand.addCommandButton(inputButton, RibbonElementPriority.TOP);
        inputButton.setEnabled(false);
        return controlBand;
    }
}
