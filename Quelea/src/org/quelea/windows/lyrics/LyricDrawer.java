package org.quelea.windows.lyrics;

import org.quelea.windows.main.DisplayableDrawer;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.quelea.data.ColourBackground;
import org.quelea.data.ImageBackground;
import org.quelea.data.ThemeDTO;
import org.quelea.data.VideoBackground;
import org.quelea.data.displayable.Displayable;
import org.quelea.data.displayable.TextDisplayable;
import org.quelea.data.displayable.TextSection;
import org.quelea.services.utils.LoggerUtils;
import org.quelea.services.utils.QueleaProperties;
import org.quelea.services.utils.Utils;
import org.quelea.windows.multimedia.MediaPlayerFactory;

/**
 * @author tomaszpio@gmail.com
 */
public class LyricDrawer extends DisplayableDrawer {

    private static final Logger LOGGER = LoggerUtils.getLogger();
    private String[] text;
    private Group textGroup;
    private Paint lastColor;
    private ThemeDTO theme;
    private TextDisplayable curDisplayable;
    private boolean capitaliseFirst;
    private ImageView blackImg = new ImageView(Utils.getImageFromColour(Color.BLACK));

    ;

    public LyricDrawer() {
        text = new String[]{};
        theme = ThemeDTO.DEFAULT_THEME;
        textGroup = new Group();

    }

