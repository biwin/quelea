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
package org.quelea.windows.multimedia;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Window;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.quelea.services.utils.LoggerUtils;
import org.quelea.services.utils.QueleaProperties;
import org.quelea.services.utils.Utils;
import org.quelea.windows.main.QueleaApp;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

/**
 * A native VLC window which is responsible for moving where it's told, and
 * playing video files. Transparent windows can then sit on top of this giving
 * the impression of a video background. This is a singleton since more than one
 * can cause native crashes - something we don't want to deal with (hence this
 * is hard-coded to just follow the projection window around.)
 * <p/>
 * @author Michael
 */
public class VLCWindowEmbed extends VLCWindow {

    /**
     * Use this thread for all VLC media player stuff to keep this class thread
     * safe.
     */
    private static final ExecutorService VLC_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Logger LOGGER = LoggerUtils.getLogger();
    public static final VLCWindowEmbed INSTANCE = QueleaProperties.get().getVLCAdvanced()?null:new VLCWindowEmbed();
    private Window window;
    private Canvas canvas;
    private MediaPlayerFactory mediaPlayerFactory;
    private EmbeddedMediaPlayer mediaPlayer;
    private boolean hideButton;
    private boolean show;
    private boolean paused;
    private volatile boolean init;
    private String location;
    private volatile double hue = 0;
    private FadeThread fadeThread;
    //temp variables
    private boolean muteTemp;
    private double progressTemp;
    private boolean isPlayingTemp;
    private boolean isPausedTemp;
    private int tempX, tempY, tempWidth, tempHeight;
    private boolean showing;

