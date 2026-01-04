package pdfproject.window.components.header;

import pdfproject.window.theme.ThemeManager;

import javax.swing.*;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;

public class ToggleSwitch extends JToggleButton {

    public ToggleSwitch(boolean initialState) {
        setSelected(initialState);
        setPreferredSize(new Dimension(42, 22));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        setUI(new ToggleUI());
    }

    private static class ToggleUI extends BasicToggleButtonUI {

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = c.getWidth();
            int h = c.getHeight();

            // Background
            g2.setColor(b.isSelected()
                    ? ThemeManager.TOGGLE_ON_BG
                    : ThemeManager.TOGGLE_OFF_BG);
            g2.fillRoundRect(0, 0, w, h, h, h);

            // Knob
            int knobSize = h - 4;
            int x = b.isSelected() ? w - knobSize - 2 : 2;

            g2.setColor(ThemeManager.TOGGLE_KNOB);
            g2.fillOval(x, 2, knobSize, knobSize);

            g2.dispose();
        }
    }
}
