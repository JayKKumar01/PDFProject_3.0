package pdfproject;

import pdfproject.core.PDFProcessor;
import pdfproject.interfaces.LauncherListener;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.utils.InputDataProvider;
import pdfproject.utils.ProcessUtils;

import java.util.List;

public class Launcher {
    public static void main(String[] args) {
        start(new LauncherListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }
        });
    }

    public static void start(LauncherListener launcherListener) {

        launcherListener.onStart();


        List<InputData> inputs = InputDataProvider.load();
        if (inputs == null || inputs.isEmpty()) {
            System.out.println("❌ No input data found. Please check the input source.");
            return;
        }
        // 👇 Check if all paths are PDFs
        boolean allPathsArePDF = inputs.stream().allMatch(input ->
                isPdf(input.getPath1()) && isPdf(input.getPath2())
        );

        if (!allPathsArePDF && ProcessUtils.isWordRunning()) {
            System.out.println("⚠️ MS Word is currently running. Please close it and try again.");
            return;
        }

        // here input data has two paths check if all paths are pdf store true otherwise false
        // getPath1 and getPath2


        System.out.println("✅ Loaded " + inputs.size() + " input rows.");
        List<MapModel> mapModel = new PDFProcessor().processAll(inputs);

        launcherListener.onFinish();


    }

    private static boolean isPdf(String path) {
        return path != null && path.toLowerCase().endsWith(".pdf");
    }
}
