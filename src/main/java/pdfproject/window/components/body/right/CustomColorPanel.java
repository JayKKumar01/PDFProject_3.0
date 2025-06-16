package pdfproject.window.components.body.right;

import pdfproject.constants.OperationColor;
import pdfproject.interfaces.TaskStateListener;
import pdfproject.window.constants.ThemeColors;
import pdfproject.window.utils.ComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomColorPanel extends JPanel implements TaskStateListener {

    private final Map<String, JComboBox<String>> dropdownMap = new LinkedHashMap<>();
    private final Map<String, Color> defaultColorMap = new LinkedHashMap<>();

    public CustomColorPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.BACKGROUND);
        setPreferredSize(new Dimension(0, 300));

        initializeDefaultColors();

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private void initializeDefaultColors() {
        defaultColorMap.put("Deleted", OperationColor.DELETED);
        defaultColorMap.put("Added", OperationColor.ADDED);
        defaultColorMap.put("Font Name", OperationColor.FONT_NAME);
        defaultColorMap.put("Font Size", OperationColor.FONT_SIZE);
        defaultColorMap.put("Font Style", OperationColor.FONT_STYLE);
        defaultColorMap.put("Multiple", OperationColor.MULTIPLE);
    }

    private JPanel buildHeader() {
        JLabel title = new JLabel("Custom Operation Colors");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(ThemeColors.THEME_BLUE);

        JButton resetBtn = ComponentFactory.createStyledButton(
                "Reset", ThemeColors.THEME_BLUE, new Color(230, 240, 255));
        resetBtn.addActionListener(e -> resetToDefaults());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(getBackground());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        panel.add(title, BorderLayout.WEST);
        panel.add(resetBtn, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildBody() {
        JPanel container = new JPanel(new GridLayout(1, 2, 20, 0));
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel left = createColumnPanel();
        JPanel right = createColumnPanel();

        int index = 0;
        for (Map.Entry<String, Color> entry : defaultColorMap.entrySet()) {
            JPanel row = createColorRow(entry.getKey(), entry.getValue());
            if (index++ < 3) left.add(row);
            else right.add(row);
        }

        container.add(left);
        container.add(right);
        return container;
    }

    private JPanel createColumnPanel() {
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setOpaque(false);
        return column;
    }

    private JPanel createColorRow(String label, Color defaultColor) {
        JLabel nameLabel = new JLabel(label + ":");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameLabel.setPreferredSize(new Dimension(130, 25));
        nameLabel.setForeground(ThemeColors.THEME_BLUE);

        JComboBox<String> dropdown = new JComboBox<>(Helper.getAllColorNames());
        dropdown.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dropdown.setPreferredSize(new Dimension(140, 25));
        dropdown.setMaximumSize(dropdown.getPreferredSize());
        dropdown.setSelectedItem(getColorName(defaultColor));
        dropdown.addActionListener(e -> Helper.setOperationColor(label, Helper.getColorFromName((String) dropdown.getSelectedItem())));

        dropdownMap.put(label, dropdown);

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.add(nameLabel);
        row.add(Box.createHorizontalStrut(10));
        row.add(dropdown);
        row.add(Box.createVerticalStrut(8));

        return row;
    }

    private void resetToDefaults() {
        for (Map.Entry<String, JComboBox<String>> entry : dropdownMap.entrySet()) {
            String label = entry.getKey();
            Color defaultColor = defaultColorMap.get(label);
            String colorName = getColorName(defaultColor);

            JComboBox<String> dropdown = entry.getValue();

            // Temporarily remove the ActionListener
            ActionListener[] listeners = dropdown.getActionListeners();
            for (ActionListener l : listeners) {
                dropdown.removeActionListener(l);
            }

            // Set default color and update OperationColor manually
            dropdown.setSelectedItem(colorName);
            Helper.setOperationColor(label, defaultColor);

            // Reattach ActionListeners
            for (ActionListener l : listeners) {
                dropdown.addActionListener(l);
            }
        }
    }


    private String getColorName(Color target) {
        return Helper.getAllColorMap().entrySet().stream()
                .filter(e -> e.getValue().equals(target))
                .map(e -> Helper.capitalize(e.getKey()))
                .findFirst()
                .orElse("Black");
    }


    @Override
    public void onStart() {
        Helper.setEnabledRecursively(this, false);
    }

    @Override
    public void onStop() {
        Helper.setEnabledRecursively(this, true);
    }


}
