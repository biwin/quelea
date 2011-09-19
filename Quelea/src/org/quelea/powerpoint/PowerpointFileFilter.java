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
package org.quelea.powerpoint;

import org.quelea.utils.ExtensionFileFilter;

/**
 * File filter implementation for video files recognised by libvlc.
 */
public class PowerpointFileFilter extends ExtensionFileFilter {

    /**
     * From the vlc_interfaces.h include file.
     */
    private static final String[] EXTENSIONS_POWERPOINT = {
        "ppt",
        "pptx",
        "pps",
        "ppsx"
    };
    /**
     * Single instance.
     */
    public static final PowerpointFileFilter INSTANCE = new PowerpointFileFilter();

    /**
     * Create a new file filter.
     */
    public PowerpointFileFilter() {
        super(EXTENSIONS_POWERPOINT);
    }

    /**
     * Get a description of this file filter.
     * @return "Powerpoint files".
     */
    @Override
    public String getDescription() {
        return "Powerpoint files";
    }
}
