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
package org.quelea.windows.main.schedule;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.quelea.data.displayable.Displayable;
import org.quelea.languages.LabelGrabber;
import org.quelea.windows.main.QueleaApp;
import org.quelea.windows.main.actionhandlers.RemoveScheduleItemActionHandler;

/**
 * The panel displaying the schedule / order of service. Items from here are
 * loaded into the preview panel where they are viewed and then projected live.
 * Items can be added here from the library.
 * <p/>
 * @author Michael
 */
public class SchedulePanel extends BorderPane {

    private final ScheduleList scheduleList;
    private final Button removeButton;
    private final Button upButton;
    private final Button downButton;
    private final Button themeButton;
    private final ScheduleThemeNode scheduleThemeNode;

    /**
     * Create and initialise the schedule panel.
     */
    public SchedulePanel() {
        scheduleList = new ScheduleList();
        scheduleList.itemsProperty().addListener(new ChangeListener<ObservableList<Displayable>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<Displayable>> ov, ObservableList<Displayable> t, ObservableList<Displayable> t1) {
                scheduleThemeNode.updateTheme();
            }
        });

        scheduleThemeNode = new ScheduleThemeNode(scheduleList);
        themeButton = new Button("",new ImageView(new Image("file:icons/settings.png", 16, 16, false, true)));
        themeButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                ContextMenu themeMenu = new ContextMenu();
                MenuItem themeItem = new MenuItem("", scheduleThemeNode);
                themeItem.setDisable(true);
                themeItem.setStyle("-fx-background-color: #000000;");
                themeMenu.getItems().add(themeItem);
                themeMenu.show(themeButton, Side.BOTTOM, 0, 0);
            }
        });
        themeButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("adjust.theme.tooltip")));

        ToolBar toolbar = new ToolBar();
        toolbar.setOrientation(Orientation.VERTICAL);
        removeButton = new Button("",new ImageView(new Image("file:icons/cross.png")));
        removeButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("remove.song.schedule.tooltip")));
        removeButton.setDisable(true);
        removeButton.setOnAction(new RemoveScheduleItemActionHandler());

        upButton = new Button("",new ImageView(new Image("file:icons/up.png")));
        upButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("move.up.schedule.tooltip")));
        upButton.setDisable(true);
        upButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent t) {
                scheduleList.moveCurrentItem(ScheduleList.Direction.UP);
            }
        });

        downButton = new Button("",new ImageView(new Image("file:icons/down.png")));
        downButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("move.down.schedule.tooltip")));
        downButton.setDisable(true);
        downButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent t) {
                scheduleList.moveCurrentItem(ScheduleList.Direction.DOWN);
            }
        });
        
        scheduleList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Displayable>() {

            @Override
            public void changed(ObservableValue<? extends Displayable> ov, Displayable t, Displayable t1) {
                if(scheduleList.selectionModelProperty().get().isEmpty()) {
                    removeButton.setDisable(true);
                    upButton.setDisable(true);
                    downButton.setDisable(true);
                }
                else {
                    removeButton.setDisable(false);
                    upButton.setDisable(false);
                    downButton.setDisable(false);
                    QueleaApp.get().getMainWindow().getMainPanel().getPreviewPanel().setDisplayable(scheduleList.selectionModelProperty().get().getSelectedItem(), 0);
                }
            }
        });

        ToolBar header = new ToolBar();
        Label headerLabel = new Label(LabelGrabber.INSTANCE.getLabel("order.service.heading"));
        headerLabel.setStyle("-fx-font-weight: bold;");
        header.getItems().add(headerLabel);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getItems().add(spacer);
        header.getItems().add(themeButton);

        toolbar.getItems().add(removeButton);
        toolbar.getItems().add(upButton);
        toolbar.getItems().add(downButton);

        setTop(header);
        setLeft(toolbar);
        setCenter(scheduleList);
    }

    /**
     * Get the schedule list backing this panel.
     * <p/>
     * @return the schedule list.
     */
    public ScheduleList getScheduleList() {
        return scheduleList;
    }

}