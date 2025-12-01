package pdfproject.constants;

import pdfproject.Config;

public final class AppPaths {
    private AppPaths() {}

    public static final String HOME_DIR = System.getProperty("user.home");
    public static final String DOWNLOAD_DIR = HOME_DIR + "\\Downloads";
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    public static final String TEMP_WORD_PDF = TEMP_DIR + "\\"+Config.FRAME_NAME+"\\wordpdfs";
    public static final String APP_HOME = HOME_DIR + "\\" + Config.FRAME_NAME;
}
