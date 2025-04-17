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

        if (ProcessUtils.isWordRunning()) {
            System.out.println("⚠️ MS Word is currently running. Please close it and try again.");
            return;
        }

        launcherListener.onStart();

        List<InputData> inputs = InputDataProvider.load();
        if (inputs == null || inputs.isEmpty()) {
            System.out.println("❌ No input data found. Please check the input source.");
            return;
        }
        System.out.println("✅ Loaded " + inputs.size() + " input rows.");
        List<MapModel> mapModel = new PDFProcessor().processAll(inputs);

        launcherListener.onFinish();
    }
}
