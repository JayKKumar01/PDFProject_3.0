package pdfproject.window.components.body.right;

import pdfproject.Config;
import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.constants.ThemeColors;

import javax.swing.*;
import java.awt.*;

public class ImageOptionPanel extends JPanel implements TaskStateListener {

    private final ImageQualityPanel imageQualityPanel;
    private final ImagePathPanel imagePathPanel;


    public ImageOptionPanel() {
        setLayout(new GridLayout(1,2,10,0));
        setBackground(ThemeColors.LAYOUT_BORDER);

        imageQualityPanel = new ImageQualityPanel();
        imagePathPanel = new ImagePathPanel();

        add(imageQualityPanel);
        add(imagePathPanel);

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
}
