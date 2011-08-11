package org.quelea.windows.library;

import org.quelea.displayable.ImageDisplayable;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import org.quelea.displayable.Displayable;
import org.quelea.displayable.TransferDisplayable;
import org.quelea.utils.Utils;

/**
 * The panel displayed on the library to select the list of images.
 * @author Michael
 */
public class ImageListPanel extends JPanel {

    private final JList<ImageDisplayable> imageList;
    private String dir;

    /**
     * Create a new image list panel.
     * @param dir the directory to use.
     */
    public ImageListPanel(String dir) {
        this.dir = dir;
        imageList = new JList<>(new DefaultListModel<ImageDisplayable>());
        imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(imageList, DnDConstants.ACTION_MOVE, new DragGestureListener() {

            public void dragGestureRecognized(DragGestureEvent dge) {
                if (imageList.getSelectedValue() != null) {
                    dge.startDrag(DragSource.DefaultCopyDrop, new TransferDisplayable((Displayable) imageList.getModel().getElementAt(imageList.locationToIndex(dge.getDragOrigin()))));
                }
            }
        });
        imageList.setCellRenderer(new CustomCellRenderer());
        addFiles((DefaultListModel<ImageDisplayable>) imageList.getModel());
        imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        imageList.setVisibleRowCount(-1);
        JScrollPane scroll = new JScrollPane(imageList);
        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Change the panel to display a new directory.
     * @param newDir the new directory.
     */
    public void changeDir(String newDir) {
        dir = newDir;
        refresh();
    }

    /**
     * Refresh the contents of this image list panel.
     */
    public void refresh() {
        DefaultListModel<ImageDisplayable> model = (DefaultListModel<ImageDisplayable>) imageList.getModel();
        model.clear();
        addFiles(model);
    }

    /**
     * Add the files to the given model.
     * @param model the model to add files to.
     */
    private void addFiles(DefaultListModel<ImageDisplayable> model) {
        File[] files = new File(dir).listFiles();
        for (File file : files) {
            if (Utils.fileIsImage(file)) {
                model.addElement(new ImageDisplayable(file));
            }
        }
    }

    /**
     * Get the full size currently selected image.
     * @return the full size currently selected image.
     */
    public BufferedImage getSelectedImage() {
        File file = imageList.getSelectedValue().getFile();
        return Utils.getImage(file.getAbsolutePath());
    }

    /**
     * The custom cell renderer for the JList behind the panel.
     */
    private static class CustomCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            CustomCellRenderer ret = (CustomCellRenderer)super.getListCellRendererComponent(list, ((ImageDisplayable)value).getImage(), index, isSelected, cellHasFocus);
            ret.setBorder(new EmptyBorder(5, 5, 5, 5));
            ret.setToolTipText(((ImageDisplayable)value).getFile().getName());
            return ret;
        }
    }
}
