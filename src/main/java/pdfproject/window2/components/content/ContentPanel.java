package pdfproject.window2.components.content;

import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class ContentPanel extends JPanel {

    // ---- Height ratios ----
    private static final float INPUT_RATIO    = 0.30f;
    private static final float OUTPUT_RATIO   = 0.15f;
    private static final float ADVANCED_RATIO = 0.40f;

    public ContentPanel(int height) {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.CONTENT_BG);

        MatteBorder border = new MatteBorder(
                3, 3, 0, 3,
                ThemeManager.ACCENT_PRIMARY
        );
        setBorder(border);

        // ---- Calculate usable height ----
        Insets insets = border.getBorderInsets(this);
        int usableHeight = height - insets.top - insets.bottom;

        // ---- Calculate heights ----
        int inputHeight    = Math.round(usableHeight * INPUT_RATIO);
        int outputHeight   = Math.round(usableHeight * OUTPUT_RATIO);
        int advancedHeight = Math.round(usableHeight * ADVANCED_RATIO);

        int actionHeight = usableHeight
                - inputHeight
                - outputHeight
                - advancedHeight;

        // ---- Panels ----
        InputAreaPanel inputArea = new InputAreaPanel();
        inputArea.setPreferredSize(new Dimension(10, inputHeight));

        OutputQualityPanel outputQuality = new OutputQualityPanel();
        outputQuality.setPreferredSize(new Dimension(10, outputHeight));

        AdvancedOptionsPanel advancedOptions = new AdvancedOptionsPanel();
        advancedOptions.setPreferredSize(new Dimension(10, advancedHeight));


        ActionPanel actionPanel = new ActionPanel(
                inputArea,
                outputQuality,
                advancedOptions
        );
        actionPanel.setPreferredSize(new Dimension(10, actionHeight));

        JPanel stack = new JPanel();
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setOpaque(false);

        stack.add(inputArea);
        stack.add(outputQuality);
        stack.add(advancedOptions);
        stack.add(actionPanel);

        add(stack, BorderLayout.CENTER);
    }
}
