/*
 * This file is part of Quelea, free projection software for churches.
 * 
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.quelea.windows.main;

import java.util.logging.Logger;
import javafx.scene.Cursor;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.quelea.services.languages.LabelGrabber;
import org.quelea.services.utils.LoggerUtils;
import org.quelea.services.utils.Utils;
import org.quelea.windows.main.DisplayCanvas.Priority;
import org.quelea.windows.multimedia.VLCMediaPlayer;

/**
 * The full screen window used for displaying the projection.
 * <p/>
 * @author Michael
 */
public class DisplayStage extends Stage {

    private static final Logger LOGGER = LoggerUtils.getLogger();
    private static final Cursor BLANK_CURSOR;
    private final DisplayCanvas canvas;

    /**
     * Initialise cursor hiding.
     */
    static {
        BLANK_CURSOR = javafx.scene.Cursor.NONE;
    }

    /**
     * Create a new display window positioned to fill the given rectangle.
     * <p/>
     * @param area the area in which the window should be drawn.
     */
    public DisplayStage(Bounds area, boolean stageView) {
        initStyle(StageStyle.UNDECORATED);
        Utils.addIconsToStage(this);
        setTitle(LabelGrabber.INSTANCE.getLabel("projection.window.title"));
        setArea(area);
        canvas = new DisplayCanvas(true, stageView, true, null, stageView ? Priority.HIGH : Priority.MID);
        canvas.setType(stageView ? DisplayCanvas.Type.STAGE : DisplayCanvas.Type.FULLSCREEN);
        canvas.setCursor(BLANK_CURSOR);
        Scene scene = new Scene(canvas);
        setScene(scene);
    }

    /**
     * Set the area of the display window.
     * <p/>
     * @param area the area of the window.
     */
    public final void setArea(final Bounds area) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setWidth(area.getMaxX() - area.getMinX());
                setHeight(area.getMaxY() - area.getMinY());
                setX(area.getMinX());
                setY(area.getMinY());
            }
        });
    }

    /**
     * Get the canvas object that underlines this display window.
     * <p/>
     * @return the lyric canvas backing this window.
     */
    public DisplayCanvas getCanvas() {
        return canvas;
    }
}