    private void drawText() {
        if (!canvas.getChildren().contains(canvas.getBackground())
                && !canvas.getChildren().contains(textGroup)) {
            canvas.getChildren().add(0, canvas.getBackground());
            canvas.getChildren().add(textGroup);
        }
        if (canvas.isCleared() || canvas.isBlacked()) {
            textGroup.getChildren().clear();
            return;
        }
        Font font = theme.getFont();
        if (font == null) {
            font = ThemeDTO.DEFAULT_FONT;
        }
        DropShadow shadow = theme.getShadow();
        if (shadow == null) {
            shadow = ThemeDTO.DEFAULT_SHADOW;
        }

        List<String> newText = sanctifyText();
        double fontSize = pickFontSize(font, newText, canvas.getWidth(), canvas.getHeight());
        font = Font.font(font.getName(), fontSize);
        FontMetrics metrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
        int y = 0;
        final Group newTextGroup = new Group();
        StackPane.setAlignment(newTextGroup, QueleaProperties.get().getTextPosition().getLayouPos());
        canvas.getChildren().clear();
        canvas.getChildren().add(newTextGroup);


        ParallelTransition paintTransition = new ParallelTransition();
        for (String line : newText) {
            Text t = new Text(line);
            double width = metrics.computeStringWidth(line);
            double centreOffset = (canvas.getWidth() - width) / 2;
            t.setFont(font);
            t.setEffect(shadow);
            t.setX(centreOffset);
            t.setY(y);
            if (theme.getFontPaint() == lastColor || lastColor == null) {
                t.setFill(theme.getFontPaint());
            } else {
                Timeline paintTimeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(t.fillProperty(), lastColor)),
                        new KeyFrame(Duration.seconds(0.3), new KeyValue(t.fillProperty(), theme.getFontPaint())));
                paintTransition.getChildren().add(paintTimeline);
            }
            y += metrics.getLineHeight();
            newTextGroup.getChildren().add(t);
        }
        if (!paintTransition.getChildren().isEmpty()) {
            paintTransition.play();
        }
        lastColor = theme.getFontPaint();

        textGroup = newTextGroup;
    }

    /**
     * Set the theme of this canvas.
     * <p/>
     * @param theme the theme to place on the canvas.
     */
    public void setTheme(ThemeDTO theme) {
        if (theme == null) {
            theme = ThemeDTO.DEFAULT_THEME;
        }
        if (this.theme.equals(theme)) {
            return;
        }
        this.theme = theme;
        Image image;
        if (theme.getBackground() instanceof ImageBackground) {
            image = ((ImageBackground) theme.getBackground()).getImage();
        } else if (theme.getBackground() instanceof ColourBackground) {
            Color color = ((ColourBackground) theme.getBackground()).getColour();
            image = Utils.getImageFromColour(color);
        } else if (theme.getBackground() instanceof VideoBackground) {
            image = null;
        } else {
            throw new AssertionError("Bug: Unhandled theme case");
        }
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), canvas.getBackground());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        Node newBackground;
        if (image == null) {
            final MediaView newVideo = new MediaView();
            String location = ((VideoBackground) theme.getBackground()).getVideoFile().toURI().toString();
            try {
                MediaPlayer player = MediaPlayerFactory.getInstance(location);
                player.setVolume(0);
                player.setAutoPlay(true);
                player.setCycleCount(javafx.scene.media.MediaPlayer.INDEFINITE);
                newVideo.setMediaPlayer(player);
                canvas.getChildren().add(0, newVideo);
                newBackground = newVideo;
            } catch (MediaException ex) {
                return;
                //Don't shout about it at this point.
            }
        } else {
            final ImageView newImage = canvas.getNewImageView();
            newImage.setFitHeight(canvas.getHeight());
            newImage.setFitWidth(canvas.getWidth());
            newImage.setImage(image);
            canvas.getChildren().add(0, newImage);
            newBackground = newImage;
        }
        final Node oldBackground = canvas.getBackground();
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                canvas.getChildren().remove(oldBackground);
            }
        });
        canvas.setBackground(newBackground);
        fadeOut.play();
    }

    /**
     * Get the theme currently in use on the canvas.
     * <p/>
     * @return the current theme
     */
    public ThemeDTO getTheme() {
        return theme;
    }

    @Override
    public void requestFocus() {
    }

    /**
     * Set whether the first of each line should be capitalised.
     * <p/>
     * @param val true if the first character should be, false otherwise.
     */
    public void setCapitaliseFirst(boolean val) {
        this.capitaliseFirst = val;
    }

    private double pickFontSize(Font font, List<String> text, double width, double height) {
        FontMetrics metrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
        double totalHeight = (metrics.getLineHeight()) * text.size();
        while (totalHeight > height) {
            font = new Font(font.getName(), font.getSize() - 0.5);
            if (font.getSize() < 1) {
                return 1;
            }
            metrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
            totalHeight = (metrics.getLineHeight()) * text.size();
        }

        String longestLine = longestLine(font, text);
        double totalWidth = metrics.computeStringWidth(longestLine);
        while (totalWidth > width) {
            font = new Font(font.getName(), font.getSize() - 0.5);
            if (font.getSize() < 1) {
                return 1;
            }
            metrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
            totalWidth = metrics.computeStringWidth(longestLine);
        }

        return font.getSize();
    }

    private String longestLine(Font font, List<String> text) {
        FontMetrics metrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
        double longestWidth = -1;
        String longestStr = null;
        for (String line : text) {
            double width = metrics.computeStringWidth(line);
            if (width > longestWidth) {
                longestWidth = width;
                longestStr = line;
            }
        }
        return longestStr;
    }

    /**
     * Take the raw text and format it into a number of lines nicely, where the
     * lines aren't more than the maximum length.
     * <p/>
     * @return processed, sanctified text that can be displayed nicely.
     */
    private List<String> sanctifyText() {
        List<String> ret = new ArrayList<>();
        int maxLength = QueleaProperties.get().getMaxChars();
        for (String line : text) {
            if (canvas.isStageView()) {
                ret.add(line);
            } else {
                ret.addAll(splitLine(line, maxLength));
            }
        }
        return ret;
    }

    /**
     * Given a line of any length, sensibly split it up into several lines.
     * <p/>
     * @param line the line to split.
     * @return the split line (or the unaltered line if it is less than or equal
     * to the allowed length.
     */
    private List<String> splitLine(String line, int maxLength) {
        List<String> sections = new ArrayList<>();
        if (line.length() > maxLength) {
            if (containsNotAtEnd(line, ";")) {
                for (String s : splitMiddle(line, ';')) {
                    sections.addAll(splitLine(s, maxLength));
                }
            } else if (containsNotAtEnd(line, ",")) {
                for (String s : splitMiddle(line, ',')) {
                    sections.addAll(splitLine(s, maxLength));
                }
            } else if (containsNotAtEnd(line, " ")) {
                for (String s : splitMiddle(line, ' ')) {
                    sections.addAll(splitLine(s, maxLength));
                }
            } //            else if(containsNotAtEnd(line, "-")) {
            //                for(String s : splitMiddle(line, '-')) {
            //                    sections.addAll(splitLine(s, maxLength));
            //                }
            //            }
            else {
                sections.addAll(splitLine(new StringBuilder(line).insert(line.length() / 2, "-").toString(), maxLength));
            }
        } else {
            if (!canvas.isStageView()) {
                line = line.trim();
            }
            if (capitaliseFirst && QueleaProperties.get().checkCapitalFirst()) {
                line = Utils.capitaliseFirst(line);
            }
            sections.add(line);
        }
        return sections;
    }

    /**
     * Determine if the given line contains the given string in the middle 80%
     * of the line.
     * <p/>
     * @param line the line to check.
     * @param str the string to use.
     * @return true if the line contains the delimiter, false otherwise.
     */
    private static boolean containsNotAtEnd(String line, String str) {
        final int percentage = 80;
        int removeChars = (int) ((double) line.length() * ((double) (100 - percentage) / 100));
        return line.substring(removeChars, line.length() - removeChars).contains(str);
    }

    /**
     * Split a string with the given delimiter into two parts, using the
     * delimiter closest to the middle of the string.
     * <p/>
     * @param line the line to split.
     * @param delimiter the delimiter.
     * @return an array containing two strings split in the middle by the
     * delimiter.
     */
    private static String[] splitMiddle(String line, char delimiter) {
        final int middle = (int) (((double) line.length() / 2) + 0.5);
        int nearestIndex = -1;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == delimiter) {
                int curDistance = Math.abs(nearestIndex - middle);
                int newDistance = Math.abs(i - middle);
                if (newDistance < curDistance || nearestIndex < 0) {
                    nearestIndex = i;
                }
            }
        }
        return new String[]{line.substring(0, nearestIndex + 1), line.substring(nearestIndex + 1, line.length())};
    }

    public void setText(TextDisplayable displayable, int index) {
        boolean fade;
        if (curDisplayable == displayable) {
            fade = false;
        } else {
            fade = true;
        }
        curDisplayable = displayable;
        setText(displayable.getSections()[index].getText(false, false), displayable.getSections()[index].getSmallText(), fade);
    }

    /**
     * Set the text to appear on the canvas. The lines will be automatically
     * wrapped and if the text is too large to fit on the screen in the current
     * font, the size will be decreased until all the text fits.
     * <p/>
     * @param text an array of the lines to display on the canvas, one entry in
     * the array is one line.
     * @param smallText an array of the small lines to be displayed on the
     * canvas.
     */
    public void setText(String[] text, String[] smallText, boolean fade) {
        if (text == null) {
            text = new String[0];
        }
        if (smallText == null) {
            smallText = new String[0];
        }
        this.text = Arrays.copyOf(text, text.length);
        draw(curDisplayable);
    }

    /**
     * Erase all the text on the canvas.
     */
    public void eraseText() {
        setText(null, null, true);
    }

    /**
     * Get the text currently set to appear on the canvas. The text may or may
     * not be shown depending on whether the canvas is blacked or cleared.
     * <p/>
     * @return the current text.
     */
    public String[] getText() {
        return Arrays.copyOf(text, text.length);
    }

    public void draw(Displayable displayable) {
        drawText();
        if (canvas.isBlacked()) {
            if (canvas.getChildren().contains(canvas.getBackground())) {
                canvas.getChildren().add(0, blackImg);
                canvas.getChildren().remove(canvas.getBackground());
            }
        } else {
            if (!canvas.getChildren().contains(canvas.getBackground())) {
                canvas.getChildren().remove(blackImg);
                canvas.getChildren().add(0, canvas.getBackground());
            }
        }
        if (canvas.getBackground() instanceof ImageView) {
            ImageView imgBackground = (ImageView) canvas.getBackground();
            imgBackground.setFitHeight(canvas.getHeight());
            imgBackground.setFitWidth(canvas.getWidth());
        } else if (canvas.getBackground() instanceof MediaView) {
            MediaView vidBackground = (MediaView) canvas.getBackground();
            vidBackground.setPreserveRatio(false);
            vidBackground.setFitHeight(canvas.getHeight());
            vidBackground.setFitWidth(canvas.getWidth());
        } else {
            LOGGER.log(Level.WARNING, "BUG: Unrecognised image background");
        }
        blackImg.setFitHeight(canvas.getHeight());
        blackImg.setFitWidth(canvas.getWidth());
    }

    @Override
    public void clear() {
        if (canvas.getChildren() != null) {
            canvas.getChildren().clear();
        }
        setTheme(null);
        eraseText();
    }

    public void updateCanvas(Displayable displayable, SelectLyricsList lyricsList, int selectedIndex) {
        curDisplayable = (TextDisplayable) displayable;
        TextSection currentSection = lyricsList.itemsProperty().get().get(selectedIndex);
        if (currentSection.getTempTheme() != null) {
            setTheme(currentSection.getTempTheme());
        } else {
            setTheme(currentSection.getTheme());
        }
        setCapitaliseFirst(currentSection.shouldCapitaliseFirst());
        setText(curDisplayable, selectedIndex);
    }
}
