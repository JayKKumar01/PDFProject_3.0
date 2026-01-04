package pdfproject.services;

import pdfproject.models.UserTimeRecord;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Centralized time & persistence policy.
 * Currently mimics saving records in UTC.
 */
public class UserTimeRecordService {

    /**
     * Ensures the record is stored using UTC time.
     * Actual database logic will be added later.
     */
    public void save(UserTimeRecord record) {

        // Convert timestamp to UTC before saving
        LocalDateTime utcTimestamp =
                record.getTimestamp()
                        .atZone(ZoneOffset.systemDefault())
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .toLocalDateTime();

        UserTimeRecord utcRecord =
                new UserTimeRecord(record.getUsername(), utcTimestamp);

        // ---- FUTURE DATABASE SAVE POINT ----
        // JDBC / JPA / File / Cloud storage
        // -----------------------------------

        // Temporary visibility (safe to remove later)
        System.out.println("Saved (UTC): " + utcRecord.getTimestamp());
    }
}
