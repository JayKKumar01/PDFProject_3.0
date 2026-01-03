package pdfproject;

import pdfproject.constants.AppPaths;
import pdfproject.utils.AppSettings;

public final class Config {

    private Config() {} // Prevent instantiation

    public static final String FRAME_NAME = "PDF Project";

    public static String inputPath = "";
    public static int renderDpi = 100;
    public static boolean isProdigyValidation;
    public static String outputImagePath = AppSettings.loadOutputPath(AppPaths.HOME_DIR + "\\" + FRAME_NAME + "\\Reports");
}
