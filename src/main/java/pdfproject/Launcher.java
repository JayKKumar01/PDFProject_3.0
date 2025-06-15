package pdfproject;

import pdfproject.constants.AppPaths;
import pdfproject.constants.FileTypes;
import pdfproject.core.PDFProcessor;
import pdfproject.interfaces.LauncherListener;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.utils.DataMapGenerator;
import pdfproject.utils.InputDataProvider;
import pdfproject.utils.ProcessUtils;

import java.util.ArrayList;
import java.util.List;

public class Launcher {

    public static void main(String[] args) {
//        start(null);
    }

    public static void start(LauncherListener launcherListener) {
        List<InputData> inputs = InputDataProvider.load();

        if (inputs == null || inputs.isEmpty()) {
            System.out.println("❌ No input data found.");
            return;
        }

        boolean allPdf = inputs.stream()
                .allMatch(input -> isPdf(input.getPath1()) && isPdf(input.getPath2()));

        if (!allPdf && ProcessUtils.isWordRunning()) {
            System.out.println("⚠️ MS Word is running. Please close it and try again.");
            return;
        }

        System.out.println("✅ Loaded " + inputs.size() + " input rows.");
        Config.outputImagePath = AppPaths.OUTPUT_IMAGES_BASE + "\\Result - " + System.currentTimeMillis();
        List<MapModel> resultList = new ArrayList<>();

        for (int i = 0; i < inputs.size(); i++) {
            if (launcherListener.stoppedByUser()){
                return;
            }
            InputData data = inputs.get(i);
            MapModel result = new MapModel(Config.outputImagePath);
            result.setKey(data.getKey());

            try {
                PDFProcessor.processRow(data, i, result);
            } catch (Exception e) {
                System.err.printf("Error in item %d: %s%n", i + 1, e.getMessage());
                e.printStackTrace();
            } finally {
                resultList.add(result);
            }
        }

        DataMapGenerator.generateDataMapJs(resultList, Config.outputImagePath);
    }

    private static boolean isPdf(String path) {
        return path != null && path.toLowerCase().endsWith(FileTypes.PDF_EXTENSION);
    }
}
