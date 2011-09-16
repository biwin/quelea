/* 
 * This file is part of Quelea, free projection software for churches.
 * Copyright (C) 2011 Michael Berry
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
package org.quelea.windows.main;

import java.util.Set;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import org.quelea.Application;
import javax.swing.JDialog;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentListener;
import org.quelea.Theme;
import org.quelea.displayable.Displayable;
import org.quelea.displayable.TextDisplayable;
import org.quelea.displayable.TextSection;
import org.quelea.utils.FadeWindow;
import org.quelea.utils.LoggerUtils;
import org.quelea.utils.QueleaProperties;
import org.quelea.utils.Utils;

import org.quelea.windows.newsong.ThemePanel;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 *
 * @author Michael
 */
public class ScheduleThemePopupWindow extends FadeWindow {

    private static final Logger LOGGER = LoggerUtils.getLogger();
    private JPanel contentPanel;
    private Theme tempTheme;
    private ScheduleList schedule;

    public ScheduleThemePopupWindow(final ScheduleList schedule) {
        setSpeed(0.06f);
        this.schedule = schedule;
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(1, 1, 0, 0));
        contentPanel.setBorder(new LineBorder(Color.BLACK, 1));
        refresh();
        add(contentPanel);
        startWatching();
    }

    /**
     * Start the watcher thread.
     */
    private void startWatching() {
        try {
            final WatchService watcher = FileSystems.getDefault().newWatchService();
            final Path themePath = FileSystems.getDefault().getPath(new File(QueleaProperties.getQueleaUserHome(), "themes").getAbsolutePath());
            themePath.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            new Thread() {

                @SuppressWarnings("unchecked")
                public void run() {
                    while (true) {
                        WatchKey key;
                        try {
                            key = watcher.take();
                        }
                        catch (InterruptedException ex) {
                            return;
                        }

                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();
                            if (kind == OVERFLOW) {
                                continue;
                            }

                            WatchEvent<Path> ev = (WatchEvent<Path>) event;
                            Path filename = ev.context();
                            if (!filename.toFile().toString().toLowerCase().endsWith(".th")) {
                                continue;
                            }

                            if (!key.reset()) {
                                break;
                            }
                            Utils.sleep(200); //TODO: Bodge
                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {
                                    refresh();
                                }
                            });

                        }
                    }
                }
            }.start();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Theme getTempTheme() {
        return tempTheme;
    }

    public void updateTheme() {
        setTheme(tempTheme);
    }

    public synchronized final void refresh() {
        List<Theme> themes = null;
        try {
            themes = getThemes();
        }
        catch (Exception ex) {
            return;
        }
        themes.add(null);
        final ButtonGroup group = new ButtonGroup();
        Component[] components = contentPanel.getComponents();
        for (Component component : components) {
            contentPanel.remove(component);
        }
        contentPanel.validate();
        contentPanel.repaint();
        contentPanel.setLayout(new BorderLayout());
        final JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        northPanel.add(new JLabel("Select the theme to use for the service:"));
        contentPanel.add(northPanel, BorderLayout.NORTH);
        final JPanel themePreviews = new JPanel();
        themePreviews.setLayout(new GridLayout((themes.size()/5)+1, 5, 5, 5));
        for (final Theme theme : themes) {
            ThemePreviewPanel panel = new ThemePreviewPanel(theme);
            panel.getSelectButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    tempTheme = theme;
                    setTheme(theme);
                    Set<LyricCanvas> canvases = Application.get().getMainWindow().getMainPanel().getPreviewPanel().getCanvases();
                    for(LyricCanvas canvas : canvases) {
                        canvas.setTheme(theme);
                        canvas.repaint();
                    }
                }
            });
            group.add(panel.getSelectButton());
            themePreviews.add(panel);
        }
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton newThemeButton = new JButton("New...");
        newThemeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JDialog dialog = new JDialog(Application.get().getMainWindow(), "New theme", ModalityType.APPLICATION_MODAL);
                dialog.setLayout(new BorderLayout());
                JPanel northPanel = new JPanel();
                dialog.add(northPanel, BorderLayout.NORTH);
                northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                northPanel.add(new JLabel("Theme name"));
                final JTextField nameField = new JTextField(20);
                northPanel.add(nameField);
                final ThemePanel themePanel = new ThemePanel();
                themePanel.getCanvas().setPreferredSize(new Dimension(200, 200));
                dialog.add(themePanel, BorderLayout.CENTER);
                JPanel southPanel = new JPanel();
                dialog.add(southPanel, BorderLayout.SOUTH);
                southPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                final JButton addButton = new JButton("Add theme");
                addButton.setEnabled(false);
                nameField.getDocument().addDocumentListener(new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        check();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        check();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        check();
                    }
                    
                    private void check() {
                        addButton.setEnabled(!nameField.getText().trim().isEmpty());
                    }
                });
                addButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.setVisible(false);
                        Theme theme = themePanel.getTheme();
                        theme.setThemeName(nameField.getText());
                        String themeStr = theme.toDBString();
                        File file;
                        int filenum = 1;
                        do {
                            file = new File(new File(QueleaProperties.getQueleaUserHome(), "themes"), "theme" + filenum + ".th");
                            filenum++;
                        } while (file.exists());
                        try(PrintWriter pw = new PrintWriter(file)) {
                            pw.println(themeStr);
                        }
                        catch(IOException ex) {
                            LOGGER.log(Level.WARNING, "Couldn't write new theme", ex);
                        }

                    }
                });
                southPanel.add(addButton);
                dialog.pack();
                dialog.setLocationRelativeTo(dialog.getOwner());
                dialog.setVisible(true);
            }
        });
        buttonPanel.add(newThemeButton);
        contentPanel.add(themePreviews, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        contentPanel.validate();
        contentPanel.repaint();
    }

    private List<Theme> getThemes() {
        List<Theme> themesList = new ArrayList<>();
        File themeDir = new File(QueleaProperties.getQueleaUserHome(), "themes");
        if (!themeDir.exists()) {
            themeDir.mkdir();
        }
        for (File file : themeDir.listFiles()) {
            if (file.getName().endsWith(".th")) {
                final Theme theme = Theme.parseDBString(Utils.getTextFromFile(file.getAbsolutePath(), ""));
                if (theme.equals(Theme.DEFAULT_THEME)) {
                    LOGGER.log(Level.WARNING, "Error parsing theme file: {0}", file.getAbsolutePath());
                    continue;  //error
                }
                theme.setFile(file);
                themesList.add(theme);
            }
        }
        return themesList;
    }

    private void setTheme(Theme theme) {
        if (schedule == null) {
            LOGGER.log(Level.WARNING, "Null schedule, not setting theme");
            return;
        }
        for (int i = 0; i < schedule.getModel().getSize(); i++) {
            Displayable displayable = schedule.getModel().get(i);
            if (displayable instanceof TextDisplayable) {
                TextDisplayable textDisplayable = (TextDisplayable) displayable;
                for (TextSection section : textDisplayable.getSections()) {
                    section.setTempTheme(theme);
                }
            }
        }
    }

    public static void main(String[] args) {
        JWindow window = new ScheduleThemePopupWindow(null);
        window.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        window.pack();
        window.setVisible(true);
    }
}
