package pdfproject.windowui.core;

import pdfproject.windowui.body.BodyContentPanel;
import pdfproject.windowui.components.ConsolePanel;
import pdfproject.windowui.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class Window {

    private final JFrame frame;
    private final ConsolePanel consolePanel;
    private final JPanel bodyPanel;

    public Window(int height) {
        int width = (int) (height * (16.0 / 9));

        frame = new JFrame("PDF Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(ThemeColors.BACKGROUND);

        // Set emoji icon
        frame.setIconImage(generateIconImage(new Font("Segoe UI Emoji", Font.PLAIN, 48)));

        // Body panel and content
        bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(ThemeColors.BACKGROUND);
        bodyPanel.add(new BodyContentPanel(), BorderLayout.CENTER); // ✅ Add body content here

        // Console panel
        consolePanel = new ConsolePanel();
        consolePanel.redirectSystemStreams();

        // Main wrapper layout
        JPanel mainWrapper = new JPanel();
        mainWrapper.setLayout(new BoxLayout(mainWrapper, BoxLayout.Y_AXIS));
        mainWrapper.setBackground(ThemeColors.BACKGROUND);

        mainWrapper.add(bodyPanel);
        mainWrapper.add(consolePanel);

        frame.setContentPane(mainWrapper);
        frame.setVisible(true);

        resizePanels(height);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizePanels(frame.getHeight());
            }
        });
    }

    private void resizePanels(int totalHeight) {
        int consoleHeight = (int) (totalHeight * 0.4);
        int bodyHeight = totalHeight - consoleHeight;

        bodyPanel.setPreferredSize(new Dimension(0, bodyHeight));
        consolePanel.setDynamicHeight(consoleHeight);

        frame.revalidate();
    }

    private Image generateIconImage(Font font) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(ThemeColors.THEME_BLUE);
        g2d.drawString("📝🔍", 5, 50);
        g2d.dispose();
        return img;
    }

    public JFrame getFrame() {
        return frame;
    }

    public JPanel getBodyPanel() {
        return bodyPanel;
    }
}
