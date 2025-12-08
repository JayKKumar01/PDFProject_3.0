package pdfproject.window.experiment.components.body;

import pdfproject.window.experiment.components.body.left.LeftSectionPanel;
import pdfproject.window.experiment.components.body.right.RightSectionPanel;
import pdfproject.window.experiment.components.common.SplitTwoPanel;
import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class BodyExperimentPanel extends JPanel implements PropertyChangeListener {

    private final LeftSectionPanel left;
    private final RightSectionPanel right;

    public BodyExperimentPanel() {
        super(new BorderLayout());

        left = new LeftSectionPanel();
        right = new RightSectionPanel();

        // Horizontal split, 1dp separator, 40% left / 60% right
        SplitTwoPanel split = new SplitTwoPanel(
                SplitTwoPanel.Orientation.HORIZONTAL,
                1,
                0.40    // <===== 40 / 60 split
        );
        split.setComponents(left, right);

        add(split, BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        ThemeManager.register(this);
        applyTheme(ThemeManager.getTheme());
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }

    private void applyTheme(ExperimentTheme t) {
        if (t == null) return;
        Color bg = t.bodyBg;
        setBackground(bg);
        left.setBackground(bg);
        right.setBackground(bg);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }
}
