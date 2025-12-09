package pdfproject.utils;

import org.apache.commons.math3.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SentenceUtils {

    // Utility method to extract pairs
    public static List<Pair<String, String>> extractPairs(String jsonStr) {
        List<Pair<String, String>> result = new ArrayList<>();

        JSONArray array = new JSONArray(jsonStr);

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            String original = obj.getString("original");
            String corrected = obj.getString("corrected");

            result.add(new Pair<>(original, corrected));
        }

        return result;
    }

    // Reads and returns JSON string from file
    public static String jsonString() {
        String path = "C:\\Users\\jayte\\Downloads\\prodigy_response_sample.json";
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
            return "[]"; // fallback empty JSON array
        }
    }

    // MAIN
    public static void main(String[] args) {

        String jsonStr = jsonString(); // load file content

        List<Pair<String, String>> pairs = extractPairs(jsonStr);

        for (Pair<String, String> p : pairs) {
            System.out.println("Original:  " + p.getFirst());
            System.out.println("Corrected: " + p.getSecond());
            System.out.println("----------------------------------");
        }
    }
}
