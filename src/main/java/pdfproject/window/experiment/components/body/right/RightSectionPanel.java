package pdfproject.window.experiment.components.body.right;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;
import pdfproject.window.experiment.utils.ValidationCenter;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Right half of the body area.
 * Becomes non-interactive during validation (start -> disable, stop -> enable).
 */
public class RightSectionPanel extends JPanel implements PropertyChangeListener, ValidationCenter.ValidationListener {

    private final JLabel titleLabel;
    private final JTextArea preview;

    public RightSectionPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));

        titleLabel = new JLabel("Right — Preview / Status", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        preview = new JTextArea();
        preview.setEditable(false);
        preview.setText("Preview / progress / logs will appear here.");
        preview.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        preview.setOpaque(false);
        preview.setLineWrap(true);
        preview.setWrapStyleWord(true);

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
        preview.setForeground(t.bodyText);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }

    // Register this panel as the single global listener
    @Override
    public void addNotify() {
        super.addNotify();
        ValidationCenter.setListener(this);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
        ValidationCenter.setListener(null);
    }

    // ---------------- Validation Events ----------------

    @Override
    public void onStart() {
        // disable entire panel so user cannot interact
        setEnabledRecursively(this, false);
    }

    @Override
    public void onStop() {
        // enable entire panel again
        setEnabledRecursively(this, true);
    }

    // Utility: recursively enable/disable all components
    private void setEnabledRecursively(Component c, boolean enabled) {
        c.setEnabled(enabled);
        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                setEnabledRecursively(child, enabled);
            }
        }
    }
}
