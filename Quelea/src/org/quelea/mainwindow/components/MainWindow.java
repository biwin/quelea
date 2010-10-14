package org.quelea.mainwindow.components;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * The main window used to control the projection.
 * @author Michael
 */
public class MainWindow extends JFrame {

    private MainToolbar toolbar;
    private MainMenuBar menubar;
    private MainPanel mainpanel;

    /**
     * Create a new main window.
     */
    public MainWindow() {
        super("Quelea V0.0 alpha");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            setIconImage(ImageIO.read(new File("img/logo.png")));
        }
        catch(IOException ex) {
        }
        setLayout(new BorderLayout());
        menubar = new MainMenuBar();
        toolbar = new MainToolbar();
        mainpanel = new MainPanel();
        setJMenuBar(menubar);
        add(toolbar, BorderLayout.NORTH);
        add(mainpanel);
        pack();
    }

    public MainPanel getMainPanel() {
        return mainpanel;
    }

    /**
     * Get the live lyrics list object in this main window. Listeners can be
     * added to this list and it can be queried for its current section.
     */
    public SelectLyricsList getLiveLyricsList() {
        return mainpanel.getLiveLyricsPanel().getLyricsList();
    }
}
