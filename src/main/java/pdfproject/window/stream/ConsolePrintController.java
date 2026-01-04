package pdfproject.window.stream;

import java.util.concurrent.atomic.AtomicBoolean;

public final class ConsolePrintController {

    private static final AtomicBoolean PAUSED = new AtomicBoolean(false);

    private ConsolePrintController() {}

    public static void pause() {
        PAUSED.set(true);
    }

    public static void resume() {
        PAUSED.set(false);
    }

    public static boolean isPaused() {
        return PAUSED.get();
    }
}
