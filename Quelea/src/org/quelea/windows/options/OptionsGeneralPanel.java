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
 * MERCHANTABILITYs or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.quelea.windows.options;

import java.io.File;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import org.quelea.data.powerpoint.OOPresentation;
import org.quelea.data.powerpoint.OOUtils;
import org.quelea.services.languages.LabelGrabber;
import org.quelea.services.languages.LanguageFile;
import org.quelea.services.languages.LanguageFileManager;
import org.quelea.services.utils.PropertyPanel;
import org.quelea.services.utils.QueleaProperties;
import org.quelea.windows.main.QueleaApp;

/**
 * A panel where the general options in the program are set.
 * <p/>
 * @author Michael
 */
public class OptionsGeneralPanel extends GridPane implements PropertyPanel {

    private final CheckBox startupUpdateCheckBox;
    private final CheckBox capitalFirstCheckBox;
    private final CheckBox oneMonitorWarnCheckBox;
    private final CheckBox displaySongInfoCheckBox;
    private final CheckBox oneLineModeCheckBox;
    private final CheckBox textShadowCheckBox;
    private final CheckBox clearLiveOnRemoveCheckBox;
    private final CheckBox useOOCheckBox;
    private final CheckBox uniformFontSizeCheckBox;
    private final ComboBox<TextPosition> textPositionComboBox;
    private final ComboBox<LanguageFile> languageFileComboBox;
    private final TextField ooPathTextField;
    private final DirectoryChooser ooChooser;
    private final Button selectButton;
    private final Slider borderThicknessSlider;
    private final Slider maximumFontSizeSlider;
    private final Slider additionalLineSpacingSlider;
    private final Slider maxCharsSlider;
//    private final Slider minLinesSlider;
    private LanguageFile currentLanguageFile;

