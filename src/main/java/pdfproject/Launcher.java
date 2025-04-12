package pdfproject;

import pdfproject.core.PDFProcessor;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.utils.InputDataProvider;
import pdfproject.utils.ProcessUtils;

import java.util.List;

public class Launcher {
    public static void main(String[] args) {
        start();
    }

    public static void start() {
        if (ProcessUtils.isWordRunning()) {
            System.out.println("⚠️ MS Word is currently running. Please close it and try again.");
            return;
        }

        List<InputData> inputs = InputDataProvider.load();
        if (inputs == null || inputs.isEmpty()) {
            System.out.println("❌ No input data found. Please check the input source.");
            return;
        }
        System.out.println("✅ Loaded " + inputs.size() + " input rows.");
        MapModel mapModel = new PDFProcessor().processAll(inputs);
    }
}
