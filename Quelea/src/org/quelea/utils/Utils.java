package org.quelea.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * General utility class containing a bunch of static methods.
 * @author Michael
 */
public final class Utils {

    private static final Logger LOGGER = LoggerUtils.getLogger();

    /**
     * Don't instantiate me. I bite.
     */
    private Utils() {
        throw new AssertionError();
    }

    /**
     * Escape the XML special characters.
     * @param s the string to escape.
     * @return the escaped string.
     */
    public static String escapeXML(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    /**
     * Get an image icon from the location of a specified file.
     * @param location the location of the image to use.
     * @return the icon formed from the image, or null if an IOException
     * occured.
     */
    public static Icon getImageIcon(String location) {
        Image image = getImage(location);
        if(image==null) {
            return null;
        }
        return new ImageIcon(image);
    }

    /**
     * Get an image from the location of a specified file.
     * @param location the location of the image to use.
     * @return the icon formed from the image, or null if an IOException
     * occured.
     */
    public static BufferedImage getImage(String location) {
        try {
            return ImageIO.read(new File(location));
        }
        catch(IOException ex) {
            LOGGER.log(Level.WARNING, "Couldn't get image: " + location, ex);
            return null;
        }
    }

    /**
     * Determine whether the given string is the title of a song section.
     * @param title the title to check.
     * @return true if it is a valid title, false otherwise.
     */
    public static boolean isTitle(String title) {
        return title.toLowerCase().startsWith("verse") ||
                title.toLowerCase().startsWith("chorus") ||
                title.toLowerCase().startsWith("tag") ||
                title.toLowerCase().startsWith("pre-chorus") ||
                title.toLowerCase().startsWith("pre chorus") ||
                title.toLowerCase().startsWith("coda") ||
                title.toLowerCase().startsWith("bridge");
    }

    /**
     * Get the names of all the fonts available on the current system.
     * @return the names of all the fonts available.
     */
    public static String[] getAllFonts() {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        String[] ret = new String[fonts.length];
        for(int i=0 ; i<fonts.length ; i++) {
            ret[i] = fonts[i].getName();
        }
        return ret;
    }

    /**
     * Get an image filled with the specified colour.
     * @param color the colour of the image.
     * @param width the width of the image.
     * @param height the height of the image.
     * @return the image.
     */
    public static BufferedImage getImageFromColour(Color color, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, width, height);
        return image;
    }

    /**
     * Parse a colour string to a colour.
     * @param colour the colour string.
     * @return the colour.
     */
    public static Color parseColour(String colour) {
        colour = colour.substring(colour.indexOf('[')+1, colour.indexOf(']'));
        String[] parts = colour.split(",");
        int red = Integer.parseInt(parts[0].split("=")[1]);
        int green = Integer.parseInt(parts[1].split("=")[1]);
        int blue = Integer.parseInt(parts[2].split("=")[1]);
        return new Color(red, green, blue);
    }

}
