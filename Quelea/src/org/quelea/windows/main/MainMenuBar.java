package org.quelea.windows.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.quelea.windows.help.AboutDialog;

/**
 * The main menu bar that's displayed at the top of the main Quelea window.
 * @author Michael
 */
public class MainMenuBar extends JMenuBar {

    private JMenuItem newSchedule;
    private JMenuItem openSchedule;
    private JMenuItem saveSchedule;
    private JMenuItem saveScheduleAs;
    private JMenuItem options;
    private JMenuItem about;

    /**
     * Create a new main menu bar and initialise all the components.
     */
    public MainMenuBar() {
        addFileMenu();
        addToolsMenu();
        addHelpMenu();
    }

    /**
     * Get the new schedule menu item.
     * @return the new schedule menu item.
     */
    public JMenuItem getNewSchedule() {
        return newSchedule;
    }

    /**
     * Get the open schedule menu item.
     * @return the open schedule menu item.
     */
    public JMenuItem getOpenSchedule() {
        return openSchedule;
    }

    /**
     * Get the save schedule menu item.
     * @return the save schedule menu item.
     */
    public JMenuItem getSaveSchedule() {
        return saveSchedule;
    }

    /**
     * Get the save as schedule menu item.
     * @return the save as schedule menu item.
     */
    public JMenuItem getSaveScheduleAs() {
        return saveScheduleAs;
    }

    /**
     * Get the options menu item.
     * @return the options menu item.
     */
    public JMenuItem getOptions() {
        return options;
    }

    /**
     * Get the about menu item.
     * @return the about menu item.
     */
    public JMenuItem getAbout() {
        return about;
    }

    /**
     * Add the file menu to the menu bar.
     */
    private void addFileMenu() {
        JMenu fileMenu = new JMenu("File");
        newSchedule = new JMenuItem("New");
        fileMenu.add(newSchedule);
        openSchedule = new JMenuItem("Open");
        fileMenu.add(openSchedule);
        saveSchedule = new JMenuItem("Save");
        fileMenu.add(saveSchedule);
        saveScheduleAs = new JMenuItem("Save as...");
        fileMenu.add(saveScheduleAs);
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exit);
        add(fileMenu);
    }

    /**
     * Add the tools menu to the menu bar.
     */
    private void addToolsMenu() {
        JMenu toolsMenu = new JMenu("Tools");
        options = new JMenuItem("Options");
        toolsMenu.add(options);
        add(toolsMenu);
    }

    /**
     * Add the help menu to the menu bar.
     */
    private void addHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        about = new JMenuItem("About...");
        helpMenu.add(about);
        add(helpMenu);
    }

}
