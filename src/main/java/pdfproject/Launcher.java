package pdfproject;

import pdfproject.constants.AppPaths;
import pdfproject.constants.FileTypes;
import pdfproject.core.PDFProcessor;
import pdfproject.interfaces.StopListener;
import pdfproject.models.InputData;
import pdfproject.models.MapModel;
import pdfproject.utils.DataMapGenerator;
import pdfproject.utils.InputDataProvider;
import pdfproject.utils.JsonDataProvider;
import pdfproject.utils.ProcessUtils;

import java.util.ArrayList;
import java.util.List;

public class Launcher {

    public static void start(StopListener stopListener) {
        List<InputData> inputs = Config.INPUT_PATH.toLowerCase().endsWith(".json")
        ? JsonDataProvider.load()
                :InputDataProvider.load();

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
        String outputPath = Config.outputImagePath + "\\Result - " + System.currentTimeMillis();
        List<MapModel> resultList = new ArrayList<>();

        for (int i = 0; i < inputs.size(); i++) {
            if (stopListener.stoppedByUser()){
                return;
            }
            InputData data = inputs.get(i);
            MapModel result = new MapModel(outputPath);
            result.setKey(data.getKey());

            try {
                PDFProcessor.processRow(stopListener,data, i, result, outputPath);
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
        DataMapGenerator.generateDataMapJs(resultList, outputPath);
    }

    private static boolean isPdf(String path) {
        return path != null && path.toLowerCase().endsWith(FileTypes.PDF_EXTENSION);
    }
}
