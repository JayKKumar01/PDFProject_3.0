package pdfproject.utils;

import pdfproject.constants.ProcessNames;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Utility class for checking the status of system processes.
 */
public class ProcessUtils {

    /**
     * Checks if Microsoft Word is currently running.
     *
     * @return true if Word is running; false otherwise
     */
    public static boolean isWordRunning() {
        String os = System.getProperty(ProcessNames.OS_NAME_PROPERTY).toLowerCase();

        try {
            if (os.contains(ProcessNames.WINDOWS_OS_INDICATOR)) {
                return isProcessRunning(ProcessNames.TASK_LIST_COMMAND, ProcessNames.WORD_PROCESS_WINDOWS);
            } else if (os.contains(ProcessNames.LINUX_OS_INDICATOR)
                    || os.contains(ProcessNames.UNIX_OS_INDICATOR)
                    || os.contains(ProcessNames.MAC_OS_INDICATOR)) {
                return isProcessRunning(ProcessNames.PS_COMMAND, ProcessNames.WORD_PROCESS_UNIX);
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to check process status: " + e.getMessage());
        }

        return false;
    }

    /**
     * Attempts to forcefully terminate Microsoft Word if it's running.
     */
    public static void killWordProcess() {
        String os = System.getProperty(ProcessNames.OS_NAME_PROPERTY).toLowerCase();

        try {
            if (os.contains(ProcessNames.WINDOWS_OS_INDICATOR)) {
                Process process = Runtime.getRuntime().exec(new String[]{"taskkill", "/F", "/IM", ProcessNames.WORD_PROCESS_WINDOWS});
                process.waitFor(); // Optional: wait for the process to finish
                System.out.println("🛑 Microsoft Word was terminated if it was running.");
            } else {
                System.out.println("⚠️ Word termination is only supported on Windows.");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Failed to kill Microsoft Word process: " + e.getMessage());
        }
    }

    /**
     * Executes a process list command and checks if the specified process is running.
     *
     * @param command     command to list running processes
     * @param processName the process name to search for
     * @return true if the process is found; false otherwise
     */
    private static boolean isProcessRunning(String[] command, String processName) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(builder.start().getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(processName.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }
}
