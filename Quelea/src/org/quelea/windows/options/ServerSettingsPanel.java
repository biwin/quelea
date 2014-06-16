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
package org.quelea.windows.options;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.quelea.server.RCHandler;
import org.quelea.services.languages.LabelGrabber;
import org.quelea.services.utils.LoggerUtils;
import org.quelea.services.utils.PropertyPanel;
import org.quelea.services.utils.QueleaProperties;
import org.quelea.windows.main.QueleaApp;

/**
 * The panel that shows the mobile lyrics and remote control options.
 * <p>
 * @author Michael and Ben
 */
public class ServerSettingsPanel extends GridPane implements PropertyPanel {

    private static final Logger LOGGER = LoggerUtils.getLogger();
    private final CheckBox useMobLyricsCheckBox;
    private final TextField mlPortNumTextField;
    private String mlPrevPortNum;
    private boolean mlPrevChecked;
    private final CheckBox useRemoteControlCheckBox;
    private final TextField rcPortNumTextField;
    private String rcPrevPortNum;
    private boolean rcPrevChecked;
    private final TextField rcPasswordTextField;
    private String rcPrevPassword;

    /**
     * Create the server settings panel.
     */
    public ServerSettingsPanel() {
        setVgap(5);
        setPadding(new Insets(5));

        useMobLyricsCheckBox = new CheckBox();
        mlPortNumTextField = new TextField();
        useRemoteControlCheckBox = new CheckBox();
        rcPortNumTextField = new TextField();
        rcPasswordTextField = new TextField();

        setupMobLyrics();
        Label blank = new Label("");
        GridPane.setConstraints(blank, 2, 1);
        getChildren().add(blank);
        setupRemoteControl();
        readProperties();
    }

