package org.quelea.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the properties specific to Quelea.
 * @author Michael
 */
public final class QueleaProperties extends Properties {

    public static final String PROP_FILE_LOCATION = "quelea.properties";
    private static final Logger LOGGER = LoggerUtils.getLogger();
    private static final QueleaProperties INSTANCE = new QueleaProperties();

    /**
     * Load the properties from the properties file.
     */
    private QueleaProperties() {
        try {
            FileReader reader = new FileReader(PROP_FILE_LOCATION);
            try {
                load(reader);
            }
            finally {
                reader.close();
            }
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Couldn't load properties", ex);
        }
    }

    /**
     * Save these properties to the file.
     */
    private void write() {
        try {
            FileWriter writer = new FileWriter(PROP_FILE_LOCATION);
            try {
                store(writer, "Auto save");
            }
            finally {
                writer.close();
            }
        }
        catch(IOException ex) {
            LOGGER.log(Level.WARNING, "Couldn't store properties", ex);
        }
    }

    /**
     * Get the singleton instance of this class.
     * @return the instance.
     */
    public static QueleaProperties get() {
        return INSTANCE;
    }

    /**
     * Get the current version number.
     * @return the current version number.
     */
    public String getVersion() {
        return getProperty("quelea.version", "");
    }

    /**
     * Get the extension used for quelea schedules.
     * @return the extension used for quelea schedules.
     */
    public String getScheduleExtension() {
        return getProperty("quelea.schedule.extension", "qsch");
    }

    /**
     * Get the number of the screen used for the control screen. This is the
     * screen that the main Quelea operator window will be displayed on.
     * @return the control screen number.
     */
    public int getControlScreen() {
        return Integer.parseInt(getProperty("control.screen", "0"));
    }

    /**
     * Set the control screen output.
     * @param screen the number of the screen to use for the output.
     */
    public void setControlScreen(int screen) {
        setProperty("control.screen", Integer.toString(screen));
        write();
    }

    /**
     * Get the number of the projector screen. This is the screen that the
     * projected output will be displayed on.
     * @return the projector screen number.
     */
    public int getProjectorScreen() {
        return Integer.parseInt(getProperty("projector.screen", "1"));
    }
    
    /**
     * Set the control screen output.
     * @param screen the number of the screen to use for the output.
     */
    public void setProjectorScreen(int screen) {
        setProperty("projector.screen", Integer.toString(screen));
        write();
    }

    /**
     * Get the maximum number of characters allowed on any one line of
     * projected text. If the line is longer than this, it will be split up
     * intelligently.
     * @return the maximum number of characters allowed on any one line of
     * projected text.
     */
    public int getMaxChars() {
        return Integer.parseInt(getProperty("max.chars", "30"));
    }

    /**
     * Set the max chars value.
     * @param maxChars the maximum number of characters allowed on any one line
     * of projected text.
     */
    public void setMaxChars(int maxChars) {
        setProperty("max.chars", Integer.toString(maxChars));
        write();
    }

    /**
     * Get the minimum number of lines that should be displayed on each page.
     * This purely applies to font sizes, the font will be adjusted so this
     * amount of lines can fit on. This stops small lines becoming huge in the
     * preview window rather than displaying normally.
     * @return the minimum line count.
     */
    public int getMinLines() {
        return Integer.parseInt(getProperty("min.lines", "10"));
    }

    /**
     * Set the min lines value.
     * @param maxChars the minimum line count.
     */
    public void setMinLines(int minLines) {
        setProperty("min.lines", Integer.toString(minLines));
        write();
    }

    /**
     * Determine whether the single monitor warning should be shown (this warns
     * the user they only have one monitor installed.)
     * @return true if the warning should be shown, false otherwise.
     */
    public boolean showSingleMonitorWarning() {
        return Boolean.parseBoolean(getProperty("single.monitor.warning", "true"));
    }

    /**
     * Set whether the single monitor warning should be shown.
     * @param val true if the warning should be shown, false otherwise.
     */
    public void setSingleMonitorWarning(boolean val) {
        setProperty("single.monitor.warning", Boolean.toString(val));
        write();
    }

}
