package pdfproject.windowui.body;

import pdfproject.windowui.body.left.ButtonSectionPanel;
import pdfproject.windowui.body.left.InputSectionPanel;
import pdfproject.windowui.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;

public class LeftPanel extends JPanel {

    private final InputSectionPanel inputSectionPanel;
    private final ButtonSectionPanel buttonSectionPanel;

    public LeftPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ThemeColors.BACKGROUND);

        inputSectionPanel = new InputSectionPanel();
        buttonSectionPanel = new ButtonSectionPanel();

        inputSectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 0));  // let layout grow vertically
        buttonSectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        add(inputSectionPanel);
        add(buttonSectionPanel);
    }
}
