package pdfproject.window.components.body.right.color;

import pdfproject.constants.OperationColor;
import pdfproject.window.core.Theme;
import pdfproject.window.utils.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * ColorPanel — allows customizing operation colors.
 * Top: Title + Reset button
 * Center: Two columns (3 + 3) of labeled dropdowns + previews
 *
 * Labels are left-aligned; all dropdowns start at same X and have equal widths.
 * Title and labels now use theme token colors (headerText / bodyText).
 */
public class ColorPanel extends JPanel implements PropertyChangeListener {

    // metadata for rows — label text + operation key used by Helper.setOperationColor
    private static final String[][] OPS = {
            {"Deleted", "Deleted"},
            {"Added", "Added"},
            {"Font Name", "Font Name"},
            {"Font Size", "Font Size"},
            {"Font Style", "Font Style"},
            {"Multiple", "Multiple"}
    };

    private final Map<String, Color> colorMap = Helper.getAllColorMap();
    private final String[] colorNames = Helper.getAllColorNames();

    // dynamically created controls (kept in same order as OPS)
    private final List<JComboBox<String>> combos = new ArrayList<>(6);
    private final List<JLabel> previews = new ArrayList<>(6);
    // keep references to row labels so we can theme them explicitly
    private final List<JLabel> rowLabels = new ArrayList<>(6);

    // computed max label width (pixels) so combos align across columns
    private int maxLabelWidth = -1;

    // title label reference so we can theme it
    private final JLabel titleLabel = new JLabel("Custom Operation colors");

    public ColorPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        add(createTopBar(), BorderLayout.NORTH);
        createControls();               // builds combos & previews and adds center
        initListenersAndState();        // hook listeners & load current colors

