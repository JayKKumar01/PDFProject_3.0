package pdfproject;

import pdfproject.constants.ConsoleMessages;
import pdfproject.constants.FileTypes;
import pdfproject.core.PDFProcessor;
import pdfproject.interfaces.LauncherListener;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.utils.InputDataProvider;
import pdfproject.utils.ProcessUtils;

import java.util.List;

/**
 * Entry point of the PDF Project application.
 * <p>
 * Handles lifecycle management, input loading, Word process check,
 * PDF validation, and initiates the core PDF processing flow.
 * </p>
 */
public class Launcher {

    /**
     * The main entry point of the application.
     * Initializes the launcher with default start/finish hooks.
     *
     * @param args Command-line arguments (unused)
     */
    public static void main(String[] args) {
        start(new LauncherListener() {
            @Override
            public void onStart() {
                // Optional: Pre-processing hook (e.g., logging, loading indicators)
            }

            @Override
            public void onFinish() {
                // Optional: Post-processing hook (e.g., cleanup, status update)
            }
        });
    }

    /**
     * Launches the input processing workflow. Steps include:
     * <ul>
     *     <li>Calling the onStart lifecycle hook</li>
     *     <li>Loading input data from Excel via {@link InputDataProvider}</li>
     *     <li>Validating that all input paths are PDFs</li>
     *     <li>Checking if MS Word is running when Word files are detected</li>
     *     <li>Processing each row via {@link PDFProcessor}</li>
     *     <li>Calling the onFinish lifecycle hook</li>
     * </ul>
     *
     * @param launcherListener Lifecycle listener for pre/post event hooks
     */
    public static void start(LauncherListener launcherListener) {
        launcherListener.onStart();

        // Load data from configured Excel input path
        List<InputData> inputs = InputDataProvider.load();

        // If no input rows found, stop processing
        if (inputs == null || inputs.isEmpty()) {
            System.out.println(ConsoleMessages.NO_INPUT_DATA);
            return;
        }

        // Ensure all input files are PDFs
        boolean allPathsArePDF = inputs.stream().allMatch(input ->
                isPdf(input.getPath1()) && isPdf(input.getPath2())
        );

        // If non-PDFs exist and MS Word is open, abort for safety
        if (!allPathsArePDF && ProcessUtils.isWordRunning()) {
            System.out.println(ConsoleMessages.WORD_RUNNING_ERROR);
            return;
        }

        // Print success message with row count
        System.out.println(ConsoleMessages.LOADED_ROWS_PREFIX + inputs.size() + ConsoleMessages.LOADED_ROWS_SUFFIX);

        // Process all input records via the core engine
        List<MapModel> mapModels = new PDFProcessor().processAll(inputs);

        launcherListener.onFinish();
    }

    /**
     * Utility method to check if a given file path ends with a ".pdf" extension.
     *
     * @param path Absolute or relative file path
     * @return true if the path ends with ".pdf" (case-insensitive), false otherwise
     */
    private static boolean isPdf(String path) {
        return path != null && path.toLowerCase().endsWith(FileTypes.PDF_EXTENSION);
    }
}
