package org.quelea.display;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.quelea.Schedule;
import org.quelea.Theme;
import org.quelea.utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A song that contains a number of sections (verses, choruses, etc.)
 * @author Michael
 */
public class Song implements Displayable, Searchable, Comparable<Song> {

    private String title;
    private String author;
    private List<SongSection> sections;
    private Theme theme;
    private int id;

    /**
     * Copy constructor - creates a shallow copy.
     * @param song the song to copy to create the new song.
     */
    public Song(Song song) {
        this.title = song.title;
        this.author = song.author;
        this.sections = song.sections;
        this.theme = song.theme;
        this.id = song.id;
    }

    /**
     * Create a new, empty song.
     * @param title the title of the song.
     * @param author the author of the song.
     */
    public Song(String title, String author) {
        this(title, author, new Theme(Theme.DEFAULT_FONT, Theme.DEFAULT_FONT_COLOR, Theme.DEFAULT_BACKGROUND));
    }

    /**
     * Create a new, empty song.
     * @param title the title of the song.
     * @param author the author of the song.
     * @param theme the theme of the song.
     */
    public Song(String title, String author, Theme theme) {
        id = -1;
        this.title = title;
        this.author = author;
        this.theme = theme;
        sections = new ArrayList<SongSection>();
    }

    /**
     * Get the unique ID of the song.
     * @return the ID of the song.
     */
    public int getID() {
        return id;
    }

    /**
     * Set the unique ID of this song.
     * @param id the id of the song.
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Get the title of this song.
     * @return the title of this song.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the song.
     * @param title the new song title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the author of this song.
     * @return the author of the song.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Set the author of the song.
     * @param author the new song author.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Get all the lyrics to this song as a string. This can be parsed using
     * the setLyrics() method.
     * @return the lyrics to this song.
     */
    public String getLyrics() {
        StringBuilder ret = new StringBuilder();
        for (SongSection section : sections) {
            if (section.getTitle() != null && !section.getTitle().equals("")) {
                ret.append(section.getTitle()).append("\n");
            }
            for (String line : section.getLyrics()) {
                ret.append(line).append("\n");
            }
            ret.append("\n");
        }
        return ret.toString().trim();
    }

    /**
     * Set the lyrics to this song as a string. This will erase any sections
     * currently in the song and parse the given lyrics into a number of
     * song sections.
     * @param lyrics the lyrics to set as this song's lyrics.
     */
    public void setLyrics(String lyrics) {
        sections.clear();
        for (String section : lyrics.split("\n\n")) {
            String[] sectionLines = section.split("\n");
            String[] newLyrics = section.split("\n");
            String sectionTitle = "";
            if (Utils.isTitle(sectionLines[0])) {
                sectionTitle = sectionLines[0];
                newLyrics = new String[sectionLines.length - 1];
                System.arraycopy(sectionLines, 1, newLyrics, 0, newLyrics.length);
            }
            sections.add(new SongSection(sectionTitle, newLyrics));
        }
    }

    /**
     * Add a section to this song.
     * @param section the section to add.
     */
    public void addSection(SongSection section) {
        if (section.getTheme() == null) {
            section.setTheme(theme);
        }
        sections.add(section);
    }

    /**
     * Add a number of sections to this song.
     * @param sections the sections to add.
     */
    public void addSections(SongSection[] sections) {
        for (SongSection section : sections) {
            addSection(section);
        }
    }

    /**
     * Get an array of all the sections in this song.
     * @return the song sections.
     */
    public SongSection[] getSections() {
        return sections.toArray(new SongSection[sections.size()]);
    }

    /**
     * Determine whether this song matches a particular search.
     * @param s the search term.
     * @return true if the song matches, false otherwise.
     */
    public boolean search(String s) {
        return title.toLowerCase().contains(s.toLowerCase());
    }

    /**
     * Get a representation of this song in XML format.
     * @return the song in XML format.
     */
    public String getXML() {
        StringBuilder xml = new StringBuilder();
        xml.append("<song>");
        xml.append("<title>");
        xml.append(title);
        xml.append("</title>");
        xml.append("<author>");
        xml.append(author);
        xml.append("</author>");
        xml.append("<lyrics>");
        for (SongSection section : sections) {
            xml.append(section.getXML());
        }
        xml.append("</lyrics>");
        xml.append("</song>");
        return xml.toString();
    }

    public static Song parseSong(String xml) {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            return parseXML(doc.getFirstChild());
        }
        catch (ParserConfigurationException ex) {
            return null;
        }
        catch (SAXException ex) {
            return null;
        }
        catch (IOException ex) {
            return null;
        }
    }

    /**
     * Parse a song in XML format and return the song object.
     * @param xml the xml string to parse.
     * @return the song, or null if an error occurs.
     */
    public static Song parseXML(Node song) {
        NodeList list = song.getChildNodes();
        String title = "";
        String author = "";
        List<SongSection> songSections = new ArrayList<SongSection>();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeName().equals("title")) {
                title = node.getTextContent();
            }
            if (node.getNodeName().equals("author")) {
                author = node.getTextContent();
            }
            if (node.getNodeName().equals("lyrics")) {
                NodeList sections = node.getChildNodes();
                for (int j = 0; j < sections.getLength(); j++) {
                    Node sectionNode = sections.item(j);
                    if (sectionNode.getNodeName().equals("section")) {
                        songSections.add(SongSection.parseXML(sectionNode));
                    }
                }
            }
        }
        Song ret = new Song(title, author, new Theme(Theme.DEFAULT_FONT, Theme.DEFAULT_FONT_COLOR, Theme.DEFAULT_BACKGROUND));
        for (SongSection section : songSections) {
            ret.addSection(section);
        }
        return ret;
    }

    /**
     * Generate a hashcode for this song.
     * @return the hashcode.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 29 * hash + (this.author != null ? this.author.hashCode() : 0);
        hash = 29 * hash + (this.sections != null ? this.sections.hashCode() : 0);
        hash = 29 * hash + (this.theme != null ? this.theme.hashCode() : 0);
        return hash;
    }

    /**
     * Determine whether this song equals another object.
     * @param obj the other object.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Song other = (Song) obj;
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if ((this.author == null) ? (other.author != null) : !this.author.equals(other.author)) {
            return false;
        }
        if (this.sections != other.sections && (this.sections == null || !this.sections.equals(other.sections))) {
            return false;
        }
        if (this.theme != other.theme && (this.theme == null || !this.theme.equals(other.theme))) {
            return false;
        }
        return true;
    }

    /**
     * Compare this song to another song, first by title and then by author.
     * @param other the other song.
     * @return 1 if this song is greater than the other song, 0 if they're
     * the same, and -1 if this is less than the other song.
     */
    public int compareTo(Song other) {
        int result = getTitle().compareToIgnoreCase(other.getTitle());
        if (result == 0) {
            result = getAuthor().compareToIgnoreCase(other.getAuthor());
            if (result == 0) {
                result = getLyrics().compareTo(other.getLyrics());
            }
        }
        return result;
    }

    /**
     * Get a string representation of this song.
     * @return a string representation of the song.
     */
    @Override
    public String toString() {
        return getXML();
    }
}
