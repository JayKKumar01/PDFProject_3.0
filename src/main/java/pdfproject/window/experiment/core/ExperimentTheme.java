package pdfproject.window.experiment.core;

import pdfproject.window.experiment.constants.ExperimentColors;

import java.awt.Color;

public final class ExperimentTheme {

    public final Color headerBg, headerText;
    public final Color usernameAccent;
    public final Color bodyBg, bodyText;
    public final Color consoleBg, consoleText;

    public ExperimentTheme(Color headerBg, Color headerText, Color usernameAccent,
                           Color bodyBg, Color bodyText,
                           Color consoleBg, Color consoleText) {

        this.headerBg = headerBg;
        this.headerText = headerText;
        this.usernameAccent = usernameAccent;
        this.bodyBg = bodyBg;
        this.bodyText = bodyText;
        this.consoleBg = consoleBg;
        this.consoleText = consoleText;
    }

    public static final ExperimentTheme LIGHT = new ExperimentTheme(
            ExperimentColors.HEADER_BG_LIGHT,
            ExperimentColors.HEADER_TEXT_LIGHT,
            ExperimentColors.USERNAME_ACCENT_LIGHT,

            ExperimentColors.BODY_BG_LIGHT,
            ExperimentColors.BODY_TEXT_LIGHT,

            ExperimentColors.CONSOLE_BG_LIGHT,
            ExperimentColors.CONSOLE_TEXT_LIGHT
    );

    public static final ExperimentTheme DARK = new ExperimentTheme(
            ExperimentColors.HEADER_BG_DARK,
            ExperimentColors.HEADER_TEXT_DARK,
            ExperimentColors.USERNAME_ACCENT_DARK,

            ExperimentColors.BODY_BG_DARK,
            ExperimentColors.BODY_TEXT_DARK,

            ExperimentColors.CONSOLE_BG_DARK,
            ExperimentColors.CONSOLE_TEXT_DARK
    );
}
