package pdfproject.window.components.content;

import pdfproject.constants.OperationColor;
import pdfproject.utils.AppSettings;
import pdfproject.window.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdvancedOptionsPanel extends JPanel {

    private static final int COMBO_WIDTH = 140;

    /** Order matters → LinkedHashMap */
    private static final Map<String, String> OPS = new LinkedHashMap<>() {{
        put("Deleted:", "Deleted");
        put("Added:", "Added");
        put("Font Name:", "Font Name");
        put("Font Size:", "Font Size");
        put("Font Style:", "Font Style");
        put("Multiple:", "Multiple");
    }};

    private final String[] colorNames = Helper.getAllColorNames();

    public AdvancedOptionsPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(ThemeManager.CONTENT_BG);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        add(createTopBar(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
    }

    // =====================================================
    // Top bar
    // =====================================================
    private JComponent createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Color Options");
        title.setForeground(ThemeManager.ACCENT_PRIMARY);
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
    // Center grid
    // =====================================================
    private JComponent createCenter() {
        JPanel center = new JPanel(new GridLayout(2, 3, 150, 8));
        center.setOpaque(false);

        OPS.forEach((label, opKey) -> center.add(createRow(label, opKey)));
        return center;
    }

    // =====================================================
    // Single row (self-contained)
    // =====================================================
    private JPanel createRow(String labelText, String opKey) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;

        // label
        JLabel label = new JLabel(labelText);
        label.setForeground(ThemeManager.ACCENT_SOFT);
        gbc.gridx = 0;
        row.add(label, gbc);

        // spacer
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        row.add(Box.createHorizontalGlue(), gbc);

        // combo
        JComboBox<String> combo = new JComboBox<>(colorNames);
        combo.setFocusable(false);
        combo.setBackground(ThemeManager.CONSOLE_BG);
        combo.setForeground(ThemeManager.HEADER_TEXT);
        Dimension size = new Dimension(COMBO_WIDTH, combo.getPreferredSize().height);
        combo.setPreferredSize(size);

        // preview
        JLabel preview = new JLabel();
        preview.setOpaque(true);
        preview.setPreferredSize(new Dimension(22, 16));
        preview.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        loadInitialColor(opKey, combo, preview);

        combo.addActionListener(e -> {
            String name = ((String) combo.getSelectedItem()).toLowerCase();
            Color c = Helper.getColorFromName(name);

            Helper.setOperationColor(opKey, c);
            AppSettings.saveOperationColor(opKey, name);
            preview.setBackground(c);
        });

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        row.add(combo, gbc);

        gbc.gridx = 3;
        row.add(preview, gbc);

        return row;
    }

    // =====================================================
    // Helpers
    // =====================================================
    private void loadInitialColor(String opKey, JComboBox<String> combo, JLabel preview) {
        Color def = getCurrentColor(opKey);
        String defName = Helper.capitalize(colorNameFor(def));

        String saved = AppSettings.loadOperationColor(opKey, defName.toLowerCase());
        Color c = Helper.getColorFromName(saved);

        Helper.setOperationColor(opKey, c);
        combo.setSelectedItem(Helper.capitalize(saved));
        preview.setBackground(c);
    }

    private void resetColors() {
        OperationColor.DELETED    = OperationColor.DEF_DELETED;
        OperationColor.ADDED      = OperationColor.DEF_ADDED;
        OperationColor.FONT_NAME  = OperationColor.DEF_FONT_NAME;
        OperationColor.FONT_SIZE  = OperationColor.DEF_FONT_SIZE;
        OperationColor.FONT_STYLE = OperationColor.DEF_FONT_STYLE;
        OperationColor.MULTIPLE   = OperationColor.DEF_MULTIPLE;

        OPS.forEach((k, op) -> {
            Color c = getCurrentColor(op);
            AppSettings.saveOperationColor(op, colorNameFor(c));
        });

        removeAll();
        add(createTopBar(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private Color getCurrentColor(String op) {
        return switch (op) {
            case "Deleted" -> OperationColor.DELETED;
            case "Added" -> OperationColor.ADDED;
            case "Font Name" -> OperationColor.FONT_NAME;
            case "Font Size" -> OperationColor.FONT_SIZE;
            case "Font Style" -> OperationColor.FONT_STYLE;
            default -> OperationColor.MULTIPLE;
        };
    }

    private String colorNameFor(Color color) {
        return Helper.getAllColorMap().entrySet()
                .stream()
                .filter(e -> e.getValue().equals(color))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("black");
    }
}
