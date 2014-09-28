/* 
 * This file is part of Quelea, free projection software for churches.
 * 
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
package org.quelea.services.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import org.quelea.data.bible.Bible;
import org.quelea.data.displayable.TextAlignment;
import org.quelea.services.languages.spelling.Dictionary;
import org.quelea.services.languages.spelling.DictionaryManager;

/**
 * Manages the properties specific to Quelea.
 * <p/>
 * @author Michael
 */
public final class QueleaProperties extends Properties {

    public static final Version VERSION = new Version("2014.1", "beta");
    private static final QueleaProperties INSTANCE = new QueleaProperties();
//    private static final Logger LOGGER = LoggerUtils.getLogger();

    /**
     * Load the properties from the properties file.
     */
    private QueleaProperties() {
        try {
            if (!getPropFile().exists()) {
                getPropFile().createNewFile();
            }
            try (StringReader reader = new StringReader(Utils.getTextFromFile(getPropFile().getAbsolutePath(), ""))) {
                load(reader);
            }
        } catch (IOException ex) {
//            LOGGER.log(Level.SEVERE, "Couldn't load properties", ex);
//            ex.printStackTrace();
        }
    }

    /**
     * Get the properties file.
     * <p/>
     * @return the properties file.
     */
    private File getPropFile() {
        return new File(getQueleaUserHome(), "quelea.properties");
    }

    /**
     * Save these properties to the file.
     */
    private void write() {
        try (FileWriter writer = new FileWriter(getPropFile())) {
            store(writer, "Auto save");
        } catch (IOException ex) {
//            LOGGER.log(Level.WARNING, "Couldn't store properties", ex);
        }
    }

    /**
     * Get the singleton instance of this class.
     * <p/>
     * @return the instance.
     */
    public static QueleaProperties get() {
        return INSTANCE;
    }

    /**
     * Get the languages file that should be used as specified in the properties
     * file.
     * <p/>
     * @return the languages file for the GUI.
     */
    public File getLanguageFile() {
        return new File("languages", getProperty("language.file", "gb.lang"));
    }

