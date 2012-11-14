/*
 * This file is part of Quelea, free projection software for churches.
 * 
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by * the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.quelea.languages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quelea.services.utils.LoggerUtils;
import org.quelea.services.utils.QueleaProperties;

/**
 * Responsible for grabbing the appropriate labels from the current langauges
 * file based on keys.
 *
 * @author Michael
 */
public class LabelGrabber {

    private static final Logger LOGGER = LoggerUtils.getLogger();
    public static final LabelGrabber INSTANCE = new LabelGrabber();
    
    private Properties labels;

    /**
     * Create the label grabber.
     */
    private LabelGrabber() {
        labels = new Properties();
        File langFile = QueleaProperties.get().getLanguageFile();
        if(langFile==null) {
            LOGGER.log(Level.SEVERE, "Couldn't load languages file, file was null");
            return;
        }
        LOGGER.log(Level.INFO, "Using languages file {0}", langFile.getAbsolutePath());
        try (InputStream stream = new FileInputStream(langFile)) {
            labels.load(stream);
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Couldn't load languages file", ex);
        }
    }
    
    /**
     * Get a label from the language file.
     * @param key the key to use to get the label.
     * @return the textual string in the appropriate language.
     */
    public String getLabel(String key) {
        String ret = labels.getProperty(key);
        if(ret==null) {
            LOGGER.log(Level.WARNING, "Missing label in language file: {0}", key);
            return key;
        }
        return ret;
    }
}