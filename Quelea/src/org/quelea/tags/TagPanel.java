package org.quelea.tags;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import org.quelea.utils.Utils;
import org.quelea.utils.WrapLayout;
import org.quelea.windows.library.LibrarySongList;

/**
 *
 * @author Michael
 */
public class TagPanel extends JPanel {

    private Set<String> tags;

    public TagPanel() {
        setLayout(new WrapLayout(FlowLayout.LEFT));
        tags = new HashSet<>();
    }

    public void addTag(final String tag, final LibrarySongList list) {
        tags.add(tag);
        final JPanel tagPanel = new JPanel();
        tagPanel.setBorder(new LineBorder(Color.BLACK, 2));
        tagPanel.add(new JLabel(tag));
        final JButton button = new JButton(Utils.getImageIcon("icons/delete.png", 16, 16));
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Container ancestor = button.getTopLevelAncestor();
                tags.remove(tag);
                remove(tagPanel);
                redo(ancestor);
                list.filterByTag(getTags(), false);
            }
        });
        button.setBorder(new EmptyBorder(0, 0, 0, 0));
        tagPanel.add(button);
        add(tagPanel);
        redo(button.getTopLevelAncestor());
    }

    private void redo(Container ancestor) {
        validate();
        repaint();
        ((JDialog) ancestor).validate();
        ((JDialog) ancestor).repaint();
    }

    public List<String> getTags() {
        List<String> ret = new ArrayList<>();
        ret.addAll(tags);
        return ret;
    }

    public void removeTags() {
        tags.clear();
        removeAll();
    }
}
