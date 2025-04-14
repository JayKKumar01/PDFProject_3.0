package pdfproject.utils;

import pdfproject.constants.ProcessNames;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

public class ProcessUtils {

    public static boolean isWordRunning() {
        String os = System.getProperty(ProcessNames.OS_NAME).toLowerCase();

        try {
            ProcessBuilder processBuilder;
            BufferedReader reader;
            String line;
            boolean isRunning = false;

            if (os.contains("win")) {
                // Windows-specific process checking
                processBuilder = new ProcessBuilder(ProcessNames.TASK_LIST_COMMAND);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().contains(ProcessNames.WORD_PROCESS.toLowerCase())) {
                        isRunning = true;
                        break;
                    }
                }
                process.waitFor();
                reader.close();
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                // Unix/Linux/Mac-specific process checking
                processBuilder = new ProcessBuilder("ps", "-ef");
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().contains(ProcessNames.WORD_PROCESS.toLowerCase())) {
                        isRunning = true;
                        break;
                    }
                }
                process.waitFor();
                reader.close();
            }

            return isRunning;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
