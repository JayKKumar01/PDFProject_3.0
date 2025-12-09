package pdfproject.window.utils;

import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

/**
 * Small DPI / UI scale helper.
 *
 * Usage:
 *   // initialize early (Window3 does this)
 *   UiScale.initFromGraphicsConfig(frame.getGraphicsConfiguration());
 *
 *   // get scale factor (1.0 = default baseline)
 *   float s = UiScale.getScale();
 *
 *   // get a font scaled from a base font
 *   Font scaled = UiScale.getScaledFont(new Font("Segoe UI", Font.BOLD, 16));
 *
 * Baseline: 96 DPI (common Windows logical DPI). If your target baseline differs, change BASELINE_DPI.
 */
public final class UiScale {
    private UiScale() {}

    // baseline DPI we treat as scale 1.0 (common default)
    private static final float BASELINE_DPI = 96f;

    // computed scale factor
    private static volatile float scale = 1.0f;

    /** Initialize scale from a GraphicsConfiguration (recommended). */
    public static void initFromGraphicsConfig(GraphicsConfiguration gc) {
        float dpi = getDpiFromGraphicsConfig(gc);
        setScale(dpi / BASELINE_DPI);
    }

    /** Initialize from default screen resolution (fallback). */
    public static void initFromDefaultScreen() {
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        setScale((float) dpi / BASELINE_DPI);
    }

    private static float getDpiFromGraphicsConfig(GraphicsConfiguration gc) {
        if (gc == null) return Toolkit.getDefaultToolkit().getScreenResolution();
        try {
            // Modern JDKs expose getDefaultTransform on GraphicsConfiguration
            // but to keep compatibility we derive DPI via Toolkit as fallback.
            // If transform scale is available we could multiply transforms, but Toolkit dpi is fine.
            return Toolkit.getDefaultToolkit().getScreenResolution();
        } catch (Throwable t) {
            return Toolkit.getDefaultToolkit().getScreenResolution();
        }
    }

    private static void setScale(float s) {
        if (s <= 0) s = 1.0f;
        scale = Math.max(0.75f, Math.min(3.0f, s)); // clamp to reasonable range
    }

    public static float getScale() {
        return scale;
    }

    /** Return an integer scaled size (rounded). */
    public static int scaleInt(int base) {
        return Math.round(base * scale);
    }

    /** Return a Font scaled from a base font (size multiplied by scale). */
    public static Font getScaledFont(Font base) {
        if (base == null) return null;
        float sz = base.getSize2D() * scale;
        return base.deriveFont(base.getStyle(), Math.max(8f, sz));
    }
}
