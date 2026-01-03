package pdfproject.utils;

import pdfproject.constants.AppPaths;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppSettings {
    private static final String SETTING_FILE = AppPaths.APP_HOME + "\\app_settings.properties";
    private static final String OUTPUT_PATH_KEY = "outputImagesPath";
    private static final String THEME_DARK_KEY = "themeDark";
    private static final String PRODIGY_VALIDATION_KEY = "prodigyValidation";
    private static final String IMAGE_QUALITY_KEY = "imageQualityIndex";


    public static void saveOutputPath(String path){
        Properties props = loadAll();
        props.setProperty(OUTPUT_PATH_KEY, path);
        store(props);
    }

    public static String loadOutputPath(String defaultPath) {
        Properties props = loadAll();
        String val = props.getProperty(OUTPUT_PATH_KEY);
        if (val == null) {
            // ensure directory exists
            new File(defaultPath).mkdirs();
            return defaultPath;
        }
        return val;
    }

    /**
     * Save theme flag. true => dark theme, false => light theme
     */
    public static void saveTheme(boolean dark) {
        Properties props = loadAll();
        props.setProperty(THEME_DARK_KEY, Boolean.toString(dark));
        store(props);
    }

    /**
     * Load theme flag. If missing or unreadable, returns defaultValue.
     */
    public static boolean loadTheme(boolean defaultValue) {
        Properties props = loadAll();
        String v = props.getProperty(THEME_DARK_KEY);
        if (v == null) return defaultValue;
        return Boolean.parseBoolean(v);
    }

    // --- helpers to read/write the same properties file ---

    private static Properties loadAll() {
        Properties props = new Properties();
        File f = new File(SETTING_FILE);
        if (!f.exists()) return props;
        try (FileInputStream in = new FileInputStream(f)) {
            props.load(in);
        } catch (IOException e) {
            // silently ignore and return empty props
        }
        return props;
    }

    private static void store(Properties props) {
        try {
            File parent = new File(SETTING_FILE).getParentFile();
            if (parent != null) parent.mkdirs();
            try (FileOutputStream out = new FileOutputStream(SETTING_FILE)) {
                props.store(out, "App Settings");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save prodigy validation toggle state
     */
    public static void saveProdigyValidation(boolean enabled) {
        Properties props = loadAll();
        props.setProperty(PRODIGY_VALIDATION_KEY, Boolean.toString(enabled));
        store(props);
    }

    /**
     * Load prodigy validation toggle state.
     * Defaults to false if missing.
     */
    public static boolean loadProdigyValidation(boolean defaultValue) {
        Properties props = loadAll();
        String v = props.getProperty(PRODIGY_VALIDATION_KEY);
        if (v == null) return defaultValue;
        return Boolean.parseBoolean(v);
    }


    /**
     * Save selected image quality index (0 = Low, 1 = Medium, 2 = High)
     */
    public static void saveImageQualityIndex(int index) {
        Properties props = loadAll();
        props.setProperty(IMAGE_QUALITY_KEY, Integer.toString(index));
        store(props);
    }

    /**
     * Load selected image quality index.
     * Defaults to provided value if missing or invalid.
     */
    public static int loadImageQualityIndex(int defaultIndex) {
        Properties props = loadAll();
        String v = props.getProperty(IMAGE_QUALITY_KEY);
        if (v == null) return defaultIndex;
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return defaultIndex;
        }
    }

}
