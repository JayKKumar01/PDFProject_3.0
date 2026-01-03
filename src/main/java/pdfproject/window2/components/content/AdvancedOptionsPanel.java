package pdfproject.window2.components.content;

import pdfproject.constants.OperationColor;
import pdfproject.window2.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancedOptionsPanel extends JPanel {

    private static final String[][] OPS = {
            {"Deleted:", "Deleted"},
            {"Added:", "Added"},
            {"Font Name:", "Font Name"},
            {"Font Size:", "Font Size"},
            {"Font Style:", "Font Style"},
            {"Multiple:", "Multiple"}
    };

    private static final int COMBO_WIDTH = 140;

    private final Map<String, Color> colorMap = Helper.getAllColorMap();
    private final String[] colorNames = Helper.getAllColorNames();

    private final List<JComboBox<String>> combos = new ArrayList<>(6);
    private final List<JLabel> previews = new ArrayList<>(6);

    public AdvancedOptionsPanel() {
        setOpaque(true);
        setLayout(new BorderLayout());
        setBackground(ThemeManager.CONTENT_BG);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        add(createTopBar(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);

        loadInitialState();
    }

    // =====================================================
    // Top bar
    // =====================================================
    private JComponent createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Color Options");
        title.setForeground(ThemeManager.CONTENT_TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD));

        JButton reset = new JButton("Reset");
        reset.setFocusable(false);
        reset.setBackground(ThemeManager.ACCENT_PRIMARY);
        reset.setForeground(Color.BLACK);

        reset.addActionListener(e -> resetColors());

        top.add(title, BorderLayout.WEST);
        top.add(reset, BorderLayout.EAST);
        return top;
    }

    // =====================================================
// Center grid (3 × 2)
// =====================================================
    private JComponent createCenter() {
        JPanel center = new JPanel(new GridLayout(2, 3, 150, 8));
        center.setOpaque(false);

        for (String[] op : OPS) {
            JPanel row = createRow(op[0], op[1]);
            center.add(row);
        }

        return center;
    }


    private JPanel createRow(String labelText, String opKey) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;

        // ---- Label (LEFT) ----
        JLabel label = new JLabel(labelText);
        label.setForeground(ThemeManager.CONTENT_TEXT);

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        row.add(label, gbc);

        // ---- Flexible spacer (pushes combo to the right) ----
        gbc.gridx = 1;
        gbc.weightx = 1;                 // 🔴 KEY LINE
        gbc.fill = GridBagConstraints.HORIZONTAL;
        row.add(Box.createHorizontalGlue(), gbc);

        // ---- Combo (RIGHT, fixed width) ----
        JComboBox<String> combo = new JComboBox<>(colorNames);
        combo.setFocusable(false);
        combo.setBackground(ThemeManager.CONSOLE_BG);
        combo.setForeground(ThemeManager.HEADER_TEXT);

        Dimension comboSize = new Dimension(COMBO_WIDTH, combo.getPreferredSize().height);
        combo.setPreferredSize(comboSize);
        combo.setMinimumSize(comboSize);
        combo.setMaximumSize(comboSize);

        combo.addActionListener(e -> {
            String selected = (String) combo.getSelectedItem();
            if (selected == null) return;

            Color c = Helper.getColorFromName(selected.toLowerCase());
            Helper.setOperationColor(opKey, c);
        });

        combos.add(combo);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        row.add(combo, gbc);

        // ---- Preview ----
        JLabel preview = new JLabel();
        preview.setOpaque(true);
        preview.setPreferredSize(new Dimension(22, 16));
        preview.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        previews.add(preview);

        gbc.gridx = 3;
        row.add(preview, gbc);

        return row;
    }


    // =====================================================
    // State helpers
    // =====================================================
    private void loadInitialState() {
        for (int i = 0; i < OPS.length; i++) {
            Color c = currentColorForOp(OPS[i][1]);
            String name = colorNameForColor(c);
            combos.get(i).setSelectedItem(Helper.capitalize(name));
            previews.get(i).setBackground(c);
        }
    }

    private void resetColors() {
        OperationColor.DELETED = OperationColor.DEF_DELETED;
        OperationColor.ADDED = OperationColor.DEF_ADDED;
        OperationColor.FONT_NAME = OperationColor.DEF_FONT_NAME;
        OperationColor.FONT_SIZE = OperationColor.DEF_FONT_SIZE;
        OperationColor.FONT_STYLE = OperationColor.DEF_FONT_STYLE;
        OperationColor.MULTIPLE = OperationColor.DEF_MULTIPLE;

        loadInitialState();
    }

    private Color currentColorForOp(String op) {
        return switch (op) {
            case "Deleted" -> OperationColor.DELETED;
            case "Added" -> OperationColor.ADDED;
            case "Font Name" -> OperationColor.FONT_NAME;
            case "Font Size" -> OperationColor.FONT_SIZE;
            case "Font Style" -> OperationColor.FONT_STYLE;
            case "Multiple" -> OperationColor.MULTIPLE;
            default -> OperationColor.MULTIPLE;
        };
    }

    private String colorNameForColor(Color color) {
        for (Map.Entry<String, Color> e : colorMap.entrySet()) {
            if (e.getValue().equals(color)) return e.getKey();
        }
        return "black";
    }
}
