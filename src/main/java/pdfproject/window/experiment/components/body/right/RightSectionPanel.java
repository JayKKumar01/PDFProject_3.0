package pdfproject.window.experiment.components.body.right;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Right half of the body area. Lightweight, themed panel intended to hold preview/status.
 */
public class RightSectionPanel extends JPanel implements PropertyChangeListener {

    private final JLabel titleLabel;

    public RightSectionPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));

        titleLabel = new JLabel("Right — Preview / Status", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Example placeholder center area (you'll replace with the real preview)
        JTextArea preview = new JTextArea();
        preview.setEditable(false);
        preview.setText("Preview / progress / logs will appear here.");
        preview.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        preview.setOpaque(false);

        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(preview,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private void applyTheme(ExperimentTheme t) {
        setBackground(t.bodyBg);
        titleLabel.setForeground(t.headerText);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