    /**
     * Get the languages file that should be used as specified in the properties
     * file.
     * <p/>
     * @return the languages file for the GUI.
     */
    public Dictionary getDictionary() {
        String dict = getProperty("language.file", "gb.lang");
        String[] parts = dict.split("\\.");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            builder.append(parts[i]);
            builder.append(".");
        }
        builder.append("words");
        return DictionaryManager.INSTANCE.getFromFilename(builder.toString());
    }

    /**
     * Set the name of the language file to use.
     * <p/>
     * @param file the name of the language file to use.
     */
    public void setLanguageFile(String file) {
        setProperty("language.file", file);
        write();
    }

    /**
     * Get the english languages file that should be present on all
     * installations. We can default to this if labels are missing in other
     * languages.
     * <p/>
     * @return the english languages file for the GUI.
     */
    public File getEnglishLanguageFile() {
        return new File("languages", "gb.lang");
    }

    /**
     * Get the scene info as stored from the last exit of Quelea (or some
     * default values if it doesn't exist in the properties file.)
     * <p/>
     * @return the scene info.
     */
    public SceneInfo getSceneInfo() {
        try {
            String[] parts = getProperty("scene.info", "461,15,997,995").split(",");
            return new SceneInfo(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        } catch (Exception ex) {
            LoggerUtils.getLogger().log(Level.WARNING, "Invalid scene info: " + getProperty("scene.info"), ex);
            return null;
        }
    }

    /**
     * Set the scene info for Quelea's main window - generally called just
     * before exit so the next invocation of the program can display the window
     * in the same position.
     * <p/>
     * @param info the scene info.
     */
    public void setSceneInfo(SceneInfo info) {
        setProperty("scene.info", info.toString());
        write();
    }

    /**
     * Get a list of user chosen fonts to appear in the theme dialog.
     * <p>
     * @return a list of user chosen fonts to appear in the theme dialog.
     */
    public List<String> getChosenFonts() {
        String fontStr = getProperty("chosen.fonts", "Arial|Liberation Sans|Noto Sans|Oxygen|Roboto|Vegur");
        List<String> ret = new ArrayList<>();
        for (String str : fontStr.split("\\|")) {
            if (!str.trim().isEmpty()) {
                ret.add(str);
            }
        }
        return ret;
    }

    /**
     * Set a list of user chosen fonts to appear in the theme dialog.
     * <p>
     * @param fonts the list of user chosen fonts to appear in the theme dialog.
     */
    public void setChosenFonts(List<String> fonts) {
        StringBuilder fontBuilder = new StringBuilder();
        for (int i = 0; i < fonts.size(); i++) {
            fontBuilder.append(fonts.get(i));
            if (i < fonts.size() - 1) {
                fontBuilder.append("|");
            }
        }
        setProperty("chosen.fonts", fontBuilder.toString());
        write();
    }

    /**
     * Determine if the same font size should be used for each section in a
     * displayable - this can stop the sizes jumping all over the place
     * depending on how much text there is per slide.
     * <p>
     * @return true if the uniform font size should be used, false otherwise.
     */
    public boolean getUseUniformFontSize() {
        return Boolean.parseBoolean(getProperty("uniform.font.size", "true"));
    }

    /**
     * Set if the same font size should be used for each section in a
     * displayable - this can stop the sizes jumping all over the place
     * depending on how much text there is per slide.
     * <p>
     * @param val true if the uniform font size should be used, false otherwise.
     */
    public void setUseUniformFontSize(boolean val) {
        setProperty("uniform.font.size", Boolean.toString(val));
    }

    /**
     * Determine if we should show verse numbers for bible passages.
     * <p>
     * @return true if we should show verse numbers, false otherwise.
     */
    public boolean getShowVerseNumbers() {
        return Boolean.parseBoolean(getProperty("show.verse.numbers", "true"));
    }

    /**
     * Set if we should show verse numbers for bible passages.
     * <p>
     * @param val true if we should show verse numbers, false otherwise.
     */
    public void setShowVerseNumbers(boolean val) {
        setProperty("show.verse.numbers", Boolean.toString(val));
    }

    /**
     * Get the colour to use for notice backgrounds.
     *
     * @return the colour to use for notice backgrounds.
     */
    public Color getNoticeBackgroundColour() {
        return getColor(getProperty("notice.background.colour", getStr(Color.BROWN)));
    }

    /**
     * Set the colour to use for notice backgrounds.
     *
     * @param colour the colour to use for notice backgrounds.
     */
    public void setNoticeBackgroundColour(Color colour) {
        setProperty("notice.background.colour", getStr(colour));
    }

    /**
     * Get the position at which to display the notices.
     *
     * @return the position at which to display the notices.
     */
    public String getNoticePosition() {
        return getProperty("notice.position", "Bottom");
    }

    /**
     * Set the position at which to display the notices.
     *
     * @param position the position at which to display the notices.
     */
    public void setNoticePosition(String position) {
        setProperty("notice.position", position);
    }

    /**
     * Get the speed at which to display the notices.
     *
     * @return the speed at which to display the notices.
     */
    public double getNoticeSpeed() {
        return Double.parseDouble(getProperty("notice.speed", "10"));
    }

    /**
     * Set the speed at which to display the notices.
     *
     * @param speed the speed at which to display the notices.
     */
    public void setNoticeSpeed(double speed) {
        setProperty("notice.speed", Double.toString(speed));
    }

    /**
     * Get the font size at which to display the notices.
     *
     * @return the font size at which to display the notices.
     */
    public double getNoticeFontSize() {
        return Double.parseDouble(getProperty("notice.font.size", "50"));
    }

    /**
     * Set the font size at which to display the notices.
     *
     * @param fontSize the font size at which to display the notices.
     */
    public void setNoticeFontSize(double fontSize) {
        setProperty("notice.font.size", Double.toString(fontSize));
    }

    /**
     * Determine if we should attempt to fetch translations automatically.
     * <p>
     * @return true if we should translate automatically, false otherwise.
     */
    public boolean getAutoTranslate() {
        return Boolean.parseBoolean(getProperty("auto.translate", "true"));
    }

    /**
     * Set if we should attempt to fetch translations automatically.
     * <p>
     * @param val true if we should translate automatically, false otherwise.
     */
    public void setAutoTranslate(boolean val) {
        setProperty("auto.translate", Boolean.toString(val));
    }

    /**
     * Get the maximum font size used by text displayables.
     * <p>
     * @return the maximum font size used by text displayables.
     */
    public double getMaxFontSize() {
        return Double.parseDouble(getProperty("max.font.size", "1000"));
    }

    /**
     * Set the maximum font size used by text displayables.
     * <p>
     * @param fontSize the maximum font size used by text displayables.
     */
    public void setMaxFontSize(double fontSize) {
        setProperty("max.font.size", Double.toString(fontSize));
    }

    /**
     * Get the additional line spacing (in pixels) to be used between each line.
     * <p>
     * @return the additional line spacing.
     */
    public double getAdditionalLineSpacing() {
        return Double.parseDouble(getProperty("additional.line.spacing", "10"));
    }

    /**
     * Set the additional line spacing (in pixels) to be used between each line.
     * <p>
     * @param spacing the additional line spacing.
     */
    public void setAdditionalLineSpacing(double spacing) {
        setProperty("additional.line.spacing", Double.toString(spacing));
    }

    /**
     * Determine if, when an item is removed from the schedule and displayed on
     * the live view, whether it should be removed from the live view or kept
     * until something replaces it.
     * <p/>
     * @return true if it should be cleared, false otherwise.
     */
    public boolean getClearLiveOnRemove() {
        return Boolean.parseBoolean(getProperty("clear.live.on.remove", "true"));
    }

    /**
     * Set if, when an item is removed from the schedule and displayed on the
     * live view, whether it should be removed from the live view or kept until
     * something replaces it.
     * <p/>
     * @param val true if it should be cleared, false otherwise.
     */
    public void setClearLiveOnRemove(boolean val) {
        setProperty("clear.live.on.remove", Boolean.toString(val));
    }

    /**
     * Get the location of Quelea's Facebook page.
     * <p/>
     * @return the location of the facebook page.
     */
    public String getFacebookPageLocation() {
        return getProperty("facebook.page", "http://www.facebook.com/quelea.projection");
    }

    /**
     * Get the Quelea home directory in the user's directory.
     * <p/>
     * @return the Quelea home directory.
     */
    public static File getQueleaUserHome() {
        File ret = new File(new File(System.getProperty("user.home")), ".quelea");
        if (!ret.exists()) {
            ret.mkdir();
        }
        return ret;
    }

    /**
     * Get the user's turbo db exe converter file.
     * <p/>
     * @return the user's turbo db exe converter.
     */
    public static File getTurboDBExe() {
        return new File(QueleaProperties.getQueleaUserHome(), "TdbDataX.exe");
    }

    /**
     * Get the number of the next kingsway song that should be imported.
     * <p/>
     * @return the number of the next song.
     */
    public int getNextKingswaySong() {
        return Integer.parseInt(getProperty("next.kingsway.song", "1"));
    }

    /**
     * Set the number of the next kingsway song that should be imported.
     * <p/>
     * @param num the number of the next song.
     */
    public void setNextKingswaySong(int num) {
        setProperty("next.kingsway.song", Integer.toString(num));
        write();
    }

    /**
     * Get the font to use for stage text.
     * <p/>
     * @return the font to use for stage text.
     */
    public String getStageTextFont() {
        return getProperty("stage.font", "SansSerif");
    }

    /**
     * Set the font to use for stage text.
     * <p/>
     * @param font the font to use for stage text.
     */
    public void setStageTextFont(String font) {
        setProperty("stage.font", font);
        write();
    }

    /**
     * Get the alignment of the text on stage view.
     * <p/>
     * @return the alignment of the text on stage view.
     */
    public String getStageTextAlignment() {
        return TextAlignment.valueOf(getProperty("stage.text.alignment", "LEFT")).toFriendlyString();
    }

    /**
     * Set the alignment of the text on stage view.
     * <p/>
     * @param alignment the alignment of the text on stage view.
     */
    public void setStageTextAlignment(TextAlignment alignment) {
        setProperty("stage.text.alignment", alignment.toString());
        write();
    }

    /**
     * Get whether we should display the chords in stage view.
     * <p/>
     * @return true if they should be displayed, false otherwise.
     */
    public boolean getShowChords() {
        return Boolean.parseBoolean(getProperty("stage.show.chords", "true"));
    }

    /**
     * Set whether we should display the chords in stage view.
     * <p/>
     * @param showChords true if they should be displayed, false otherwise.
     */
    public void setShowChords(boolean showChords) {
        setProperty("stage.show.chords", Boolean.toString(showChords));
        write();
    }

    /**
     * Determine whether we should phone home at startup with anonymous
     * information. Simply put phonehome=false in the properties file to disable
     * phonehome.
     * <p/>
     * @return true if we should phone home, false otherwise.
     */
    public boolean getPhoneHome() {
        return Boolean.parseBoolean(getProperty("phonehome", "true"));
    }

    /**
     * Get the directory used for storing the bibles.
     * <p/>
     * @return the bibles directory.
     */
    public File getBibleDir() {
        return new File(getQueleaUserHome(), "bibles");
    }

    /**
     * Get the directory used for storing images.
     * <p/>
     * @return the img directory
     */
    public File getImageDir() {
        return new File(getQueleaUserHome(), "img");
    }

    /**
     * Get the directory used for storing dictionaries.
     * <p/>
     * @return the dictionaries directory
     */
    public File getDictionaryDir() {
        return new File(getQueleaUserHome(), "dictionaries");
    }

    /**
     * Get the directory used for storing videos.
     * <p/>
     * @return the vid directory
     */
    public File getVidDir() {
        return new File(getQueleaUserHome(), "vid");
    }

    /**
     * Get the extension used for quelea schedules.
     * <p/>
     * @return the extension used for quelea schedules.
     */
    public String getScheduleExtension() {
        return getProperty("quelea.schedule.extension", "qsch");
    }

    /**
     * Get the extension used for quelea song packs.
     * <p/>
     * @return the extension used for quelea song packs.
     */
    public String getSongPackExtension() {
        return getProperty("quelea.songpack.extension", "qsp");
    }

    /**
     * Get the number of the screen used for the control screen. This is the
     * screen that the main Quelea operator window will be displayed on.
     * <p/>
     * @return the control screen number.
     */
    public int getControlScreen() {
        return Integer.parseInt(getProperty("control.screen", "0"));
    }

    /**
     * Set the control screen output.
     * <p/>
     * @param screen the number of the screen to use for the output.
     */
    public void setControlScreen(int screen) {
        setProperty("control.screen", Integer.toString(screen));
        write();
    }

    /**
     * Get the one line mode.
     * <p/>
     * @return true if one line mode should be enabled, false otherwise.
     */
    public boolean getOneLineMode() {
        return Boolean.parseBoolean(getProperty("one.line.mode", "false"));
    }

    /**
     * Set the one line mode property.
     * <p/>
     * @param val the value of the one linde mode.
     */
    public void setOneLineMode(boolean val) {
        setProperty("one.line.mode", Boolean.toString(val));
        write();
    }

    /**
     * Get the text shadow property.
     * <p/>
     * @return true if text shadows are enabled, false otherwise.
     */
    public boolean getTextShadow() {
        return Boolean.parseBoolean(getProperty("text.shadow", "false"));
    }

    /**
     * Set the text shadow property.
     * <p/>
     * @param val true if text shadows are enabled, false otherwise.
     */
    public void setTextShadow(boolean val) {
        setProperty("text.shadow", Boolean.toString(val));
        write();
    }

    /**
     * Get the number of the projector screen. This is the screen that the
     * projected output will be displayed on.
     * <p/>
     * @return the projector screen number.
     */
    public int getProjectorScreen() {
        return Integer.parseInt(getProperty("projector.screen", "1"));
    }

    /**
     * Set the control screen output.
     * <p/>
     * @param screen the number of the screen to use for the output.
     */
    public void setProjectorScreen(int screen) {
        setProperty("projector.screen", Integer.toString(screen));
        write();
    }

    /**
     * Get the maximum number of characters allowed on any one line of projected
     * text. If the line is longer than this, it will be split up intelligently.
     * <p/>
     * @return the maximum number of characters allowed on any one line of
     * projected text.
     */
    public int getMaxChars() {
        return Integer.parseInt(getProperty("max.chars", "30"));
    }

    /**
     * Set the max chars value.
     * <p/>
     * @param maxChars the maximum number of characters allowed on any one line
     * of projected text.
     */
    public void setMaxChars(int maxChars) {
        setProperty("max.chars", Integer.toString(maxChars));
        write();
    }

    /**
     * Get the custom projector co-ordinates.
     * <p/>
     * @return the co-ordinates.
     */
    public Bounds getProjectorCoords() {
        String[] prop = getProperty("projector.coords", "0,0,0,0").trim().split(",");
        return new BoundingBox(Integer.parseInt(prop[0]),
                Integer.parseInt(prop[1]),
                Integer.parseInt(prop[2]),
                Integer.parseInt(prop[3]));
    }

    /**
     * Set the custom projector co-ordinates.
     * <p/>
     * @param coords the co-ordinates to set.
     */
    public void setProjectorCoords(Bounds coords) {
        String rectStr = Integer.toString((int) coords.getMinX())
                + "," + Integer.toString((int) coords.getMinY())
                + "," + Integer.toString((int) coords.getWidth())
                + "," + Integer.toString((int) coords.getHeight());

        setProperty("projector.coords", rectStr);
        write();
    }

    /**
     * Determine if the projector mode is set to manual co-ordinates or a screen
     * number.
     * <p/>
     * @return true if it's set to manual co-ordinates, false if it's a screen
     * number.
     */
    public boolean isProjectorModeCoords() {
        return "coords".equals(getProperty("projector.mode"));
    }

    /**
     * Set the projector mode to be manual co-ordinates.
     */
    public void setProjectorModeCoords() {
        setProperty("projector.mode", "coords");
        write();
    }

    /**
     * Set the projector mode to be a screen number.
     */
    public void setProjectorModeScreen() {
        setProperty("projector.mode", "screen");
        write();
    }

    /**
     * Get the number of the stage screen. This is the screen that the projected
     * output will be displayed on.
     * <p/>
     * @return the stage screen number.
     */
    public int getStageScreen() {
        return Integer.parseInt(getProperty("stage.screen", "-1"));
    }

    /**
     * Set the stage screen output.
     * <p/>
     * @param screen the number of the screen to use for the output.
     */
    public void setStageScreen(int screen) {
        setProperty("stage.screen", Integer.toString(screen));
        write();
    }

    /**
     * Get the custom stage screen co-ordinates.
     * <p/>
     * @return the co-ordinates.
     */
    public Bounds getStageCoords() {
        String[] prop = getProperty("stage.coords", "0,0,0,0").trim().split(",");
        return new BoundingBox(Integer.parseInt(prop[0]),
                Integer.parseInt(prop[1]),
                Integer.parseInt(prop[2]),
                Integer.parseInt(prop[3]));
    }

    /**
     * Set the custom stage screen co-ordinates.
     * <p/>
     * @param coords the co-ordinates to set.
     */
    public void setStageCoords(Bounds coords) {
        String rectStr = Integer.toString((int) coords.getMinX())
                + "," + Integer.toString((int) coords.getMinY())
                + "," + Integer.toString((int) coords.getWidth())
                + "," + Integer.toString((int) coords.getHeight());

        setProperty("stage.coords", rectStr);
        write();
    }

    /**
     * Determine if the stage mode is set to manual co-ordinates or a screen
     * number.
     * <p/>
     * @return true if it's set to manual co-ordinates, false if it's a screen
     * number.
     */
    public boolean isStageModeCoords() {
        return "coords".equals(getProperty("stage.mode"));
    }

    /**
     * Set the stage mode to be manual co-ordinates.
     */
    public void setStageModeCoords() {
        setProperty("stage.mode", "coords");
        write();
    }

    /**
     * Set the stage mode to be a screen number.
     */
    public void setStageModeScreen() {
        setProperty("stage.mode", "screen");
        write();
    }

    /**
     * Get the minimum number of lines that should be displayed on each page.
     * This purely applies to font sizes, the font will be adjusted so this
     * amount of lines can fit on. This stops small lines becoming huge in the
     * preview window rather than displaying normally.
     * <p/>
     * @return the minimum line count.
     */
    public int getMinLines() {
        return Integer.parseInt(getProperty("min.lines", "10"));
    }

    /**
     * Set the min lines value.
     * <p/>
     * @param minLines the minimum line count.
     */
    public void setMinLines(int minLines) {
        setProperty("min.lines", Integer.toString(minLines));
        write();
    }

    /**
     * Determine whether the single monitor warning should be shown (this warns
     * the user they only have one monitor installed.)
     * <p/>
     * @return true if the warning should be shown, false otherwise.
     */
    public boolean showSingleMonitorWarning() {
        return Boolean.parseBoolean(getProperty("single.monitor.warning", "true"));
    }

    /**
     * Set whether the single monitor warning should be shown.
     * <p/>
     * @param val true if the warning should be shown, false otherwise.
     */
    public void setSingleMonitorWarning(boolean val) {
        setProperty("single.monitor.warning", Boolean.toString(val));
        write();
    }

    /**
     * Get the URL to download Quelea.
     * <p/>
     * @return the URL to download Quelea.
     */
    public String getDownloadLocation() {
        return getProperty("download.location", "http://code.google.com/p/quelea-projection/downloads/list");
    }

    /**
     * Get the URL to the Quelea website.
     * <p/>
     * @return the URL to the Quelea website.
     */
    public String getWebsiteLocation() {
        return getProperty("website.location", "http://www.quelea.org/");
    }

    /**
     * Get the URL to the Quelea discussion forum.
     * <p/>
     * @return the URL to the Quelea discussion forum.
     */
    public String getDiscussLocation() {
        return getProperty("discuss.location", "https://groups.google.com/group/quelea-discuss");
    }

    /**
     * Get the URL used for checking the latest version.
     * <p/>
     * @return the URL used for checking the latest version.
     */
    public String getUpdateURL() {
        return getProperty("update.url", "http://code.google.com/p/quelea-projection/");
    }

    /**
     * Determine whether we should check for updates each time the program
     * starts.
     * <p/>
     * @return true if we should check for updates, false otherwise.
     */
    public boolean checkUpdate() {
        return Boolean.parseBoolean(getProperty("check.update", "true"));
    }

    /**
     * Set whether we should check for updates each time the program starts.
     * <p/>
     * @param val true if we should check for updates, false otherwise.
     */
    public void setCheckUpdate(boolean val) {
        setProperty("check.update", Boolean.toString(val));
        write();
    }

    /**
     * Determine whether the first letter of all displayed lines should be a
     * capital.
     * <p/>
     * @return true if it should be a capital, false otherwise.
     */
    public boolean checkCapitalFirst() {
        return Boolean.parseBoolean(getProperty("capital.first", "false"));
    }

    /**
     * Set whether the first letter of all displayed lines should be a capital.
     * <p/>
     * @param val true if it should be a capital, false otherwise.
     */
    public void setCapitalFirst(boolean val) {
        setProperty("capital.first", Boolean.toString(val));
        write();
    }

    /**
     * Determine whether the song info text should be displayed.
     * <p/>
     * @return true if it should be a displayed, false otherwise.
     */
    public boolean checkDisplaySongInfoText() {
        return Boolean.parseBoolean(getProperty("display.songinfotext", "true"));
    }

    /**
     * Set whether the song info text should be displayed.
     * <p/>
     * @param val true if it should be displayed, false otherwise.
     */
    public void setDisplaySongInfoText(boolean val) {
        setProperty("display.songinfotext", Boolean.toString(val));
        write();
    }

    /**
     * Get the default bible to use.
     * <p/>
     * @return the default bible.
     */
    public String getDefaultBible() {
        return getProperty("default.bible");
    }

    /**
     * Set the default bible.
     * <p/>
     * @param bible the default bible.
     */
    public void setDefaultBible(Bible bible) {
        setProperty("default.bible", bible.getName());
        write();
    }

    /**
     * Get the colour used to display chords in stage view.
     * <p/>
     * @return the colour used to display chords in stage view.
     */
    public Color getStageChordColor() {
        return getColor(getProperty("stage.chord.color", "200,200,200"));
    }

    /**
     * Set the colour used to display chords in stage view.
     * <p/>
     * @param color the colour used to display chords in stage view.
     */
    public void setStageChordColor(Color color) {
        setProperty("stage.chord.color", getStr(color));
    }

    /**
     * Get the colour used to display lyrics in stage view.
     * <p/>
     * @return the colour used to display lyrics in stage view.
     */
    public Color getStageLyricsColor() {
        return getColor(getProperty("stage.lyrics.color", "255,255,255"));
    }

    /**
     * Set the colour used to display lyrics in stage view.
     * <p/>
     * @param color the colour used to display lyrics in stage view.
     */
    public void setStageLyricsColor(Color color) {
        setProperty("stage.lyrics.color", getStr(color));
    }

    /**
     * Set the colour used for the background in stage view.
     * <p/>
     * @param color the colour used for the background in stage view.
     */
    public void setStageBackgroundColor(Color color) {
        setProperty("stage.background.color", getStr(color));
    }

    /**
     * Get the colour used for the background in stage view.
     * <p/>
     * @return the colour used for the background in stage view.
     */
    public Color getStageBackgroundColor() {
        return getColor(getProperty("stage.background.color", "0,0,0"));
    }

    /**
     * Get a color from a string.
     * <p/>
     * @param str the string to use to get the color value.
     * @return the color.
     */
    private Color getColor(String str) {
        String[] color = str.split(",");
        double red = Double.parseDouble(color[0].trim());
        double green = Double.parseDouble(color[1].trim());
        double blue = Double.parseDouble(color[2].trim());
        if (red > 1 || green > 1 || blue > 1) {
            red /= 255;
            green /= 255;
            blue /= 255;
        }
        return new Color(red, green, blue, 1);
    }

    /**
     * Get a color value as a string.
     * <p/>
     * @param color the color to get as a string.
     * @return the color as a string.
     */
    private String getStr(Color color) {
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }

    /**
     * Get the colour used to signify an active list.
     * <p/>
     * @return the colour used to signify an active list.
     */
    public Color getActiveSelectionColor() {
        return getColor(getProperty("active.selection.color", "30,160,225"));
    }

    /**
     * Get the colour used to signify an active list.
     * <p/>
     * @return the colour used to signify an active list.
     */
    public Color getInactiveSelectionColor() {
        return getColor(getProperty("inactive.selection.color", "150,150,150"));
    }

    /**
     * Get the thickness of the outline to use for displaying the text.
     * <p/>
     * @return the outline thickness in pixels.
     */
    public int getOutlineThickness() {
        return Integer.parseInt(getProperty("outline.thickness", "2"));
    }

    /**
     * Set the outline thickness.
     * <p/>
     * @param px the outline thickness in pixels.
     */
    public void setOutlineThickness(int px) {
        setProperty("outline.thickness", Integer.toString(px));
        write();
    }

    /**
     * Get the notice box height (px).
     * <p/>
     * @return the notice box height.
     */
    public int getNoticeBoxHeight() {
        return Integer.parseInt(getProperty("notice.box.height", "40"));
    }

    /**
     * Set the notice box height (px).
     * <p/>
     * @param height the notice box height.
     */
    public void setNoticeBoxHeight(int height) {
        setProperty("notice.box.height", Integer.toString(height));
        write();
    }

    /**
     * Get the notice box speed.
     * <p/>
     * @return the notice box speed.
     */
    public int getNoticeBoxSpeed() {
        return Integer.parseInt(getProperty("notice.box.speed", "8"));
    }

    /**
     * Set the notice box speed.
     * <p/>
     * @param speed the notice box speed.
     */
    public void setNoticeBoxSpeed(int speed) {
        setProperty("notice.box.speed", Integer.toString(speed));
        write();
    }

    /**
     * Get the specially treated words that are auto-capitalised by the song
     * importer when deciding how to un-caps-lock a line of text.
     * <p/>
     * @return the array of God words, separated by commas in the properties
     * file.
     */
    public String[] getGodWords() {
        return getProperty("god.words",
                "god,God,jesus,Jesus,christ,Christ,you,You,he,He,lamb,Lamb,"
                + "lord,Lord,him,Him,son,Son,i,I,his,His,your,Your,king,King,"
                + "saviour,Saviour,savior,Savior,majesty,Majesty,alpha,Alpha,omega,Omega") //Yeah.. default testing properties.
                .trim().split(",");
    }

    /**
     * Get whether to use openoffice for presentations.
     * <p/>
     * @return true if we should use openoffice, false if we should just use the
     * basic POI images.
     */
    public boolean getUseOO() {
        return Boolean.parseBoolean(getProperty("use.oo", "false"));
    }

    /**
     * Set whether to use openoffice for presentations.
     * <p/>
     * @param val if we should use openoffice, false if we should just use the
     * basic POI images.
     */
    public void setUseOO(boolean val) {
        setProperty("use.oo", Boolean.toString(val));
        write();
    }

    /**
     * Get the path to the openoffice installation on this machine.
     * <p/>
     * @return the path to the openoffice installation on this machine.
     */
    public String getOOPath() {
        return getProperty("oo.path", "");
    }

    /**
     * Set the path to the openoffice installation on this machine.
     * <p/>
     * @param path the path to the openoffice installation on this machine.
     */
    public void setOOPath(String path) {
        setProperty("oo.path", path);
        write();
    }

    /**
     * Determine if the OO presentation should be always on top or not. Not user
     * controlled, but useful for testing.
     * <p/>
     * @return true if the presentation should be always on top, false
     * otherwise.
     */
    public boolean getOOPresOnTop() {
        return Boolean.parseBoolean(getProperty("oo.ontop", "true"));
    }

    /**
     * Determine if drag and drop is enabled
     * <p/>
     * @return true if we can use drag and drop functions
     */
    public boolean getDragAndDrop() {
        return Boolean.parseBoolean(getProperty("enable.drag.and.drop", "false"));
    }

    /**
     * Sets the logo image location for persistent use
     * <p/>
     * @param location File location
     */
    public void setLogoImage(String location) {
        setProperty("logo.image.location", location);
        write();
    }

    /**
     * Return the location of the logo image
     * <p/>
     * @return the logo image
     */
    public String getLogoImageURI() {
        return "file:" + getProperty("logo.image.location", "icons/logo default.png");
    }

    /**
     * Sets the port used for mobile lyrics display.
     * <p/>
     * @param port the port used for mobile lyrics display.
     */
    public void setMobLyricsPort(int port) {
        setProperty("mob.lyrics.port", Integer.toString(port));
        write();
    }

    /**
     * Gets the port used for mobile lyrics display.
     * <p/>
     * @return the port used for mobile lyrics display.
     */
    public int getMobLyricsPort() {
        return Integer.parseInt(getProperty("mob.lyrics.port", "1111"));
    }

    /**
     * Determine if we should use mobile lyrics.
     * <p>
     * @return true if we should, false otherwise.
     */
    public boolean getUseMobLyrics() {
        return Boolean.parseBoolean(getProperty("use.mob.lyrics", "false"));
    }

    /**
     * Set if we should use mobile lyrics.
     * <p>
     * @param val true if we should, false otherwise.
     */
    public void setUseMobLyrics(boolean val) {
        setProperty("use.mob.lyrics", Boolean.toString(val));
        write();
    }

    public void setUseRemoteControl(boolean val) {
        setProperty("use.remote.control", Boolean.toString(val));
        write();
    }

    /**
     * Set if we should set up remote control server
     * <p>
     * @param val true if we should, false otherwise.
     */
    public boolean getUseRemoteControl() {
        return Boolean.parseBoolean(getProperty("use.remote.control", "false"));
    }

    /**
     * Gets the port used for remote control server.
     * <p/>
     * @return the port used for mobile lyrics display.
     */
    public int getRemoteControlPort() {
        return Integer.parseInt(getProperty("remote.control.port", "1112"));
    }

    public void setRemoteControlPort(int port) {
        setProperty("remote.control.port", Integer.toString(port));
        write();
    }

    public void setRemoteControlPassword(String text) {
        setProperty("remote.control.password", text);
        write();
    }

    public String getRemoteControlPassword() {
        return getProperty("remote.control.password", "quelea");
    }

    public String getSmallTextPosition() {
        return getProperty("small.text.position", "right");
    }

    public void setSmallTextPosition(String position) {
        setProperty("small.text.position", position);
        write();
    }

    public boolean getSmallSongTextShow() {
        return Boolean.parseBoolean(getProperty("show.small.song.text", "true"));
    }

    public void setSmallSongTextShow(boolean show) {
        setProperty("show.small.song.text", Boolean.toString(show));
        write();
    }

    public boolean getSmallBibleTextShow() {
        return Boolean.parseBoolean(getProperty("show.small.bible.text", "true"));
    }

    public void setSmallBibleTextShow(boolean show) {
        setProperty("show.small.bible.text", Boolean.toString(show));
        write();
    }

    /**
     * Get how many words or verses to show per slide
     *
     * @return number of words or verses (depends on use.max.bible.verses)
     */
    public int getMaxBibleItems() {
        return Integer.parseInt(getProperty("max.bible.items", "5"));
    }

    public void setMaxBibleItems(int number) {
        setProperty("max.bible.items", Integer.toString(number));
        write();
    }

    /**
     * Get whether the max items is verses or words
     *
     * @return true if using maximum verses per slide
     */
    public boolean getBibleSectionVerses() {
        return Boolean.parseBoolean(getProperty("use.max.bible.verses", "true"));
    }

    public void setBibleSectionVerses(boolean useVerses) {
        setProperty("use.max.bible.verses", Boolean.toString(useVerses));
        write();
    }

    /**
     * Get the maximum number of characters allowed on any one line of bible
     * text.
     * <p/>
     * @return the maximum number of characters allowed on any one line of bible
     * text.
     */
    public int getMaxBibleChars() {
        return Integer.parseInt(getProperty("max.bible.chars", "80"));
    }

    /**
     * Set the max bible chars value.
     * <p/>
     * @param maxChars the maximum number of characters allowed on any one line
     * of bible text.
     */
    public void setMaxBibleChars(int maxChars) {
        setProperty("max.bible.chars", Integer.toString(maxChars));
        write();
    }
    
    /**
     * Get the fade duration of the logo button
     * text.
     * <p/>
     * @return the duration of the fade in milliseconds
     * text.
     */
    public int getLogoFadeDuration() {
        String t = getProperty("logo.fade.duration", "");
        if(t.equals("")) {
            setProperty("logo.fade.duration", "1000");
            write();
        }
        return Integer.parseInt(t);
    }
    
    /**
     * Get the fade duration of the black button
     * text.
     * <p/>
     * @return the duration of the fade in milliseconds
     * text.
     */
    public int getBlackFadeDuration() {
        String t = getProperty("black.fade.duration", "");
        if(t.equals("")) {
            setProperty("black.fade.duration", "1000");
            write();
        }
        return Integer.parseInt(t);
    }
    
    /**
     * Get the fade duration of the clear button
     * text.
     * <p/>
     * @return the duration of the fade in milliseconds
     * text.
     */
    public int getClearFadeDuration() {
        String t = getProperty("clear.fade.duration", "");
        if(t.equals("")) {
            setProperty("clear.fade.duration", "1000");
            write();
        }
        return Integer.parseInt(t);
    }

}
