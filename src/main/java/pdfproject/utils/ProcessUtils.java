package pdfproject.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessUtils {
    public static boolean isWordRunning() {
        try {
            Process process = Runtime.getRuntime().exec("tasklist");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("WINWORD.EXE")) {
                        return true;
                    }
                }
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
