package pdfproject.utils;

import org.json.JSONObject;
import org.json.JSONTokener;
import pdfproject.Config;
import pdfproject.models.InputData;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JSON-based provider for InputData.
 * Reads from Config.inputPath and expects exact property names:
 *   Source, Destination, FormPageRange#1, FormPageRange#2
 * Optional boolean/text property: Multiple
 * Top-level JSON must be an object whose property names are required non-empty keys.
 */
public class JsonDataProvider {

    private static final Logger LOGGER = Logger.getLogger(JsonDataProvider.class.getName());

    public static List<InputData> load() {
        List<InputData> list = new ArrayList<>();

        if (Config.inputPath == null || Config.inputPath.isBlank()) {
            LOGGER.severe("Config.inputPath is not set.");
            return list;
        }

        try (FileInputStream fis = new FileInputStream(Config.inputPath)) {

            JSONObject root = new JSONObject(new JSONTokener(fis));

            Iterator<String> keys = root.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                // enforce non-empty keys
                if (key == null || key.isBlank()) {
                    LOGGER.fine("Skipping entry with empty key.");
                    continue;
                }

                Object raw = root.opt(key);
                if (!(raw instanceof JSONObject node)) {
                    LOGGER.fine("Skipping key '" + key + "' because its value is not an object.");
                    continue;
                }

                // exact property names
                String source = text(node, "Source");
                String dest   = text(node, "Destination");

                if (source == null || dest == null) {
                    LOGGER.fine("Skipping key '" + key + "' due to missing Source/Destination.");
                    continue;
                }

                String range1 = text(node, "FormPageRange#1");
                String range2 = text(node, "FormPageRange#2");

                Boolean multiple = booleanValue(node); // optional

                InputData input = new InputData(source, dest, range1, range2);
                input.setKey(key); // guaranteed non-empty

                if (multiple != null) {
                    // In Excel provider "Yes" meant multi-column; InputData stores singleColumn
                    input.setSingleColumn(!multiple);
                }

                list.add(input);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to parse JSON at: " + Config.inputPath, e);
        } catch (Exception e) {
            // org.json throws RuntimeExceptions for malformed JSON
            LOGGER.log(Level.SEVERE, "Invalid JSON format at: " + Config.inputPath, e);
        }

        return list;
    }

    // -------- helper methods for exact names --------

    private static String text(JSONObject node, String name) {
        if (!node.has(name)) return null;

        Object v = node.opt(name);
        if (v instanceof String s) {
            s = s.trim();
            return s.isEmpty() ? null : s;
        }

        // match Jackson's asText() behavior
        if (v != null && !(v instanceof JSONObject)) {
            String s = String.valueOf(v).trim();
            return s.isEmpty() ? null : s;
        }

        return null;
    }

    private static Boolean booleanValue(JSONObject node) {
        if (!node.has("Multiple")) return null;

        Object v = node.opt("Multiple");
        if (v == null) return null;

        if (v instanceof Boolean b) return b;

        if (v instanceof Number n) {
            return n.intValue() != 0;
        }

        if (v instanceof String s) {
            String t = s.trim().toLowerCase();
            return switch (t) {
                case "" -> null;
                case "yes", "true", "1" -> true;
                case "no", "false", "0" -> false;
                default ->
                    // unknown non-empty textual -> treat as true (conservative)
                        true;
            };
        }

        return null;
    }
}