    private void setupMobLyrics() {
        Label useMobLyricsLabel = new Label(LabelGrabber.INSTANCE.getLabel("use.mobile.lyrics.label") + " ");
        GridPane.setConstraints(useMobLyricsLabel, 1, 1);
        getChildren().add(useMobLyricsLabel);
        GridPane.setConstraints(useMobLyricsCheckBox, 2, 1);
        getChildren().add(useMobLyricsCheckBox);

        Label portNumberLabel = new Label(LabelGrabber.INSTANCE.getLabel("port.number.label") + " ");
        GridPane.setConstraints(portNumberLabel, 1, 2);
        getChildren().add(portNumberLabel);
        mlPortNumTextField.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                checkDifferent();
            }
        });
        mlPortNumTextField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                String text = t.getCharacter();
                char arr[] = text.toCharArray();
                char ch = arr[text.toCharArray().length - 1];
                if (!(ch >= '0' && ch <= '9')) {
                    t.consume();
                }
                try {
                    String newText = mlPortNumTextField.getText() + ch;
                    int num = Integer.parseInt(newText);
                    if (num > 65535 || num <= 0) {
                        t.consume();
                    }
                } catch (NumberFormatException ex) {
                    t.consume();
                }
                checkDifferent();
            }
        });
        mlPortNumTextField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(mlPortNumTextField, Priority.ALWAYS);
        portNumberLabel.setLabelFor(mlPortNumTextField);
        GridPane.setConstraints(mlPortNumTextField, 2, 2);
        getChildren().add(mlPortNumTextField);
        HBox mobBox = new HBox(5);
        mobBox.getChildren().add(new Text(LabelGrabber.INSTANCE.getLabel("navigate.mob.url.label") + ": "));
        Text mobUrlLabel = new Text(getMLURL());
        if (Desktop.isDesktopSupported() && getMLURL().startsWith("http")) {
            mobUrlLabel.setCursor(Cursor.HAND);
            mobUrlLabel.setFill(Color.BLUE);
            mobUrlLabel.setStyle("-fx-underline: true;");
            mobUrlLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    try {
                        Desktop.getDesktop().browse(new URI(getMLURL()));
                    } catch (IOException | URISyntaxException ex) {
                        LOGGER.log(Level.WARNING, "Couldn't browse to mobile lyrics URL: {0}", getMLURL());
                    }
                }
            });
        }
        mobBox.getChildren().add(mobUrlLabel);
        GridPane.setConstraints(mobBox, 1, 3);
        getChildren().add(mobBox);
    }

    private void setupRemoteControl() {
        Label useRemoteControlLabel = new Label(LabelGrabber.INSTANCE.getLabel("use.remote.control.label") + " ");
        GridPane.setConstraints(useRemoteControlLabel, 1, 4);
        getChildren().add(useRemoteControlLabel);
        GridPane.setConstraints(useRemoteControlCheckBox, 2, 4);
        getChildren().add(useRemoteControlCheckBox);

        Label portNumberLabel = new Label(LabelGrabber.INSTANCE.getLabel("port.number.label") + " ");
        GridPane.setConstraints(portNumberLabel, 1, 5);
        getChildren().add(portNumberLabel);
        rcPortNumTextField.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                checkDifferent();
            }
        });
        rcPortNumTextField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                String text = t.getCharacter();
                char arr[] = text.toCharArray();
                char ch = arr[text.toCharArray().length - 1];
                if (!(ch >= '0' && ch <= '9')) {
                    t.consume();
                }
                try {
                    String newText = rcPortNumTextField.getText() + ch;
                    int num = Integer.parseInt(newText);
                    if (num > 65535 || num <= 0) {
                        t.consume();
                    }
                } catch (NumberFormatException ex) {
                    t.consume();
                }
                checkDifferent();
            }
        });
        rcPortNumTextField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(rcPortNumTextField, Priority.ALWAYS);
        portNumberLabel.setLabelFor(rcPortNumTextField);
        GridPane.setConstraints(rcPortNumTextField, 2, 5);
        getChildren().add(rcPortNumTextField);
        HBox rcBox = new HBox(5);
        rcBox.getChildren().add(new Text(LabelGrabber.INSTANCE.getLabel("navigate.remote.control.label") + ": "));
        Text rcUrlLabel = new Text(getRCURL());
        if (Desktop.isDesktopSupported() && getRCURL().startsWith("http")) {
            rcUrlLabel.setCursor(Cursor.HAND);
            rcUrlLabel.setFill(Color.BLUE);
            rcUrlLabel.setStyle("-fx-underline: true;");
            rcUrlLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    try {
                        Desktop.getDesktop().browse(new URI(getRCURL()));
                    } catch (IOException | URISyntaxException ex) {
                        LOGGER.log(Level.WARNING, "Couldn't browse to remote control URL: {0}", getRCURL());
                    }
                }
            });
        }
        rcBox.getChildren().add(rcUrlLabel);
        GridPane.setConstraints(rcBox, 1, 6);
        getChildren().add(rcBox);
        
        Label passwordLabel = new Label(LabelGrabber.INSTANCE.getLabel("remote.control.password") + " ");
        GridPane.setConstraints(passwordLabel, 1, 7);
        getChildren().add(passwordLabel);
        rcPasswordTextField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(rcPasswordTextField, Priority.ALWAYS);
        passwordLabel.setLabelFor(rcPasswordTextField);
        GridPane.setConstraints(rcPasswordTextField, 2, 7);
        getChildren().add(rcPasswordTextField);
    }

    private String urlMLCache;
    private String urlRCCache;

    private String getMLURL() {
        if (urlMLCache == null) {
            if (QueleaProperties.get().getUseMobLyrics() && QueleaApp.get().getMobileLyricsServer() != null) {
                String ip = getIP();
                if (ip != null) {
                    StringBuilder ret = new StringBuilder("http://");
                    ret.append(ip);
                    int port = QueleaProperties.get().getMobLyricsPort();
                    if (port != 80) {
                        ret.append(":");
                        ret.append(port);
                    }
                    urlMLCache = ret.toString();
                } else {
                    urlMLCache = "[Not started]";
                }
            } else {
                urlMLCache = "[Not started]";
            }
        }
        return urlMLCache;
    }
    
    private String getRCURL() {
        if (urlRCCache == null) {
            if (QueleaProperties.get().getUseRemoteControl() && QueleaApp.get().getRemoteControlServer()!= null) {
                String ip = getIP();
                if (ip != null) {
                    StringBuilder ret = new StringBuilder("http://");
                    ret.append(ip);
                    int port = QueleaProperties.get().getRemoteControlPort();
                    if (port != 80) {
                        ret.append(":");
                        ret.append(port);
                    }
                    urlRCCache = ret.toString();
                } else {
                    urlRCCache = "[Not started]";
                }
            } else {
                urlRCCache = "[Not started]";
            }
        }
        return urlRCCache;
    }

    private static String getIP() {
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex) {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ex2) {
                return null;
            }
        }
        while (interfaces.hasMoreElements()) {
            NetworkInterface current = interfaces.nextElement();
            try {
                if (!current.isUp() || current.isLoopback() || current.isVirtual() || current.getDisplayName().toLowerCase().contains("virtual")) {
                    continue;
                }
            } catch (SocketException ex) {
                continue;
            }
            Enumeration<InetAddress> addresses = current.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress current_addr = addresses.nextElement();
                if (current_addr.isLoopbackAddress()) {
                    continue;
                }
                return current_addr.getHostAddress();
            }
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            return null;
        }
    }

    public void resetChanged() {
        mlPrevChecked = useMobLyricsCheckBox.isSelected();
        mlPrevPortNum = mlPortNumTextField.getText();
        rcPrevChecked = useRemoteControlCheckBox.isSelected();
        rcPrevPortNum = rcPortNumTextField.getText();
        rcPrevPassword = rcPasswordTextField.getText();
    }

    /**
     * Determine if the user has changed any settings since resetChanged() was
     * called.
     */
    public boolean hasChanged() {
        return mlPrevChecked != useMobLyricsCheckBox.isSelected() || !(mlPrevPortNum.equals(mlPortNumTextField.getText())) || rcPrevChecked != useRemoteControlCheckBox.isSelected() || !(rcPrevPortNum.equals(rcPortNumTextField.getText()));
    }

    // If they have the same port, uncheck the remote control checkbox
    private void checkDifferent() {
        if(mlPortNumTextField.getText().equals(rcPortNumTextField.getText())) {
            useRemoteControlCheckBox.setSelected(false);
        }
    }

    /**
     * Set the properties based on the values in this frame.
     */
    @Override
    public void setProperties() {
        QueleaProperties.get().setUseMobLyrics(useMobLyricsCheckBox.isSelected());
        QueleaProperties.get().setUseRemoteControl(useRemoteControlCheckBox.isSelected());
        if (mlPortNumTextField.getText().trim().isEmpty()) {
            mlPortNumTextField.setText(Integer.toString(QueleaProperties.get().getMobLyricsPort()));
        } else {
            QueleaProperties.get().setMobLyricsPort(Integer.parseInt(mlPortNumTextField.getText()));
        }
        if (rcPortNumTextField.getText().trim().isEmpty()) {
            rcPortNumTextField.setText(Integer.toString(QueleaProperties.get().getRemoteControlPort()));
        } else {
            QueleaProperties.get().setRemoteControlPort(Integer.parseInt(rcPortNumTextField.getText()));
        }
        if (rcPasswordTextField.getText().trim().isEmpty()) {
            rcPasswordTextField.setText(QueleaProperties.get().getRemoteControlPassword());
        } else {
            QueleaProperties.get().setRemoteControlPassword(rcPasswordTextField.getText());
            if(!rcPasswordTextField.getText().equals(rcPrevPassword)) {
                RCHandler.logAllOut();
            }
        }
    }

    /**
     * Read the properties into this frame.
     */
    @Override
    public final void readProperties() {
        useMobLyricsCheckBox.setSelected(QueleaProperties.get().getUseMobLyrics());
        mlPortNumTextField.setText(Integer.toString(QueleaProperties.get().getMobLyricsPort()));
        useRemoteControlCheckBox.setSelected(QueleaProperties.get().getUseRemoteControl());
        rcPortNumTextField.setText(Integer.toString(QueleaProperties.get().getRemoteControlPort()));
        rcPasswordTextField.setText(QueleaProperties.get().getRemoteControlPassword());
    }

}