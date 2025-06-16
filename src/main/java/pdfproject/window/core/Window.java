package pdfproject.window.core;

import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.components.body.BodyContentPanel;
import pdfproject.window.components.console.ConsolePanel;
import pdfproject.window.constants.ThemeColors;

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

        frame = initFrame(width, height);
        consolePanel = new ConsolePanel();
        consolePanel.redirectSystemStreams();

        BodyContentPanel bodyContentPanel = new BodyContentPanel();
        bodyContentPanel.setTaskStateListener(new TaskStateListener() {
            @Override
            public void onStart() {
                consolePanel.onStart();
                bodyContentPanel.onStart();
            }

            @Override
            public void onStop() {
                consolePanel.onStop();
                bodyContentPanel.onStop();
            }
        });

        bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(ThemeColors.BACKGROUND);
        bodyPanel.add(bodyContentPanel, BorderLayout.CENTER);

        frame.setContentPane(initMainLayout());
        resizePanels(height);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizePanels(frame.getHeight());
            }
        });

        frame.setVisible(true);
    }

    private JFrame initFrame(int width, int height) {
        JFrame f = new JFrame("PDF Project");
        f.setSize(width, height);
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setBackground(ThemeColors.BACKGROUND);
        f.setIconImage(generateIconImage(new Font("Segoe UI Emoji", Font.PLAIN, 48)));
        return f;
    }

    private JPanel initMainLayout() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(ThemeColors.BACKGROUND);
        wrapper.add(bodyPanel);
        wrapper.add(consolePanel);
        return wrapper;
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
        Graphics2D g = img.createGraphics();
        g.setFont(font);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(ThemeColors.THEME_BLUE);
        g.drawString("📝🔍", 5, 50);
        g.dispose();
        return img;
    }
}
