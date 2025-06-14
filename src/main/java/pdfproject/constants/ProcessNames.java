package pdfproject.constants;

/**
 * Constants used for identifying OS types and system process names.
 */
public class ProcessNames {
    public static final String OS_NAME_PROPERTY = "os.name";

    // OS indicators
    public static final String WINDOWS_OS_INDICATOR = "win";
    public static final String UNIX_OS_INDICATOR = "nix";
    public static final String LINUX_OS_INDICATOR = "nux";
    public static final String MAC_OS_INDICATOR = "mac";

    // Microsoft Word process names
    public static final String WORD_PROCESS_WINDOWS = "winword.exe";
    public static final String WORD_PROCESS_UNIX = "Microsoft Word";

    // Commands
    public static final String[] TASK_LIST_COMMAND = {"tasklist"};
    public static final String[] PS_COMMAND = {"ps", "-ef"};
}
