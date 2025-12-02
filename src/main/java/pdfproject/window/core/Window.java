package pdfproject.window.core;

import pdfproject.Config;
import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.components.body.BodyContentPanel;
import pdfproject.window.components.console.ConsolePanel;
import pdfproject.window.components.header.HeaderPanel;
import pdfproject.window.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class Window {

    private final JFrame frame;

    private final HeaderPanel headerPanel;
    private final BodyContentPanel bodyPanel;
    private final ConsolePanel consolePanel;

    public Window(int height) {
        int width = (int) (height * (16.0 / 9));

        frame = initFrame(width, height);

        // Panels
        headerPanel = new HeaderPanel();
        bodyPanel = new BodyContentPanel();
        consolePanel = new ConsolePanel();
        consolePanel.redirectSystemStreams();

        // Wiring task listener
        bodyPanel.setTaskStateListener(new TaskStateListener() {
            @Override
            public void onStart() {
                headerPanel.onStart();
                consolePanel.onStart();
                bodyPanel.onStart();
            }

            @Override
            public void onStop() {
                headerPanel.onStop();
                consolePanel.onStop();
                bodyPanel.onStop();
            }
        });


        // Main layout container (stack vertically)
        frame.setContentPane(initMainLayout());

        // Initial size distribution
        resizePanels(height);

        // Resize listener
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizePanels(frame.getHeight());
            }
        });

        frame.setVisible(true);
    }

    private JFrame initFrame(int width, int height) {
        JFrame f = new JFrame(Config.FRAME_NAME);
        f.setSize(width, height);
        f.setLocationRelativeTo(null);
        f.setResizable(true); // now resizable since proportional layout handles it
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setBackground(ThemeColors.BACKGROUND);
        f.setIconImage(generateIconImage(new Font("Segoe UI Emoji", Font.PLAIN, 48)));
        return f;
    }

    private JPanel initMainLayout() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(ThemeColors.BACKGROUND);

        wrapper.add(headerPanel);
        wrapper.add(bodyPanel);
        wrapper.add(consolePanel);

        return wrapper;
    }

    /**
     * Applies proportional layout:
     * header = 15%,
     * body = 60%,
     * console = 25%
     */
    private void resizePanels(int totalHeight) {

        int headerHeight  = (int) (totalHeight * 0.10);
        int bodyHeight    = (int) (totalHeight * 0.60);
        int consoleHeight = totalHeight - headerHeight - bodyHeight;

        headerPanel.setPreferredSize(new Dimension(0, headerHeight));
        bodyPanel.setPreferredSize(new Dimension(0, bodyHeight));
        consolePanel.setDynamicHeight(consoleHeight);

        headerPanel.revalidate();
        bodyPanel.revalidate();
        consolePanel.revalidate();
        frame.repaint();
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
