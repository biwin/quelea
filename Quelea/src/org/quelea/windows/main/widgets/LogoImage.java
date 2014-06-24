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
package org.quelea.windows.main.widgets;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.quelea.services.utils.ImageManager;
import org.quelea.services.utils.QueleaProperties;
import org.quelea.services.utils.Utils;
import org.quelea.windows.main.DisplayType;

/**
 * The logo image - on a separate stack pane with its background colour set so
 * we don't see any content behind the panel when its aspect ratio doesn't line
 * up exactly with the display canvas size!
 * <p>
 *
 * @author Michael
 */
public class LogoImage extends StackPane {

    private final ImageView logoImage;
    private boolean stageView;
    private boolean textOnlyView;
    /**
     * Create a new logo image
     * <p>
     * @param typeOfDisplay Gets the type of display for this image. view, else
     * false.
     */
    public LogoImage(DisplayType typeOfDisplay) {
        if (typeOfDisplay == DisplayType.STAGE) {
            this.stageView = true;
        } else {
            this.stageView = false;
        }
        if (typeOfDisplay == DisplayType.TEXT_ONLY) {
            this.textOnlyView = true;
        } else {
            this.textOnlyView = false;
        }
        logoImage = new ImageView();
        setStyle("-fx-background-color:#000000;");
        refresh();
        getChildren().add(logoImage);
    }

    /**
     * Update this logo image with the correct one from the properties file
     */
    public final void refresh() {
        if (stageView) {
            logoImage.setImage(Utils.getImageFromColour(QueleaProperties.get().getStageBackgroundColor()));
        } else if (textOnlyView) {
            logoImage.setImage(Utils.getImageFromColour(QueleaProperties.get().getTextOnlyBackgroundColor()));
        } else {
            logoImage.setImage(ImageManager.INSTANCE.getImage(QueleaProperties.get().getLogoImageURI()));
            logoImage.setPreserveRatio(true);
        }
        logoImage.fitWidthProperty().bind(widthProperty());
        logoImage.fitHeightProperty().bind(heightProperty());
    }
}
