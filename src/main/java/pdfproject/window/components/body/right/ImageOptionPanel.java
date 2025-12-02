package pdfproject.window.components.body.right;

import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class ImageOptionPanel extends JPanel implements TaskStateListener, ThemeManager.ThemeChangeListener {

    private final ImageQualityPanel imageQualityPanel;
    private final ImagePathPanel imagePathPanel;

    public ImageOptionPanel() {
        setLayout(new GridLayout(1, 2, 10, 0));
        setBackground(ThemeColors.LAYOUT_BORDER);

        imageQualityPanel = new ImageQualityPanel();
        imagePathPanel = new ImagePathPanel();

        add(imageQualityPanel);
        add(imagePathPanel);

        // register and apply initial theme
        ThemeManager.register(this);
        applyTheme(ThemeManager.isDarkMode());
    }

    @Override
    public void onStart() {
        imageQualityPanel.onStart();
        imagePathPanel.onStart();
    }

    @Override
    public void onStop() {
        imageQualityPanel.onStop();
        imagePathPanel.onStop();
    }

    @Override
    public void onThemeChanged(boolean dark) {
        applyTheme(dark);
    }

    private void applyTheme(boolean dark) {
        setBackground(dark ? ThemeColors.DARK_LAYOUT_BORDER : ThemeColors.LAYOUT_BORDER);

        // propagate to children if they listen themselves (they do)
        if (imageQualityPanel instanceof ThemeManager.ThemeChangeListener) {
            ((ThemeManager.ThemeChangeListener) imageQualityPanel).onThemeChanged(dark);
        }
        if (imagePathPanel instanceof ThemeManager.ThemeChangeListener) {
            ((ThemeManager.ThemeChangeListener) imagePathPanel).onThemeChanged(dark);
        }

        revalidate();
        repaint();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        ThemeManager.unregister(this);
    }
}
