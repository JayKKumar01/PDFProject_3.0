package pdfproject.window.experiment.components.body.right.option;

import pdfproject.window.experiment.components.common.SplitTwoPanel;
import pdfproject.window.experiment.core.ExperimentTheme;
import pdfproject.window.experiment.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class OptionPanel extends JPanel implements PropertyChangeListener {

    private final QualityPanel qualityPanel;
    private final PathPanel pathPanel;

    public OptionPanel() {
        super(new BorderLayout());
        setOpaque(true);

        qualityPanel = new QualityPanel();
        pathPanel = new PathPanel();

        // horizontal split (left/right) - equal halves
        SplitTwoPanel split = new SplitTwoPanel(SplitTwoPanel.Orientation.HORIZONTAL, 1, 0.29);
        split.setComponents(qualityPanel, pathPanel);

        add(split, BorderLayout.CENTER);

        applyTheme(ThemeManager.getTheme());
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

    private void applyTheme(ExperimentTheme t) {
        if (t == null) return;

        Color bg = t.bodyBg;

        setBackground(bg);

        qualityPanel.setBackground(bg);
        pathPanel.setBackground(bg);

        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }

    public QualityPanel getQualityPanel() { return qualityPanel; }
    public PathPanel getPathPanel() { return pathPanel; }
}
