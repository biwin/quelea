package org.quelea.importexport;

import org.quelea.utils.FileFilters;
import org.quelea.utils.LoggerUtils;

import javax.swing.*;
import java.util.logging.Logger;

/**
 * An import dialog for the survivor song books in PDF format.
 * @author Michael
 */
public class SurvivorImportDialog extends ImportDialog {

    private static final Logger LOGGER = LoggerUtils.getLogger();

    /**
     * Create a new survivor import dialog.
     * @param owner the owner of the dialog.
     */
    public SurvivorImportDialog(JFrame owner) {
        super(owner, new String[]{
                "Select the location of the Survivor Songbook PDF below.",
                "<html>This must be the <b>acetates.pdf</b> file, <i>not</i> the guitar chords or the sheet music.</html>"
        }, FileFilters.SURVIVOR_SONGBOOK, new SurvivorSongbookParser(), false);
    }
}
