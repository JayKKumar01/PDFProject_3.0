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

    public static void saveOutputPath(String path){
        Properties props = new Properties();
        props.setProperty(OUTPUT_PATH_KEY,path);
        try(FileOutputStream out = new FileOutputStream(SETTING_FILE)){
            props.store(out, "App Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadOutputPath(String defaultPath) {
        Properties props = new Properties();

        try (FileInputStream in = new FileInputStream(SETTING_FILE)) {
            props.load(in);
            return props.getProperty(OUTPUT_PATH_KEY, defaultPath);

        } catch (IOException e) {
            new File(defaultPath).mkdirs();
            // File may not exist yet — return default
            return defaultPath;
        }
    }
}
