package pdfproject.parsers;

import java.util.*;
import java.util.regex.*;

public class RangeParser {
    private static final Pattern VALID_PATTERN = Pattern.compile("^\\s*\\d+\\s*(-\\s*\\d+\\s*)?$");

    public static List<Integer> parse(String range, int totalPages) throws IllegalArgumentException {
        List<Integer> result = new ArrayList<>();
        String[] parts = range.split(",");

        for (String part : parts) {
            part = part.trim();
            if (!VALID_PATTERN.matcher(part).matches())
                throw new IllegalArgumentException("Invalid range part: '" + part + "'");

            if (part.contains("-")) {
                String[] bounds = part.split("-");
                int start = Integer.parseInt(bounds[0].trim());
                int end = Integer.parseInt(bounds[1].trim());
                if (start > end) throw new IllegalArgumentException("Reversed range: " + part);
                if (start < 1 || end > totalPages) throw new IllegalArgumentException("Out of bounds: " + part);
                for (int i = start; i <= end; i++) result.add(i);
            } else {
                int page = Integer.parseInt(part);
                if (page < 1 || page > totalPages) throw new IllegalArgumentException("Out of bounds: " + page);
                result.add(page);
            }
        }

        return result;
    }
}

