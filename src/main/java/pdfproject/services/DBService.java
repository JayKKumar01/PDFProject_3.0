package pdfproject.services;

import pdfproject.models.UserTimeRecord;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Centralized time & persistence policy.
 * Currently mimics saving records in UTC.
 */
public final class DBService {

    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

    // Represents one app/session run
    private static String sessionId;

    // ❌ No instances
    private DBService() {}

    /**
     * Initialize a new session (called once on app start).
     */
    public static void init(UserTimeRecord record) {

        if (record == null) {
            throw new IllegalArgumentException("UserTimeRecord cannot be null");
        }

        sessionId = UUID.randomUUID().toString();

        LocalDateTime startedAtUtc =
                record.getTimestamp()
                        .atZone(SYSTEM_ZONE)
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .toLocalDateTime();

        // ---- FUTURE DATABASE SAVE POINT ----
        // INSERT INTO user_session
        // (session_id, username, timezone, started_at_utc)
        // -----------------------------------

        System.out.println("DB SAVE (SESSION INIT)");
        System.out.println("Session ID : " + sessionId);
        System.out.println("Username   : " + record.getUsername());
        System.out.println("Started UTC: " + startedAtUtc);
        System.out.println("Timezone   : " + SYSTEM_ZONE);
    }

    /**
     * Save validation data (privacy-safe).
     */
    public static void saveValidationData(
            String docType1,
            String docType2,
            boolean isProdigyValidation,
            boolean isTotalSuccess
    ) {

        if (sessionId == null) {
            throw new IllegalStateException("DBService not initialized");
        }

        LocalDateTime validatedAtUtc =
                LocalDateTime.now()
                        .atZone(SYSTEM_ZONE)
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .toLocalDateTime();

        // ---- FUTURE DATABASE SAVE POINT ----
        // INSERT INTO validation_run
        // (session_id, validated_at_utc, doc_type_1, doc_type_2,
        //  prodigy_used, total_success)
        // -----------------------------------

        System.out.println("DB SAVE (VALIDATION DATA)");
        System.out.println("Session ID    : " + sessionId);
        System.out.println("Validated UTC : " + validatedAtUtc);
        System.out.println("Doc Type 1    : " + docType1);
        System.out.println("Doc Type 2    : " + docType2);
        System.out.println("Prodigy used  : " + isProdigyValidation);
        System.out.println("Total success : " + isTotalSuccess);
    }
}
