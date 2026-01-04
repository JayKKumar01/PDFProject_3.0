package pdfproject.utils;

import pdfproject.constants.AppPaths;

import java.io.*;
import java.util.Properties;

public final class AppSettings {

    private static final String SETTING_FILE =
            AppPaths.APP_HOME + File.separator + "app_settings.properties";

    private static final String OUTPUT_PATH_KEY = "outputImagesPath";
    private static final String PRODIGY_VALIDATION_KEY = "prodigyValidation";
    private static final String IMAGE_QUALITY_KEY = "imageQualityIndex";

    private static Properties props;

    private AppSettings() {
        // prevent instantiation
    }

    /* =======================
       Core load / save
       ======================= */

    private static synchronized Properties props() {
        if (props == null) {
            props = new Properties();
            File file = new File(SETTING_FILE);
            if (file.exists()) {
                try (FileInputStream in = new FileInputStream(file)) {
                    props.load(in);
                } catch (IOException ignored) {
                }
            }
        }
        return props;
    }

    private static synchronized void save() {
        try {
            File parent = new File(SETTING_FILE).getParentFile();
            if (parent != null) parent.mkdirs();

            try (FileOutputStream out = new FileOutputStream(SETTING_FILE)) {
                props().store(out, "App Settings");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* =======================
       Output Path
       ======================= */

    public static void saveOutputPath(String path) {
        props().setProperty(OUTPUT_PATH_KEY, path);
        save();
    }

    public static String loadOutputPath(String defaultPath) {
        String value = props().getProperty(OUTPUT_PATH_KEY);
        if (value == null || value.isBlank()) {
            new File(defaultPath).mkdirs();
            return defaultPath;
        }
        return value;
    }

    /* =======================
       Prodigy Validation
       ======================= */

    public static void saveProdigyValidation(boolean enabled) {
        props().setProperty(PRODIGY_VALIDATION_KEY, Boolean.toString(enabled));
        save();
    }

    public static boolean loadProdigyValidation(boolean defaultValue) {
        String v = props().getProperty(PRODIGY_VALIDATION_KEY);
        return v == null ? defaultValue : Boolean.parseBoolean(v);
    }

    /* =======================
       Image Quality
       ======================= */

    public static void saveImageQualityIndex(int index) {
        props().setProperty(IMAGE_QUALITY_KEY, Integer.toString(index));
        save();
    }

    public static int loadImageQualityIndex(int defaultIndex) {
        String v = props().getProperty(IMAGE_QUALITY_KEY);
        try {
            return v == null ? defaultIndex : Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return defaultIndex;
        }
    }

    /* =======================
       Operation Colors
       ======================= */

    private static String opColorKey(String op) {
        return "opColor." + op;
    }

    public static void saveOperationColor(String op, String colorName) {
        props().setProperty(opColorKey(op), colorName);
        save();
    }

    public static String loadOperationColor(String op, String defaultColorName) {
        return props().getProperty(opColorKey(op), defaultColorName);
    }
}