    /**
     * Creates a new VLC in-process window to play multimedia.
     */
    private VLCWindowEmbed() {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {
                try {
                    window = new Window(null);
                    window.setBackground(Color.BLACK);
                    canvas = new Canvas();
                    canvas.setBackground(Color.BLACK);
                    mediaPlayerFactory = new MediaPlayerFactory("--no-video-title-show");
                    mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
                    CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
                    mediaPlayer.setVideoSurface(videoSurface);
                    mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

                        @Override
                        public void finished(MediaPlayer mp) {
                            if (mediaPlayer.subItemCount() > 0) {
                                String mrl = mediaPlayer.subItems().remove(0);
                                mediaPlayer.playMedia(mrl);
                            }
                        }
                    });
                    window.add(canvas);
                    show = true;
                    window.setVisible(true);
                    window.toBack();
                    init = true;
                    LOGGER.log(Level.INFO, "Video initialised ok");
                } catch (Exception ex) {
                    LOGGER.log(Level.INFO, "Couldn't initialise video, almost definitely because VLC (or correct version of VLC) was not found.", ex);
                }
            }
        });
        ScheduledExecutorService exc = Executors.newSingleThreadScheduledExecutor();
        exc.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (init) {
                    runOnVLCThread(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer.setAdjustVideo(true);
                            mediaPlayer.setHue((int) (hue * 360));
                        }
                    });
                }
            }
        }, 0, 30, TimeUnit.MILLISECONDS);
    }

    /**
     * Determine if VLC has initialised correctly.
     * <p>
     * @return true if it has, false if it hasn't because something went wrong
     * (the most likely cause is an outdated version.)
     */
    @Override
    public boolean isInit() {
        runOnVLCThreadAndWait(new Runnable() {

            @Override
            public void run() {
                //Just to block until construction has finished!
            }
        });
        return init;
    }

    /**
     * Set the repeat of the current video playing.
     *
     * @param repeat True if repeat desired, false otherwise.
     */
    @Override
    public void setRepeat(final boolean repeat) {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    mediaPlayer.setRepeat(repeat);
                }

            }
        });
    }

    /**
     * Load the desired video into the video player
     *
     * @param path The path to the desired video
     */
    @Override
    public void load(final String path) {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {
                if (init) {
                    paused = false;
                    String sanitisedPath = path;
                    sanitisedPath = sanitisedPath.trim();
                    if (sanitisedPath.startsWith("www")) {
                        sanitisedPath = "http://" + sanitisedPath;
                    }
                    mediaPlayer.prepareMedia(sanitisedPath);
                }
            }
        });
    }

    /**
     * Play the already loaded video.
     */
    @Override
    public void play() {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    paused = false;
                    mediaPlayer.play();
                }

            }
        });
    }

    /**
     * Play the video passed into the method.
     *
     * @param vid The path to the desired video
     */
    @Override
    public void play(final String vid) {
        this.location = vid;
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    paused = false;
                    mediaPlayer.playMedia(vid);
                }

            }
        });
    }

    /**
     * Get the last played or loaded video.
     *
     * @return The path to the last played or loaded video.
     */
    @Override
    public String getLastLocation() {
        return location;
    }

    /**
     * Pause the currently playing video
     */
    @Override
    public void pause() {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    paused = true;
                    mediaPlayer.pause();
                }

            }
        });
    }

    /**
     * Stop the currently playing video
     * @param stopButton the value of stopButton
     */
    @Override
    public void stop(boolean stopButton) {
        location = null;
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    paused = false;
                    mediaPlayer.stop();
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            window.toBack();
                        }
                    });
                }

            }
        });
    }

    /**
     * Get whether the currently playing video is muted.
     *
     * @return True if muted, False otherwise
     */
    @Override
    public boolean isMute() {
        runOnVLCThreadAndWait(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    muteTemp = mediaPlayer.isMute();
                } else {
                    muteTemp = false;
                }

            }
        });
        return muteTemp;
    }

    /**
     * Mute the currently playing video.
     *
     * @param mute True if mute desired, otherwise false
     */
    @Override
    public void setMute(final boolean mute) {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    mediaPlayer.mute(mute);
                }

            }
        });
    }

    /**
     * Return the progress of the video as a percent.
     *
     * @return The percentage elapsed of the currently playing video.
     */
    @Override
    public double getProgressPercent() {
        runOnVLCThreadAndWait(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    progressTemp = (double) mediaPlayer.getTime() / mediaPlayer.getLength();
                } else {
                    progressTemp = 0;
                }

            }
        });
        return progressTemp;
    }

    /**
     * Set the position of the current video. The position is in percent
     * elapsed.
     *
     * @param percent The desired percentage elapsed.
     */
    @Override
    public void setProgressPercent(final double percent) {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    mediaPlayer.setPosition((float) percent);
                }

            }
        });
    }

    /**
     * Determine whether the video is currently playing
     *
     * @return True if playing, false otherwise
     */
    @Override
    public boolean isPlaying() {
        runOnVLCThreadAndWait(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    isPlayingTemp = mediaPlayer.isPlaying();
                } else {
                    isPlayingTemp = false;
                }

            }
        });
        return isPlayingTemp;
    }

    /**
     * Determine whether the video is currently paused
     *
     * @return True if paused, false otherwise
     */
    @Override
    public boolean isPaused() {
        runOnVLCThreadAndWait(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    isPausedTemp = paused;
                } else {
                    isPausedTemp = false;
                }

            }
        });
        return isPausedTemp;
    }

    /**
     * Set a runnable to be executed upon the completion of the currently
     * playing video
     *
     * @param onFinished The runnable that should be run upon completion of the
     * currently playing video.
     */
    @Override
    public void setOnFinished(final Runnable onFinished) {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    paused = false;
                    mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
                        @Override
                        public void finished(MediaPlayer mediaPlayer) {
                            if (mediaPlayer.subItemCount() == 0) {
                                onFinished.run();
                            }
                        }
                    });
                }

            }
        });
    }

    /**
     * Show the video player
     */
    @Override
    public void show() {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    show = true;
                    updateState();
                }

            }
        });
    }

    /**
     * Hide the video player
     */
    @Override
    public void hide() {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    show = false;
                    updateState();
                }

            }
        });
    }

    /**
     * Determine what happens when the Hide button was clicked
     *
     * @param hide Boolean representing whether the player should be hidden, or
     * shown
     */
    @Override
    public void setHideButton(final boolean hide) {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    hideButton = hide;
                    updateState();
                }

            }
        });
    }

    /**
     * Updates the state of the video playback window to reflect whether it
     * should be hidden or shown, etc.
     */
    private void updateState() {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            window.setOpacity((hideButton || !show) ? 0 : 1);
                            window.toBack();
                        }
                    });
                }

            }
        });
    }

    /**
     * Set the location of the video playback window
     *
     * @param x The x coordinate of the video playback window.
     * @param y The y coordinate of the video playback window.
     */
    @Override
    public void setLocation(final int x, final int y) {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            window.setLocation(x, y);
                        }
                    });
                }

            }
        });
    }

    /**
     * Set the size of the video playback window
     *
     * @param width The desired width of the video playback window.
     * @param height The desired height of the video playback window.
     */
    @Override
    public void setSize(final int width, final int height) {
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            window.setSize(width, height);
                        }
                    });
                }

            }
        });
    }

    /**
     * Refresh the position of the video playback window to be underneath the
     * JavaFX Quelea projection window
     */
    @Override
    public void refreshPosition() {
        Utils.fxRunAndWait(new Runnable() {
            @Override
            public void run() {
                showing = QueleaApp.get().getProjectionWindow().isShowing();
                if (showing) {
                    tempX = (int) QueleaApp.get().getProjectionWindow().getX();
                    tempY = (int) QueleaApp.get().getProjectionWindow().getY();
                    tempWidth = (int) QueleaApp.get().getProjectionWindow().getWidth();
                    tempHeight = (int) QueleaApp.get().getProjectionWindow().getHeight();
                }
            }
        });
        runOnVLCThread(new Runnable() {
            @Override
            public void run() {

                if (init) {
                    if (showing) {
                        show();
                        setLocation(tempX, tempY);
                        setSize(tempWidth, tempHeight);
                    } else {
                        hide();
                    }
                }

            }
        });
    }

    /**
     * Thread that will fade the hue
     */
    private class FadeThread extends Thread {

        private static final double INCREMENT = 0.002;
        private double toVal;
        private volatile boolean go = true;

        /**
         * Creates a new Fade thread.
         *
         * @param toVal To which value the Hue should be faded to.
         */
        public FadeThread(double toVal) {
            this.toVal = toVal;
        }

        /**
         * Fade the hue at the given increment.
         */
        @Override
        public void run() {
            double diff = toVal - getHue();
            if (diff < 0) {
                while (diff < 0 && go) {
                    setHue(getHue() - INCREMENT);
                    diff = toVal - getHue();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        //Meh
                    }
                }
            } else if (diff > 0) {
                while (diff > 0 && go) {
                    setHue(getHue() + INCREMENT);
                    diff = toVal - getHue();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        //Meh
                    }
                }
            }
            setHue(toVal);
        }

        /**
         * Stop fading
         */
        public void halt() {
            go = false;
        }

    }
     /**
     * Fade the hue of the video currently playing back
     *
     * @param hue The desired hue to fade to.
     */
    @Override
    public synchronized void fadeHue(final double hue) {
        if (fadeThread != null) {
            fadeThread.halt();
        }
        fadeThread = new FadeThread(hue);
        fadeThread.start();
    }
/**
     * Set the hue of the video currently playing back
     *
     * @param hue The desired hue to fade to.
     */
    @Override
    public void setHue(final double hue) {
        this.hue = hue;
    }
     /**
     * Get the current hue of the video playing back
     *
     * @return The hue of the video.
     */
    @Override
    public double getHue() {
        return hue;
    }

    /**
     * Run the specified runnable on the VLC thread. All VLC actions should be
     * executed on this thread to avoid threading issues.
     * <p/>
     * @param r the runnable to run.
     */
    private void runOnVLCThread(Runnable r) {
        VLC_EXECUTOR.submit(r);
    }

    /**
     * Run the specified runnable on the VLC thread and wait for it to complete.
     * All VLC actions should be executed on this thread to avoid threading
     * issues.
     * <p/>
     * @param r the runnable to run.
     */
    private void runOnVLCThreadAndWait(Runnable r) {
        try {
            VLC_EXECUTOR.submit(r).get();
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.WARNING, "Interrupted or execution error", ex);
        }
    }
}