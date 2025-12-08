package pdfproject.window.experiment.components.body.right;

import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;
import pdfproject.window.experiment.components.ValidationAwarePanel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Right half of the body area.
 * Validation enable/disable is handled by ValidationAwarePanel.
 * This panel only manages theming and its own UI.
 */
public class RightSectionPanel extends ValidationAwarePanel implements PropertyChangeListener {

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private final JLabel titleLabel;
    private final JTextArea preview;

    public RightSectionPanel() {
        super();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));
        setOpaque(true);

        titleLabel = new JLabel("Right — Preview / Status", SwingConstants.LEFT);
        titleLabel.setFont(TITLE_FONT);

        preview = new JTextArea();
        preview.setEditable(false);
        preview.setText("Preview / progress / logs will appear here.");
        preview.setFont(BODY_FONT);
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
        if (t == null) return;
        setBackground(t.bodyBg);
        titleLabel.setForeground(t.headerText);
        preview.setForeground(t.bodyText);
        revalidate();
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // ThemeManager fires property changes — update on EDT
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }

    /**
     * Ensure ThemeManager unregistered; validation lifecycle handled by ValidationAwarePanel.
     */
    @Override
    public void removeNotify() {
        ThemeManager.unregister(this);
        super.removeNotify();
    }
}
