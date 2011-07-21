package org.quelea.windows.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import org.pushingpixels.substance.internal.ui.SubstanceSliderUI;
import org.quelea.utils.Utils;
import org.quelea.video.RemotePlayer;
import org.quelea.video.RemotePlayerFactory;

/**
 *
 * @author Michael
 */
public class VideoControlPanel extends JPanel {

    private JButton play;
    private JButton pause;
    private JButton stop;
    private JButton mute;
    private JSlider positionSlider;
    private Canvas videoArea;
    private List<RemotePlayer> mediaPlayers;
    private List<LyricCanvas> registeredCanvases;
    private ScheduledExecutorService executorService;
    private boolean pauseCheck;
    private String videoPath;

    public VideoControlPanel() {

        executorService = Executors.newSingleThreadScheduledExecutor();
        play = new JButton(Utils.getImageIcon("icons/play.png"));
        play.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                playVideo();
            }
        });
        pause = new JButton(Utils.getImageIcon("icons/pause.png"));
        pause.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pauseVideo();
            }
        });
        stop = new JButton(Utils.getImageIcon("icons/stop.png"));
        stop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stopVideo();
                positionSlider.setValue(0);
            }
        });
        mute = new JButton(Utils.getImageIcon("icons/mute.png"));
        mute.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setMute(!getMute());
            }
        });
        positionSlider = new JSlider(0, 1000);
        positionSlider.setValue(0);
        positionSlider.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                for (RemotePlayer mediaPlayer : mediaPlayers) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        pauseCheck = false;
                    }
                    else {
                        pauseCheck = true;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                for (RemotePlayer mediaPlayer : mediaPlayers) {
                    mediaPlayer.setTime((long) ((positionSlider.getValue() / (double) 1000) * mediaPlayer.getLength()));
                    if (!pauseCheck) {
                        mediaPlayer.play();
                    }
                }
            }
        });
        try {
            positionSlider.setUI(new SubstanceSliderUI(positionSlider) {

                @Override
                protected void scrollDueToClickInTrack(int direction) {
                    // this is the default behaviour, let's comment that out
                    //scrollByBlock(direction);

                    int value = positionSlider.getValue();

                    if (positionSlider.getOrientation() == JSlider.HORIZONTAL) {
                        value = this.valueForXPosition(positionSlider.getMousePosition().x);
                    }
                    else if (positionSlider.getOrientation() == JSlider.VERTICAL) {
                        value = this.valueForYPosition(positionSlider.getMousePosition().y);
                    }
                    positionSlider.setValue(value);
                }
            });
        }
        catch (Exception ex) {
            //UI issue, cannot do a lot and don't want to break program...
        }
        videoArea = new Canvas();
        videoArea.setBackground(Color.BLACK);
        videoArea.setMinimumSize(new Dimension(20, 20));
        videoArea.setPreferredSize(new Dimension(100, 100));
        setLayout(new BorderLayout());
        add(videoArea, BorderLayout.CENTER);
        registeredCanvases = new ArrayList<LyricCanvas>();

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
        sliderPanel.add(positionSlider);
        controlPanel.add(sliderPanel, BorderLayout.SOUTH);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(play);
        buttonPanel.add(pause);
        buttonPanel.add(stop);
        buttonPanel.add(mute);
        controlPanel.add(buttonPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.NORTH);
        mediaPlayers = new ArrayList<RemotePlayer>();
        videoArea.addHierarchyListener(new HierarchyListener() {

            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) > 0 && videoArea.isShowing()) {
                    RemotePlayer player = RemotePlayerFactory.getRemotePlayer(videoArea);
                    mediaPlayers.add(0, player);
                    if (videoPath != null) {
                        player.load(videoPath);
                    }
                    videoArea.removeHierarchyListener(this);
                }
            }
        });
    }

    public void registerCanvas(final LyricCanvas canvas) {
        registeredCanvases.add(canvas);
        if (!canvas.isShowing()) {
            canvas.addHierarchyListener(new HierarchyListener() {

                @Override
                public void hierarchyChanged(HierarchyEvent e) {
                    if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) > 0 && canvas.isShowing()) {
                        RemotePlayer player = RemotePlayerFactory.getRemotePlayer(canvas);
                        player.setMute(true);
                        mediaPlayers.add(player);
                        if (videoPath != null) {
                            player.load(videoPath);
                        }
                        canvas.removeHierarchyListener(this);
                    }
                }
            });
        }
        else {
            RemotePlayer player = RemotePlayerFactory.getRemotePlayer(canvas);
            player.setMute(true);
            mediaPlayers.add(player);
        }
    }

    public List<LyricCanvas> getRegisteredCanvases() {
        return registeredCanvases;
    }

    public void loadVideo(String videoPath) {
        this.videoPath = videoPath;
        for (RemotePlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.load(videoPath);
        }
    }

    public void playVideo() {
        for (int i = 0; i < mediaPlayers.size(); i++) {
            final RemotePlayer mediaPlayer = mediaPlayers.get(i);
            if(i>0) {
                mediaPlayer.setMute(true);
            }
            mediaPlayer.play();
            executorService.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    if (mediaPlayer.isPlaying()) {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                int timeVal = (int) ((mediaPlayer.getTime() / (double) mediaPlayer.getLength()) * 1000);
                                positionSlider.setValue(timeVal);
                            }
                        });
                    }
                }
            }, 0, 500, TimeUnit.MILLISECONDS);
        }
    }

    public long getTime() {
        return mediaPlayers.get(0).getTime();
    }

    public void setTime(long time) {
        mediaPlayers.get(0).setTime(time);
    }

    public void pauseVideo() {
        for (RemotePlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.pause();
        }
    }

    public void stopVideo() {
        for (RemotePlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.stop();
        }
    }

    public void setMute(boolean muteState) {
        mediaPlayers.get(0).setMute(muteState);
        if (getMute()) {
            mute.setIcon(Utils.getImageIcon("icons/unmute.png"));
        }
        else {
            mute.setIcon(Utils.getImageIcon("icons/mute.png"));
        }
    }

    public boolean getMute() {
        return mediaPlayers.get(0).getMute();
    }

    public void close() {
        for (RemotePlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.close();
        }
        executorService.shutdownNow();
    }

    @Override
    protected void finalize() throws Throwable {
        stopVideo();
        super.finalize();
        close();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        VideoControlPanel panel = new VideoControlPanel();
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        panel.loadVideo("F:\\Videos\\Inception\\Inception.mkv");
        panel.playVideo();
    }
}
