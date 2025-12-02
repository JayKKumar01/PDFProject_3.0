package pdfproject.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Reads from Config.INPUT_PATH and expects exact property names:
 *   Source, Destination, FormPageRange#1, FormPageRange#2
 * Optional boolean/text property: Multiple
 * Top-level JSON must be an object whose property names are required non-empty keys.
 */
public class JsonDataProvider {

    private static final Logger LOGGER = Logger.getLogger(JsonDataProvider.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static List<InputData> load() {
        List<InputData> list = new ArrayList<>();

        if (Config.INPUT_PATH == null || Config.INPUT_PATH.isBlank()) {
            LOGGER.severe("Config.INPUT_PATH is not set.");
            return list;
        }

        try (FileInputStream fis = new FileInputStream(Config.INPUT_PATH)) {

            JsonNode root = MAPPER.readTree(fis);

            if (!root.isObject()) {
                LOGGER.severe("Root JSON must be an object.");
                return list;
            }

            Iterator<String> keys = root.fieldNames();

            while (keys.hasNext()) {
                String key = keys.next();

                // enforce non-empty keys
                if (key == null || key.isBlank()) {
                    LOGGER.fine("Skipping entry with empty key.");
                    continue;
                }

                JsonNode node = root.get(key);
                if (node == null || !node.isObject()) {
                    LOGGER.fine("Skipping key '" + key + "' because its value is not an object.");
                    continue;
                }

                // exact property names as requested
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
            LOGGER.log(Level.SEVERE, "Failed to parse JSON at: " + Config.INPUT_PATH, e);
        }

        return list;
    }

    // -------- helper methods for exact names --------

    private static String text(JsonNode node, String name) {
        JsonNode v = node.get(name);
        if (v == null) return null;
        if (v.isValueNode()) {
            String s = v.asText().trim();
            return s.isEmpty() ? null : s;
        }
        return null;
    }

    private static Boolean booleanValue(JsonNode node) {
        JsonNode v = node.get("Multiple");
        if (v == null) return null;

        if (v.isBoolean()) return v.asBoolean();

        if (v.isTextual()) {
            String t = v.asText().trim().toLowerCase();
            return switch (t) {
                case "" -> null;
                case "yes", "true", "1" -> true;
                case "no", "false", "0" -> false;
                default ->
                    // unknown non-empty textual -> treat as true (conservative)
                        true;
            };
        }

        if (v.isNumber()) {
            int n = v.intValue();
            return n != 0;
        }

        return null;
    }
}
