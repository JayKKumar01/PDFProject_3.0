package pdfproject.window.experiment.components.body.left;

import pdfproject.window.experiment.components.common.SplitTwoPanel;
import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LeftSectionPanel extends JPanel implements PropertyChangeListener {

    private final InputPanel inputPanel;
    private final LauncherPanel launcherPanel;

    public LeftSectionPanel() {
        super(new BorderLayout());

        inputPanel = new InputPanel();
        launcherPanel = new LauncherPanel();

        // Vertical split: top | 1dp band | bottom, equal halves
        SplitTwoPanel split = new SplitTwoPanel(
                SplitTwoPanel.Orientation.VERTICAL,
                1,
                -1.0   // <===== equal 50 / 50
        );
        split.setComponents(inputPanel, launcherPanel);

        add(split, BorderLayout.CENTER);

        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    private void applyTheme(ExperimentTheme t) {
        if (t == null) return;
        setBackground(t.bodyBg);
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

    public InputPanel getInputPanel() { return inputPanel; }
    public LauncherPanel getLauncherPanel() { return launcherPanel; }
}