    /**
     * Create a new general panel.
     */
    public OptionsGeneralPanel() {
        int rows = 0;
        setVgap(5);

        Label startupLabel = new Label(LabelGrabber.INSTANCE.getLabel("check.for.update.label"));
        GridPane.setConstraints(startupLabel, 1, rows);
        getChildren().add(startupLabel);
        startupUpdateCheckBox = new CheckBox();
        startupLabel.setLabelFor(startupUpdateCheckBox);
        GridPane.setConstraints(startupUpdateCheckBox, 2, rows);
        getChildren().add(startupUpdateCheckBox);
        rows++;

        Label useOOLabel = new Label(LabelGrabber.INSTANCE.getLabel("use.oo.label"));
        GridPane.setConstraints(useOOLabel, 1, rows);
        getChildren().add(useOOLabel);
        useOOCheckBox = new CheckBox();
        useOOCheckBox.selectedProperty().addListener(new javafx.beans.value.ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if(useOOCheckBox.isSelected()) {
                    ooPathTextField.setDisable(true);
                    selectButton.setDisable(false);
                }
                else {
                    ooPathTextField.setDisable(true);
                    selectButton.setDisable(true);
                }
            }
        });
        useOOLabel.setLabelFor(useOOCheckBox);
        GridPane.setConstraints(useOOCheckBox, 2, rows);
        getChildren().add(useOOCheckBox);
        rows++;

        Label ooPathLabel = new Label(LabelGrabber.INSTANCE.getLabel("oo.path"));
        GridPane.setConstraints(ooPathLabel, 1, rows);
        getChildren().add(ooPathLabel);
        ooPathTextField = new TextField();
        ooPathTextField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(ooPathTextField, Priority.ALWAYS);
        ooPathTextField.setEditable(false);
        ooPathLabel.setLabelFor(ooPathTextField);
        GridPane.setConstraints(ooPathTextField, 2, rows);
        getChildren().add(ooPathTextField);
        ooChooser = new DirectoryChooser();
        selectButton = new Button(LabelGrabber.INSTANCE.getLabel("browse"));
        selectButton.setDisable(true);
        selectButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent t) {
                File dir = ooChooser.showDialog(QueleaApp.get().getMainWindow());
                if(dir != null) {
                    ooPathTextField.setText(dir.getAbsolutePath());
                }
            }
        });
        GridPane.setConstraints(selectButton, 3, rows);
        getChildren().add(selectButton);
        rows++;

        Label warnLabel = new Label(LabelGrabber.INSTANCE.getLabel("1.monitor.warn.label"));
        GridPane.setConstraints(warnLabel, 1, rows);
        getChildren().add(warnLabel);
        oneMonitorWarnCheckBox = new CheckBox();
        warnLabel.setLabelFor(oneMonitorWarnCheckBox);
        GridPane.setConstraints(oneMonitorWarnCheckBox, 2, rows);
        getChildren().add(oneMonitorWarnCheckBox);
        rows++;

        Label capitalFirstLabel = new Label(LabelGrabber.INSTANCE.getLabel("capitalise.start.line.label"));
        GridPane.setConstraints(capitalFirstLabel, 1, rows);
        getChildren().add(capitalFirstLabel);
        capitalFirstCheckBox = new CheckBox();
        startupLabel.setLabelFor(capitalFirstCheckBox);
        GridPane.setConstraints(capitalFirstCheckBox, 2, rows);
        getChildren().add(capitalFirstCheckBox);
        rows++;

        Label displaySongInfoLabel = new Label(LabelGrabber.INSTANCE.getLabel("display.song.info.label"));
        GridPane.setConstraints(displaySongInfoLabel, 1, rows);
        getChildren().add(displaySongInfoLabel);
        displaySongInfoCheckBox = new CheckBox();
        startupLabel.setLabelFor(displaySongInfoCheckBox);
        GridPane.setConstraints(displaySongInfoCheckBox, 2, rows);
        getChildren().add(displaySongInfoCheckBox);
        rows++;

        Label uniformFontSizeLabel = new Label(LabelGrabber.INSTANCE.getLabel("uniform.font.size.label"));
        GridPane.setConstraints(uniformFontSizeLabel, 1, rows);
        getChildren().add(uniformFontSizeLabel);
        uniformFontSizeCheckBox = new CheckBox();
        startupLabel.setLabelFor(uniformFontSizeCheckBox);
        GridPane.setConstraints(uniformFontSizeCheckBox, 2, rows);
        getChildren().add(uniformFontSizeCheckBox);
        rows++;

        Label oneLineModeLabel = new Label(LabelGrabber.INSTANCE.getLabel("one.line.mode.label"));
        GridPane.setConstraints(oneLineModeLabel, 1, rows);
        getChildren().add(oneLineModeLabel);
        oneLineModeCheckBox = new CheckBox();
        startupLabel.setLabelFor(oneLineModeCheckBox);
        GridPane.setConstraints(oneLineModeCheckBox, 2, rows);
        getChildren().add(oneLineModeCheckBox);
        rows++;

        Label textShadowLabel = new Label(LabelGrabber.INSTANCE.getLabel("text.shadow.label"));
        GridPane.setConstraints(textShadowLabel, 1, rows);
        getChildren().add(textShadowLabel);
        textShadowCheckBox = new CheckBox();
        startupLabel.setLabelFor(textShadowCheckBox);
        GridPane.setConstraints(textShadowCheckBox, 2, rows);
        getChildren().add(textShadowCheckBox);
        rows++;

        Label clearLiveOnRemoveLabel = new Label(LabelGrabber.INSTANCE.getLabel("clear.live.on.remove.schedule") + " ");
        GridPane.setConstraints(clearLiveOnRemoveLabel, 1, rows);
        getChildren().add(clearLiveOnRemoveLabel);
        clearLiveOnRemoveCheckBox = new CheckBox();
        startupLabel.setLabelFor(clearLiveOnRemoveCheckBox);
        GridPane.setConstraints(clearLiveOnRemoveCheckBox, 2, rows);
        getChildren().add(clearLiveOnRemoveCheckBox);
        rows++;
        
        Label textPositionLabel = new Label(LabelGrabber.INSTANCE.getLabel("text.position.label"));
        GridPane.setConstraints(textPositionLabel, 1, rows);
        getChildren().add(textPositionLabel);
        textPositionComboBox = new ComboBox<>();
        for(TextPosition position : TextPosition.values()) {
            textPositionComboBox.getItems().add(position);
        }
        startupLabel.setLabelFor(textPositionComboBox);
        GridPane.setConstraints(textPositionComboBox, 2, rows);
        getChildren().add(textPositionComboBox);
        rows++;

        Label interfaceLanguageLabel = new Label(LabelGrabber.INSTANCE.getLabel("interface.language.label"));
        GridPane.setConstraints(interfaceLanguageLabel, 1, rows);
        getChildren().add(interfaceLanguageLabel);
        languageFileComboBox = new ComboBox<>();
        for(LanguageFile file : LanguageFileManager.INSTANCE.languageFiles()) {
            languageFileComboBox.getItems().add(file);
        }
        interfaceLanguageLabel.setLabelFor(languageFileComboBox);
        GridPane.setConstraints(languageFileComboBox, 2, rows);
        getChildren().add(languageFileComboBox);
        rows++;
        
        Label borderThicknessLabel = new Label(LabelGrabber.INSTANCE.getLabel("text.border.thickness.label"));
        GridPane.setConstraints(borderThicknessLabel, 1, rows);
        getChildren().add(borderThicknessLabel);
        borderThicknessSlider = new Slider(0, 5, 0);
        GridPane.setConstraints(borderThicknessSlider, 2, rows);
        getChildren().add(borderThicknessSlider);
        borderThicknessLabel.setLabelFor(borderThicknessSlider);
        final Label borderThicknessValue = new Label(Integer.toString((int) borderThicknessSlider.getValue()));
        GridPane.setConstraints(borderThicknessValue, 3, rows);
        getChildren().add(borderThicknessValue);
        borderThicknessValue.setLabelFor(borderThicknessSlider);
        borderThicknessSlider.valueProperty().addListener(new javafx.beans.value.ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                borderThicknessValue.setText(Integer.toString((int) borderThicknessSlider.getValue()));
            }
        });
        rows++;

        Label maxFontSizeLabel = new Label(LabelGrabber.INSTANCE.getLabel("max.font.size.label"));
        GridPane.setConstraints(maxFontSizeLabel, 1, rows);
        getChildren().add(maxFontSizeLabel);
        maximumFontSizeSlider = new Slider(12, 1000, 1000);
        GridPane.setConstraints(maximumFontSizeSlider, 2, rows);
        getChildren().add(maximumFontSizeSlider);
        maxFontSizeLabel.setLabelFor(maximumFontSizeSlider);
        final Label maxFontSizeValue = new Label(Integer.toString((int) maximumFontSizeSlider.getValue()));
        GridPane.setConstraints(maxFontSizeValue, 3, rows);
        getChildren().add(maxFontSizeValue);
        maxFontSizeValue.setLabelFor(maximumFontSizeSlider);
        maximumFontSizeSlider.valueProperty().addListener(new javafx.beans.value.ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                maxFontSizeValue.setText(Integer.toString((int) maximumFontSizeSlider.getValue()));
            }
        });
        rows++;

        Label additionalLineSpacingLabel = new Label(LabelGrabber.INSTANCE.getLabel("additional.line.spacing.label"));
        GridPane.setConstraints(additionalLineSpacingLabel, 1, rows);
        getChildren().add(additionalLineSpacingLabel);
        additionalLineSpacingSlider = new Slider(0, 50, 10);
        GridPane.setConstraints(additionalLineSpacingSlider, 2, rows);
        getChildren().add(additionalLineSpacingSlider);
        maxFontSizeLabel.setLabelFor(additionalLineSpacingSlider);
        final Label additionalLineSpacingValue = new Label(Integer.toString((int) additionalLineSpacingSlider.getValue()));
        GridPane.setConstraints(additionalLineSpacingValue, 3, rows);
        getChildren().add(additionalLineSpacingValue);
        additionalLineSpacingValue.setLabelFor(additionalLineSpacingSlider);
        additionalLineSpacingSlider.valueProperty().addListener(new javafx.beans.value.ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                additionalLineSpacingValue.setText(Integer.toString((int) additionalLineSpacingSlider.getValue()));
            }
        });
        rows++;

        textShadowCheckBox.selectedProperty().addListener(new javafx.beans.value.ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if(textShadowCheckBox.isSelected()) {
                    borderThicknessValue.setDisable(true);
                    borderThicknessSlider.setDisable(true);
                }
                else {
                    borderThicknessValue.setDisable(false);
                    borderThicknessSlider.setDisable(false);
                }
            }
        });

        Label maxCharsLabel = new Label(LabelGrabber.INSTANCE.getLabel("max.chars.line.label"));
        GridPane.setConstraints(maxCharsLabel, 1, rows);
        getChildren().add(maxCharsLabel);
        maxCharsSlider = new Slider(10, 80, 0);
        GridPane.setConstraints(maxCharsSlider, 2, rows);
        getChildren().add(maxCharsSlider);
        maxCharsLabel.setLabelFor(maxCharsSlider);
        final Label maxCharsValue = new Label(Integer.toString((int) maxCharsSlider.getValue()));
        GridPane.setConstraints(maxCharsValue, 3, rows);
        getChildren().add(maxCharsValue);
        maxCharsValue.setLabelFor(maxCharsSlider);
        maxCharsSlider.valueProperty().addListener(new javafx.beans.value.ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                maxCharsValue.setText(Integer.toString((int) maxCharsSlider.getValue()));
            }
        });
        rows++;

