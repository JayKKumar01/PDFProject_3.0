package pdfproject;


import java.awt.*;

import static pdfproject.constants.FilePaths.*;

public class Config{
    public static final String INPUT_PATH = "";
    public static final Object OUTPUT_IMAGES_PATH = DOWNLOAD_DIR + "\\PDFProject\\Images";


    /**
     * Class containing color constants for different operations.
     */
    public static class Colors {
        /**
         * Color constant for deleted operation.
         */
        public static final Color DELETED_OPERATION_COLOR = Color.RED;

        /**
         * Color constant for added operation.
         */
        public static final Color ADDED_OPERATION_COLOR = Color.GREEN;

        /**
         * Color constant for font name operation.
         */
        public static final Color FONT_NAME_OPERATION_COLOR = Color.MAGENTA;

        /**
         * Color constant for font size operation.
         */
        public static final Color FONT_SIZE_OPERATION_COLOR = Color.BLUE;

        /**
         * Color constant for font style operation.
         */
        public static final Color FONT_STYLE_OPERATION_COLOR = Color.CYAN;

        /**
         * Color constant for multiple operations.
         */
        public static final Color MULTIPLE_OPERATION_COLOR = Color.BLACK;
    }
}
