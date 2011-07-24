package org.quelea.windows.main.ribbon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.quelea.Application;
import org.quelea.Schedule;
import org.quelea.displayable.Song;
import org.quelea.displayable.VideoDisplayable;
import org.quelea.mail.MailDialog;
import org.quelea.video.VideoFileFilter;
import org.quelea.windows.library.LibrarySongList;
import org.quelea.windows.main.AddSongActionListener;
import org.quelea.windows.main.EditSongScheduleActionListener;
import org.quelea.windows.main.MainPanel;
import org.quelea.windows.main.RemoveSongScheduleActionListener;
import org.quelea.windows.main.ScheduleList;

/**
 *
 * @author Michael
 */
public class ScheduleTask extends RibbonTask {

    private static MailDialog mailDialog = new MailDialog();

    public ScheduleTask() {
        super("Schedule", createSongBand(), createVideoBand(), createShareBand(), createNoticeBand());
    }

    private static void checkEditRemoveButtons(JCommandButton editSongButton, JCommandButton removeSongButton) {
        final MainPanel mainPanel = Application.get().getMainWindow().getMainPanel();
        final ScheduleList scheduleList = mainPanel.getSchedulePanel().getScheduleList();
        if (!scheduleList.isFocusOwner()) {
            editSongButton.setEnabled(false);
            removeSongButton.setEnabled(false);
            return;
        }
        if (scheduleList.getSelectedIndex() == -1) {
            editSongButton.setEnabled(false);
            removeSongButton.setEnabled(false);
        } else {
            if (scheduleList.getSelectedValue() instanceof Song) {
                editSongButton.setEnabled(true);
            } else {
                editSongButton.setEnabled(false);
            }
            removeSongButton.setEnabled(true);
        }
    }

    private static void checkAddButton(JCommandButton addSongButton) {
        final MainPanel mainPanel = Application.get().getMainWindow().getMainPanel();
        final LibrarySongList songList = mainPanel.getLibraryPanel().getLibrarySongPanel().getSongList();
        if (!songList.isFocusOwner()) {
            addSongButton.setEnabled(false);
            return;
        }
        if (songList.getSelectedIndex() == -1) {
            addSongButton.setEnabled(false);
        } else {
            addSongButton.setEnabled(true);
        }
    }