        applyTheme(ThemeManager.getTheme());
        ThemeManager.register(this);
    }

    // -----------------------
    // UI builders
    // -----------------------
    private JComponent createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setBorder(new EmptyBorder(4, 4, 4, 4));

        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(this::onReset);

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(resetBtn, BorderLayout.EAST);
        return topBar;
    }

    @SuppressWarnings("unchecked")
    private void createControls() {
        // make combos & previews & row labels
        for (String[] op : OPS) {
            JComboBox<String> combo = new JComboBox<>(colorNames);
            combos.add(combo);
            previews.add(colorPreview());

            JLabel rowLabel = new JLabel(op[0]);
            rowLabel.setHorizontalAlignment(SwingConstants.LEFT);
            rowLabels.add(rowLabel);
        }

        // compute max label width using a sample label (ensures consistent alignment)
        computeMaxLabelWidth();

        // set prototype for consistent sizing and equalize combo widths
        String prototype = getLongestCapitalized(colorNames);
        combos.forEach(c -> c.setPrototypeDisplayValue(prototype));
        equalizeComboWidths(combos.toArray(new JComboBox[0]));

        // layout: two columns (3 + 3)
        JPanel center = new JPanel(new GridLayout(1, 2, 12, 0));
        center.setOpaque(false);

        JPanel leftCol = new JPanel(new GridLayout(3, 1, 0, 8));
        leftCol.setOpaque(false);
        JPanel rightCol = new JPanel(new GridLayout(3, 1, 0, 8));
        rightCol.setOpaque(false);

        // fill columns from OPS / combos / previews (re-using rowLabels)
        for (int i = 0; i < OPS.length; i++) {
            JPanel row = makeRowPanel(rowLabels.get(i), combos.get(i), previews.get(i));
            if (i < 3) leftCol.add(row);
            else rightCol.add(row);
        }

        center.add(leftCol);
        center.add(rightCol);

        // --- CENTER WRAP: use BoxLayout with vertical glue to center the 'center' panel ---
        JPanel centerWrap = new JPanel();
        centerWrap.setOpaque(false);
        centerWrap.setBorder(new EmptyBorder(8, 4, 4, 4));
        centerWrap.setLayout(new BoxLayout(centerWrap, BoxLayout.Y_AXIS));

        // center panel should be horizontally centered too
        center.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerWrap.add(Box.createVerticalGlue()); // push down
        centerWrap.add(center);                   // the actual content
        centerWrap.add(Box.createVerticalGlue()); // push up

        add(centerWrap, BorderLayout.CENTER);
    }

    // compact row: label (fixed width), combo (fills), preview
    private JPanel makeRowPanel(JLabel lbl, JComboBox<String> combo, JLabel preview) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;

        // label: left-aligned text but fixed width so combo X aligns
        Dimension pref = lbl.getPreferredSize();
        lbl.setPreferredSize(new Dimension(maxLabelWidth, pref.height));

        gbc.gridx = 0;
        gbc.weightx = 0;
        row.add(lbl, gbc);

        // combo (fills remaining horizontal space)
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        row.add(combo, gbc);

        // preview
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        row.add(preview, gbc);

        return row;
    }

    private static JLabel colorPreview() {
        JLabel l = new JLabel();
        l.setOpaque(true);
        l.setPreferredSize(new Dimension(22, 16));
        l.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        return l;
    }

    // -----------------------
    // Wiring & initialization
    // -----------------------
    private void initListenersAndState() {
        for (int i = 0; i < OPS.length; i++) {
            JComboBox<String> combo = combos.get(i);
            JLabel preview = previews.get(i);
            String operationKey = OPS[i][1];

            combo.addActionListener(e -> {
                String selected = (String) combo.getSelectedItem();
                if (selected == null) return;
                Color c = Helper.getColorFromName(selected.toLowerCase());
                Helper.setOperationColor(operationKey, c);
                preview.setBackground(c);
                preview.repaint();
            });
        }

        loadCurrentSelections();
    }

    private void loadCurrentSelections() {
        for (int i = 0; i < OPS.length; i++) {
            Color curr = currentColorForOperation(OPS[i][1]);
            setComboSelection(combos.get(i), previews.get(i), curr);
        }
    }

    private void setComboSelection(JComboBox<String> combo, JLabel preview, Color color) {
        String name = colorNameForColor(color);
        if (name == null) name = "black";
        combo.setSelectedItem(Helper.capitalize(name));
        preview.setBackground(color);
    }

    private Color currentColorForOperation(String op) {
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

    // -----------------------
    // Helpers
    // -----------------------
    private void computeMaxLabelWidth() {
        // create temporary labels using the panel's font to compute correct widths
        int max = 0;
        Font font = getFont(); // inherited font
        for (JLabel lbl : rowLabels) {
            JLabel tmp = new JLabel(lbl.getText());
            tmp.setFont(font);
            Dimension d = tmp.getPreferredSize();
            if (d.width > max) max = d.width;
        }
        // add small padding so text doesn't touch combo
        max += 8;
        maxLabelWidth = max;
    }

    private String colorNameForColor(Color color) {
        if (color == null) return null;
        for (Map.Entry<String, Color> e : colorMap.entrySet()) {
            if (e.getValue().equals(color)) return e.getKey();
        }
        return null;
    }

    private void onReset(ActionEvent e) {
        // reset all mutable fields
        OperationColor.DELETED = OperationColor.DEF_DELETED;
        OperationColor.ADDED = OperationColor.DEF_ADDED;
        OperationColor.FONT_NAME = OperationColor.DEF_FONT_NAME;
        OperationColor.FONT_SIZE = OperationColor.DEF_FONT_SIZE;
        OperationColor.FONT_STYLE = OperationColor.DEF_FONT_STYLE;
        OperationColor.MULTIPLE = OperationColor.DEF_MULTIPLE;

        loadCurrentSelections();
    }

    // Equalize combo widths; keeps previous approach but accepts a list
    @SafeVarargs
    private void equalizeComboWidths(JComboBox<String>... combosArr) {
        int maxWidth = 0;
        int prefHeight = 0;
        for (JComboBox<String> c : combosArr) {
            Dimension p = c.getPreferredSize();
            if (p.width > maxWidth) maxWidth = p.width;
            if (p.height > prefHeight) prefHeight = p.height;
        }
        maxWidth += 8;
        Dimension uniform = new Dimension(maxWidth, prefHeight);
        for (JComboBox<String> c : combosArr) {
            c.setPreferredSize(uniform);
            c.setMaximumSize(uniform);
            c.setMinimumSize(uniform);
        }
    }

    private String getLongestCapitalized(String[] arr) {
        String longest = "";
        for (String s : arr) {
            String cap = Helper.capitalize(s);
            if (cap.length() > longest.length()) longest = cap;
        }
        return longest;
    }

    // -----------------------
    // Theming
    // -----------------------
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> applyTheme(ThemeManager.getTheme()));
    }

    private void applyTheme(Theme t) {
        if (t == null) return;
        setBackground(t.bodyBg);

        // Use explicit theme tokens:
        // - title uses headerText
        // - row labels and combo text use bodyText
        Color titleColor = t.usernameAccent != null ? t.usernameAccent : Theme.readableForeground(t.bodyBg);
        Color labelColor = t.headerText != null ? t.headerText : Theme.readableForeground(t.bodyBg);

        // theme title
        titleLabel.setForeground(titleColor);

        // theme row labels
        for (JLabel lbl : rowLabels) lbl.setForeground(labelColor);

        // theme combos, buttons and popup renderer
        for (JComboBox<String> combo : combos) {
            combo.setBackground(t.bodyBg);
            combo.setForeground(t.bodyText);
            combo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setBackground(isSelected ? t.startButtonColor : t.bodyBg);
                    setForeground(isSelected ? Theme.readableForeground(t.startButtonColor) : labelColor);
                    return this;
                }
            });
        }

        // theme reset button (startButtonColor)
        for (Component c : getComponents()) {
            if (c instanceof JPanel) {
                for (Component cc : ((Container) c).getComponents()) {
                    if (cc instanceof JButton b) {
                        b.setBackground(t.startButtonColor);
                        b.setForeground(Theme.readableForeground(t.startButtonColor));
                    }
                }
            }
        }

        // previews: keep them as they represent chosen color; give them subtle border color adaptation
        Color previewBorder = Theme.readableForeground(t.bodyBg).equals(Color.WHITE)
                ? new Color(0x3A3A3A) : Color.DARK_GRAY;
        for (JLabel p : previews) p.setBorder(BorderFactory.createLineBorder(previewBorder, 1));

        repaint();
    }

    @Override
    public void removeNotify() {
        ThemeManager.unregister(this);
        super.removeNotify();
    }
}