//        Label minLinesLabel = new Label(LabelGrabber.INSTANCE.getLabel("min.emulated.lines.label") + " (" + LabelGrabber.INSTANCE.getLabel("advanced.label") + ")");
//        GridPane.setConstraints(minLinesLabel, 1, rows);
//        getChildren().add(minLinesLabel);
//        minLinesSlider = new Slider(1, 20, 0);
//        GridPane.setConstraints(minLinesSlider, 2, rows);
//        getChildren().add(minLinesSlider);
//        minLinesLabel.setLabelFor(minLinesSlider);
//        final Label minLinesValue = new Label(Integer.toString((int) minLinesSlider.getValue()));
//        GridPane.setConstraints(minLinesValue, 3, rows);
//        getChildren().add(minLinesValue);
//        minLinesValue.setLabelFor(minLinesSlider);
//        minLinesSlider.valueProperty().addListener(new javafx.beans.value.ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
//                minLinesValue.setText(Integer.toString((int) minLinesSlider.getValue()));
//            }
//        });
//        rows++;

        readProperties();
    }
    
    /**
     * Reset the mechanism for determining if the user has changed the interface language. Call before showing the options dialog.
     */
    public void resetLanguageChanged() {
        currentLanguageFile = languageFileComboBox.getValue();
    }
    
    /**
     * Determine if the user has changed the interface language since the last call of resetLanguageChanged().
     */
    public boolean hasLanguageChanged() {
        return !languageFileComboBox.getValue().equals(currentLanguageFile);
    }

    /**
     * @inheritDoc
     */
    @Override
    public final void readProperties() {
        QueleaProperties props = QueleaProperties.get();
        languageFileComboBox.setValue(LanguageFileManager.INSTANCE.getCurrentFile());
        startupUpdateCheckBox.setSelected(props.checkUpdate());
        useOOCheckBox.setSelected(props.getUseOO());
        ooPathTextField.setText(props.getOOPath());
        capitalFirstCheckBox.setSelected(props.checkCapitalFirst());
        oneMonitorWarnCheckBox.setSelected(props.showSingleMonitorWarning());
        displaySongInfoCheckBox.setSelected(props.checkDisplaySongInfoText());
        uniformFontSizeCheckBox.setSelected(props.getUseUniformFontSize());
        oneLineModeCheckBox.setSelected(props.getOneLineMode());
        textShadowCheckBox.setSelected(props.getTextShadow());
        clearLiveOnRemoveCheckBox.setSelected(props.getClearLiveOnRemove());
        maxCharsSlider.setValue(props.getMaxChars());
//        minLinesSlider.setValue(props.getMinLines());
        borderThicknessSlider.setValue(props.getOutlineThickness());
        textPositionComboBox.setValue(props.getTextPositionInternal());
        additionalLineSpacingSlider.setValue(props.getAdditionalLineSpacing());
        maximumFontSizeSlider.setValue(props.getMaxFontSize());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setProperties() {
        QueleaProperties props = QueleaProperties.get();
        props.setLanguageFile(languageFileComboBox.getValue().getFile().getName());
        boolean checkUpdate = getStartupUpdateCheckBox().isSelected();
        props.setCheckUpdate(checkUpdate);
        boolean useOO = getUseOOCheckBox().isSelected();
        props.setUseOO(useOO);
        String ooPath = getOOPathTextField().getText();
        props.setOOPath(ooPath);
        boolean showWarning = getOneMonitorWarningCheckBox().isSelected();
        props.setSingleMonitorWarning(showWarning);
        boolean checkCapital = getCapitalFirstCheckBox().isSelected();
        props.setCapitalFirst(checkCapital);
        boolean checkDisplayInfo = getDisplaySongInfoCheckBox().isSelected();
        props.setDisplaySongInfoText(checkDisplayInfo);
        boolean useUniformFontSize = uniformFontSizeCheckBox.isSelected();
        props.setUseUniformFontSize(useUniformFontSize);
        boolean textShadow = getTextShadowCheckBox().isSelected();
        props.setTextShadow(textShadow);
        boolean clearLive = clearLiveOnRemoveCheckBox.isSelected();
        props.setClearLiveOnRemove(clearLive);
        boolean oneLineMode = getOneLineModeCheckBox().isSelected();
        props.setOneLineMode(oneLineMode);
        //One line mode needs to be updated manually
        QueleaApp.get().getMainWindow().getMainPanel().getPreviewPanel().updateOneLineMode();
        QueleaApp.get().getMainWindow().getMainPanel().getLivePanel().updateOneLineMode();
        int maxCharsPerLine = (int) getMaxCharsSlider().getValue();
        props.setMaxChars(maxCharsPerLine);
//        int minLines = (int) getMinLinesSlider().getValue();
//        props.setMinLines(minLines);
        int borderThickness = (int) getBorderThicknessSlider().getValue();
        props.setOutlineThickness(borderThickness);
        props.setTextPositionInternal(textPositionComboBox.getValue());
        props.setMaxFontSize(maximumFontSizeSlider.getValue());
        props.setAdditionalLineSpacing(additionalLineSpacingSlider.getValue());
        //Initialise presentation
        if(!OOPresentation.isInit()) {
            OOUtils.attemptInit();
        }
    }

    /**
     * Get the max chars slider.
     * <p/>
     * @return the max chars slider.
     */
    public Slider getMaxCharsSlider() {
        return maxCharsSlider;
    }

//    /**
//     * Get the min lines slider.
//     * <p/>
//     * @return the min lines slider.
//     */
//    public Slider getMinLinesSlider() {
//        return minLinesSlider;
//    }

    /**
     * Get the startup readProperties checkbox.
     * <p/>
     * @return the startup readProperties checkbox.
     */
    public CheckBox getStartupUpdateCheckBox() {
        return startupUpdateCheckBox;
    }

    /**
     * Get the capitalise first character in each line checkbox.
     * <p/>
     * @return the capitalise first character in each line checkbox.
     */
    public CheckBox getCapitalFirstCheckBox() {
        return capitalFirstCheckBox;
    }

    /**
     * Get the display song info checkbox.
     * <p/>
     * @return the display song info checkbox.
     */
    public CheckBox getDisplaySongInfoCheckBox() {
        return displaySongInfoCheckBox;
    }

    /**
     * Get the "one monitor warning" checkbox.
     * <p/>
     * @return the "one monitor warning" checkbox.
     */
    public CheckBox getOneMonitorWarningCheckBox() {
        return oneMonitorWarnCheckBox;
    }

    /**
     * Get the "one line mode" checkbox.
     * <p/>
     * @return the "one line mode" checkbox.
     */
    public CheckBox getOneLineModeCheckBox() {
        return oneLineModeCheckBox;
    }

    /**
     * Get the "use openoffice" checkbox.
     * <p/>
     * @return the "use openoffice" checkbox.
     */
    public CheckBox getUseOOCheckBox() {
        return useOOCheckBox;
    }

    /**
     * Get the "openoffice path" text field.
     * <p/>
     * @return the "openoffice path" text field.
     */
    public TextField getOOPathTextField() {
        return ooPathTextField;
    }

    /**
     * Get the "text shadow" checkbox.
     * <p/>
     * @return the "text.shadow" checkbox.
     */
    public CheckBox getTextShadowCheckBox() {
        return textShadowCheckBox;
    }

    /**
     * Get the border thickness slider.
     * <p/>
     * @return the border thickness slider.
     */
    public Slider getBorderThicknessSlider() {
        return borderThicknessSlider;
    }
}