    private static JRibbonBand createSongBand() {
        JRibbonBand songBand = new JRibbonBand("Items", RibbonUtils.getRibbonIcon("icons/schedule.png", 100, 100));
        RibbonUtils.applyStandardResizePolicies(songBand);

        final JCommandButton addSongButton = new JCommandButton("Add song", RibbonUtils.getRibbonIcon("icons/newsong.png", 100, 100));
        addSongButton.addActionListener(new AddSongActionListener());
        songBand.addCommandButton(addSongButton, RibbonElementPriority.TOP);
        addSongButton.setEnabled(false);
        final JCommandButton editSongButton = new JCommandButton("Edit song", RibbonUtils.getRibbonIcon("icons/edit.png", 100, 100));
        editSongButton.addActionListener(new EditSongScheduleActionListener());
        songBand.addCommandButton(editSongButton, RibbonElementPriority.MEDIUM);
        editSongButton.setEnabled(false);
        final JCommandButton removeSongButton = new JCommandButton("Remove item", RibbonUtils.getRibbonIcon("icons/remove 2.png", 100, 100));
        removeSongButton.addActionListener(new RemoveSongScheduleActionListener());
        songBand.addCommandButton(removeSongButton, RibbonElementPriority.MEDIUM);
        removeSongButton.setEnabled(false);

        final MainPanel mainPanel = Application.get().getMainWindow().getMainPanel();
        final ScheduleList scheduleList = mainPanel.getSchedulePanel().getScheduleList();
        scheduleList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                checkEditRemoveButtons(editSongButton, removeSongButton);
            }
        });
        scheduleList.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                checkEditRemoveButtons(editSongButton, removeSongButton);
            }

            @Override
            public void focusLost(FocusEvent e) {
                checkEditRemoveButtons(editSongButton, removeSongButton);
            }
        });
        final LibrarySongList songList = mainPanel.getLibraryPanel().getLibrarySongPanel().getSongList();
        songList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                checkAddButton(addSongButton);
            }
        });
        songList.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                checkAddButton(addSongButton);
            }

            @Override
            public void focusLost(FocusEvent e) {
                checkAddButton(addSongButton);
            }
        });

        return songBand;
    }

    private static JRibbonBand createVideoBand() {
        JRibbonBand videoBand = new JRibbonBand("Add Multimedia", RibbonUtils.getRibbonIcon("icons/video file.png", 100, 100));
        RibbonUtils.applyStandardResizePolicies(videoBand);
        JCommandButton presentationButton = new JCommandButton("Presentation", RibbonUtils.getRibbonIcon("icons/powerpoint.png", 100, 100));
        videoBand.addCommandButton(presentationButton, RibbonElementPriority.TOP);
        presentationButton.setEnabled(false);
        JCommandButton videoFileButton = new JCommandButton("Video File", RibbonUtils.getRibbonIcon("icons/video file.png", 100, 100));
        videoFileButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new VideoFileFilter());
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.showOpenDialog(Application.get().getMainWindow());
                File file = fileChooser.getSelectedFile();
                if (file != null) {
                    VideoDisplayable displayable = new VideoDisplayable(fileChooser.getSelectedFile());
                    ((DefaultListModel) Application.get().getMainWindow().getMainPanel().getSchedulePanel().getScheduleList().getModel()).addElement(displayable);
                }
            }
        });
        videoBand.addCommandButton(videoFileButton, RibbonElementPriority.TOP);
        JCommandButton youtubeButton = new JCommandButton("Youtube", RibbonUtils.getRibbonIcon("icons/youtube.png", 100, 100));
        videoBand.addCommandButton(youtubeButton, RibbonElementPriority.MEDIUM);
        youtubeButton.setEnabled(false);
        JCommandButton liveButton = new JCommandButton("Live Video", RibbonUtils.getRibbonIcon("icons/live.png", 100, 100));
        videoBand.addCommandButton(liveButton, RibbonElementPriority.MEDIUM);
        liveButton.setEnabled(false);
        JCommandButton dvdButton = new JCommandButton("DVD", RibbonUtils.getRibbonIcon("icons/dvd.png", 100, 100));
        videoBand.addCommandButton(dvdButton, RibbonElementPriority.MEDIUM);
        dvdButton.setEnabled(false);
        JCommandButton audioButton = new JCommandButton("Audio", RibbonUtils.getRibbonIcon("icons/audio.png", 100, 100));
        videoBand.addCommandButton(audioButton, RibbonElementPriority.MEDIUM);
        audioButton.setEnabled(false);
        return videoBand;
    }

    private static JRibbonBand createNoticeBand() {
        JRibbonBand noticeBand = new JRibbonBand("Notices", RibbonUtils.getRibbonIcon("icons/info.png", 100, 100));
        RibbonUtils.applyStandardResizePolicies(noticeBand);
        JCommandButton alertButton = new JCommandButton("Manage notices", RibbonUtils.getRibbonIcon("icons/info.png", 100, 100));
        alertButton.setEnabled(false);
        noticeBand.addCommandButton(alertButton, RibbonElementPriority.TOP);
        return noticeBand;
    }

    private static JRibbonBand createShareBand() {
        JRibbonBand shareBand = new JRibbonBand("Share", RibbonUtils.getRibbonIcon("icons/share.png", 100, 100));
        RibbonUtils.applyStandardResizePolicies(shareBand);
        final JCommandButton emailButton = new JCommandButton("Email", RibbonUtils.getRibbonIcon("icons/email.png", 100, 100));
        shareBand.addCommandButton(emailButton, RibbonElementPriority.TOP);
        emailButton.setEnabled(false);
        final ScheduleList scheduleList = Application.get().getMainWindow().getMainPanel().getSchedulePanel().getScheduleList();
        scheduleList.getModel().addListDataListener(new ListDataListener() {

            @Override
            public void intervalAdded(ListDataEvent e) {
                check();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                check();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                check();
            }

            private void check() {
                Schedule schedule = scheduleList.getSchedule();
                if (schedule == null || !schedule.iterator().hasNext()) {
                    emailButton.setEnabled(false);
                } else {
                    emailButton.setEnabled(true);
                }
            }
        });
        emailButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mailDialog.reset();
                mailDialog.setSchedule(scheduleList.getSchedule());
                mailDialog.setVisible(true);
            }
        });
        return shareBand;
    }
}
