package pdfproject.window.experiment.components;

import pdfproject.window.experiment.utils.ValidationCenter;

import javax.swing.*;
import java.awt.*;

/**
 * Simple base panel that wires into ValidationCenter (multi-listener).
 * Responsibilities:
 *  - register/unregister itself in addNotify/removeNotify using ValidationCenter.addListener/removeListener
 *  - provide default onStart/onStop that disables/enables the component tree.
 *
 * Note: This class intentionally does NOT deal with theming or ThemeManager.
 */
public abstract class ValidationAwarePanel extends JPanel implements ValidationCenter.ValidationListener {

    public ValidationAwarePanel(BorderLayout borderLayout) {
        super(borderLayout);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // register this instance with ValidationCenter's multi-listener API
        ValidationCenter.addListener(this);
    }

    @Override
    public void removeNotify() {
        // unregister this instance
        ValidationCenter.removeListener(this);
        super.removeNotify();
    }

    // Default behavior: disable entire subtree on start
    @Override
    public void onStart() {
        // ensure running on EDT
        SwingUtilities.invokeLater(() -> setEnabledRecursively(this, false));
    }

    // Default behavior: enable entire subtree on stop
    @Override
    public void onStop() {
        SwingUtilities.invokeLater(() -> setEnabledRecursively(this, true));
    }

    // Utility: recursively enable/disable all children
    protected void setEnabledRecursively(Component c, boolean enabled) {
        c.setEnabled(enabled);
        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                setEnabledRecursively(child, enabled);
            }
        }
    }
}
