package pdfproject.window.components.body.right.option;

import pdfproject.Config;
import pdfproject.window.core.Theme;
import pdfproject.window.utils.ThemeManager;
import pdfproject.window.utils.UiScale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * QualityPanel — centered label + dropdown for Image Quality.
 * Choosing Low/Medium/High sets Config.renderDpi to 100/150/200.
 */
public class QualityPanel extends JPanel implements PropertyChangeListener {

    private static final int[] DPI_VALUES = {100, 150, 200};

    private final JLabel label;
    private final JComboBox<String> combo;

    public QualityPanel() {
        setLayout(new GridBagLayout());
        setOpaque(true);

        label = new JLabel("Image Quality :");
        label.setFont(UiScale.getScaledFont(new Font("Segoe UI", Font.PLAIN, 10)));

        combo = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        combo.setFocusable(true);
        combo.setBorder(null);
        combo.setOpaque(true);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 8);
        add(label, c);

        c.gridx = 1;
        add(combo, c);

        // custom renderer using full theme color set
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                Theme t = ThemeManager.getTheme();
                if (t != null) {
                    Color normalBg = t.consoleBg;      // use console bg for dropdown rows
                    Color normalFg = t.consoleText;    // dropdown text
                    Color selBg = t.usernameAccent;    // accent highlight
                    Color selFg = Theme.readableForeground(selBg);

                    if (isSelected) {
                        setBackground(selBg);
                        setForeground(selFg);
                    } else {
                        setBackground(normalBg);
                        setForeground(normalFg);
                    }
                    setOpaque(true);
                }
                return this;
            }
        });

        // initialize selection from Config.renderDpi if possible
        int initIndex = indexForDpi(Config.renderDpi);
        if (initIndex >= 0) combo.setSelectedIndex(initIndex);

        // update Config.renderDpi when selection changes
        combo.addActionListener((ActionEvent e) -> {
            int index = combo.getSelectedIndex();
            if (index < 0 || index >= DPI_VALUES.length) return;
            int dpi = DPI_VALUES[index];
            Config.renderDpi = dpi;
            System.out.println("Selected image quality: " + combo.getSelectedItem() + " -> renderDpi=" + dpi);
        });

        applyTheme(ThemeManager.getTheme());
    }

    private int indexForDpi(int dpi) {
        for (int i = 0; i < DPI_VALUES.length; i++) {
            if (DPI_VALUES[i] == dpi) return i;
        }
        return -1;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        ThemeManager.register(this);
        applyTheme(ThemeManager.getTheme());
    }

    @Override
    public void removeNotify() {
        ThemeManager.unregister(this);
        super.removeNotify();
    }

    private void applyTheme(Theme t) {
        if (t == null) return;

        // background of this panel
        setBackground(t.bodyBg);

        // label styling
        label.setForeground(t.usernameAccent);

        // combo styling: different color than body
        combo.setBackground(t.consoleBg);
        combo.setForeground(t.headerText); // visually distinct readable color

        // editable text field inside combo box
        Component ed = combo.getEditor() != null ? combo.getEditor().getEditorComponent() : null;
        if (ed instanceof JComponent jc) {
            jc.setBackground(t.consoleBg);
            jc.setForeground(t.headerText);
            jc.setOpaque(true);
        }

        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (SwingUtilities.isEventDispatchThread())
            applyTheme(ThemeManager.getTheme());
        else
            SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }
}
