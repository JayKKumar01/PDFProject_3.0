package pdfproject.window2.components.content;

import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class ContentPanel extends JPanel {

    // ---- Height ratios ----
    private static final float INPUT_RATIO  = 0.30f;
    private static final float OUTPUT_RATIO = 0.15f;
    private static final float ACTION_RATIO = 0.15f;
    // remaining → AdvancedOptionsPanel (flexible)

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
        // Usable height (inside border)
        // ----------------------------
        Insets insets = border.getBorderInsets(this);
        int usableHeight = h - insets.top - insets.bottom;

        int inputHeight  = Math.round(usableHeight * INPUT_RATIO);
        int outputHeight = Math.round(usableHeight * OUTPUT_RATIO);
        int actionHeight = Math.round(usableHeight * ACTION_RATIO);

        int advancedHeight =
                usableHeight
                        - inputHeight
                        - outputHeight
                        - actionHeight;

        if (advancedHeight < 0) advancedHeight = 0;

        // ----------------------------
        // Stack container
        // ----------------------------
        JPanel stack = new JPanel();
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setOpaque(false);

        // ----------------------------
        // Panels
        // ----------------------------
        InputAreaPanel inputArea = new InputAreaPanel();
        inputArea.setPreferredSize(new Dimension(10, inputHeight));

        OutputQualityPanel outputQuality = new OutputQualityPanel();
        outputQuality.setPreferredSize(new Dimension(10, outputHeight));

        AdvancedOptionsPanel advancedOptions = new AdvancedOptionsPanel();
        advancedOptions.setPreferredSize(new Dimension(10, advancedHeight)); // flexible

        ActionPanel actionPanel = new ActionPanel(
                inputArea,
                outputQuality,
                advancedOptions
        );
        actionPanel.setPreferredSize(new Dimension(10, actionHeight));

        // ----------------------------
        // Assembly (order matters)
        // ----------------------------
        stack.add(inputArea);
        stack.add(outputQuality);
        stack.add(advancedOptions);
        stack.add(actionPanel); // always touches bottom

        add(stack, BorderLayout.CENTER);
    }
}
