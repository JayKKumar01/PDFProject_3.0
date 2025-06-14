package pdfproject.constants;

/**
 * Centralized message constants for console output throughout the application.
 * <p>
 * These messages are used to provide consistent and user-friendly feedback
 * in logs or standard output (System.out), especially during application startup,
 * validation, or error reporting.
 * </p>
 */
public class ConsoleMessages {

    /**
     * Message shown when no input data is found in the input source (e.g., Excel).
     */
    public static final String NO_INPUT_DATA = "❌ No input data found. Please check the input source.";

    /**
     * Message shown when Microsoft Word is detected as running and
     * Word documents are being processed.
     */
    public static final String WORD_RUNNING_ERROR = "⚠️ MS Word is currently running. Please close it and try again.";

    /**
     * Prefix shown before displaying the number of loaded input rows.
     * Example: "✅ Loaded 5"
     */
    public static final String LOADED_ROWS_PREFIX = "✅ Loaded ";

    /**
     * Suffix shown after displaying the number of loaded input rows.
     * Example: "5 input rows."
     */
    public static final String LOADED_ROWS_SUFFIX = " input rows.";
    public static final String EXCEL_READ_ERROR = "❌ Failed to read Excel input file: ";
    public static final String PROCESS_CHECK_FAILED = "❌ Failed to check process status: ";
}
