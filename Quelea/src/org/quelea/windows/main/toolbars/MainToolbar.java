/*
 * This file is part of Quelea, free projection software for churches.
 * 
 * Copyright (C) 2012 Michael Berry
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
package org.quelea.windows.main.toolbars;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.quelea.languages.LabelGrabber;
import org.quelea.windows.main.actionlisteners.AddDVDActionListener;
import org.quelea.windows.main.actionlisteners.AddPowerpointActionListener;
import org.quelea.windows.main.actionlisteners.AddVideoActionListener;
import org.quelea.windows.main.actionlisteners.NewScheduleActionListener;
import org.quelea.windows.main.actionlisteners.NewSongActionListener;
import org.quelea.windows.main.actionlisteners.OpenScheduleActionListener;
import org.quelea.windows.main.actionlisteners.PrintScheduleActionListener;
import org.quelea.windows.main.actionlisteners.SaveScheduleActionListener;
import org.quelea.windows.main.actionlisteners.ShowNoticesActionListener;
import org.quelea.windows.main.actionlisteners.ViewTagsActionListener;

/**
 * Quelea's main toolbar.
 *
 * @author Michael
 */
public class MainToolbar extends ToolBar {

    private Button newScheduleButton;
    private Button openScheduleButton;
    private Button saveScheduleButton;
    private Button printScheduleButton;
    private Button newSongButton;
    private Button addPresentationButton;
    private Button addVideoButton;
    private Button addDVDButton;
    private Button manageNoticesButton;
    private Button manageTagsButton;

    /**
     * Create the toolbar.
     */
    public MainToolbar() {
        newScheduleButton = new Button("", new ImageView(new Image("file:icons/filenew.png", 24, 24, false, true)));
        newScheduleButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("new.schedule.tooltip")));
        newScheduleButton.setOnAction(new NewScheduleActionListener());
        getItems().add(newScheduleButton);

        openScheduleButton = new Button("", new ImageView(new Image("file:icons/fileopen.png", 24, 24, false, true)));
        openScheduleButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("open.schedule.tooltip")));
        openScheduleButton.setOnAction(new OpenScheduleActionListener());
        getItems().add(openScheduleButton);

        saveScheduleButton = new Button("", new ImageView(new Image("file:icons/filesave.png", 24, 24, false, true)));
        saveScheduleButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("save.schedule.tooltip")));
        saveScheduleButton.setOnAction(new SaveScheduleActionListener(false));
        getItems().add(saveScheduleButton);

        printScheduleButton = new Button("", new ImageView(new Image("file:icons/fileprint.png", 24, 24, false, true)));
        printScheduleButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("print.schedule.tooltip")));
        printScheduleButton.setOnAction(new PrintScheduleActionListener());
        getItems().add(printScheduleButton);

        getItems().add(new Separator());

        newSongButton = new Button("", new ImageView(new Image("file:icons/newsong.png", 24, 24, false, true)));
        newSongButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("new.song.tooltip")));
        newSongButton.setOnAction(new NewSongActionListener());
        getItems().add(newSongButton);

        getItems().add(new Separator());

        addPresentationButton = new Button("", new ImageView(new Image("file:icons/powerpoint.png", 24, 24, false, true)));
        addPresentationButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("add.presentation.tooltip")));
        addPresentationButton.setOnAction(new AddPowerpointActionListener());
        getItems().add(addPresentationButton);

        addVideoButton = new Button("", new ImageView(new Image("file:icons/video file.png", 24, 24, false, true)));
        addVideoButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("add.video.tooltip")));
        addVideoButton.setOnAction(new AddVideoActionListener());
        getItems().add(addVideoButton);

        addDVDButton = new Button("", new ImageView(new Image("file:icons/dvd.png", 24, 24, false, true)));
        addDVDButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("add.dvd.tooltip")));
        addDVDButton.setOnAction(new AddDVDActionListener());
        getItems().add(addDVDButton);

        getItems().add(new Separator());

        manageTagsButton = new Button("", new ImageView(new Image("file:icons/tag.png", 24, 24, false, true)));
        manageTagsButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("manage.tags.tooltip")));
        manageTagsButton.setOnAction(new ViewTagsActionListener());
        getItems().add(manageTagsButton);

        manageNoticesButton = new Button("", new ImageView(new Image("file:icons/info.png", 24, 24, false, true)));
        manageNoticesButton.setTooltip(new Tooltip(LabelGrabber.INSTANCE.getLabel("manage.notices.tooltip")));
        manageNoticesButton.setOnAction(new ShowNoticesActionListener());
        getItems().add(manageNoticesButton);
        
    }
}
