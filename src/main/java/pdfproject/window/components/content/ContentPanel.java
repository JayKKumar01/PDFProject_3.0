package pdfproject.window.components.content;

import pdfproject.window.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class ContentPanel extends JPanel {

    // ---- Height ratios ----
    private static final float INPUT_RATIO  = 0.30f;
    private static final float OUTPUT_RATIO = 0.15f;
    // remaining → AdvancedOptionsPanel
    // ActionPanel → natural height

    public ContentPanel(int h) {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(ThemeManager.CONTENT_BG);

        MatteBorder border = new MatteBorder(
                3, 3, 0, 3,
                ThemeManager.ACCENT_PRIMARY
        );
        setBorder(border);

        // ----------------------------
        // Calculate usable height
        // ----------------------------
        Insets insets = border.getBorderInsets(this);
        int usableHeight = h - insets.top - insets.bottom;

        int inputHeight  = Math.round(usableHeight * INPUT_RATIO);
        int outputHeight = Math.round(usableHeight * OUTPUT_RATIO);

        // ----------------------------
        // Panels
        // ----------------------------
        InputAreaPanel inputArea = new InputAreaPanel();
        inputArea.setPreferredSize(new Dimension(Integer.MAX_VALUE, inputHeight));
        inputArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, inputHeight));

        OutputQualityPanel outputQuality = new OutputQualityPanel();
        outputQuality.setPreferredSize(new Dimension(Integer.MAX_VALUE, outputHeight));
        outputQuality.setMaximumSize(new Dimension(Integer.MAX_VALUE, outputHeight));

        AdvancedOptionsPanel advancedOptions = new AdvancedOptionsPanel();
        // flexible – no preferred size

        // ----------------------------
        // Center stack
        // ----------------------------
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        center.add(inputArea);
        center.add(outputQuality);
        center.add(advancedOptions);

        // ----------------------------
        // Action panel (receives panels)
        // ----------------------------
        ActionPanel actionPanel = new ActionPanel(
                inputArea,
                outputQuality,
                advancedOptions
        );

        // ----------------------------
        // Assemble
        // ----------------------------
        add(center, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH); // always touches bottom
    }
}
