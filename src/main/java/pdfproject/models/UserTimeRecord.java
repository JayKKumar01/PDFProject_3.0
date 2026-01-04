package pdfproject.models;

import java.time.LocalDateTime;

/**
 * Pure data model.
 * No database, no framework assumptions.
 */
public class UserTimeRecord {

    private final String username;
    private final LocalDateTime timestamp;

    public UserTimeRecord(String username, LocalDateTime timestamp) {
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
