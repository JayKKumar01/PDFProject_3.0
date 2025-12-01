package pdfproject;

import pdfproject.constants.AppPaths;
import pdfproject.constants.FileTypes;
import pdfproject.core.PDFProcessor;
import pdfproject.interfaces.StopListener;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.utils.DataMapGenerator;
import pdfproject.utils.InputDataProvider;
import pdfproject.utils.ProcessUtils;

import java.util.ArrayList;
import java.util.List;

public class Launcher {

    public static void start(StopListener stopListener) {
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
            if (stopListener.stoppedByUser()){
                return;
            }
            InputData data = inputs.get(i);
            MapModel result = new MapModel(Config.outputImagePath);
            result.setKey(data.getKey());

            try {
                PDFProcessor.processRow(stopListener,data, i, result);
            } catch (Exception e) {
                System.err.printf("Error in item %d: %s%n", i + 1, e.getMessage());
                e.printStackTrace();
            } finally {
                resultList.add(result);
            }
        }
        if (stopListener.stoppedByUser()){
            return;
        }
        DataMapGenerator.generateDataMapJs(resultList, Config.outputImagePath);
    }

    private static boolean isPdf(String path) {
        return path != null && path.toLowerCase().endsWith(FileTypes.PDF_EXTENSION);
    }
}
