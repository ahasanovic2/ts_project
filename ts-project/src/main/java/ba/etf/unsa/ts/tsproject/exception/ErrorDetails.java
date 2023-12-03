package ba.etf.unsa.ts.tsproject.exception;

import java.time.LocalDateTime;

public class ErrorDetails {
    private LocalDateTime timestamp;
    private String field;
    private String message;

    public ErrorDetails(LocalDateTime timestamp, String field, String message) {
        super();
        this.timestamp = timestamp;
        this.field = field;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{" +
                "\"timestamp\":\"" + timestamp.toString() +
                "\", \"field\":\"" + field + '\"' +
                ", \"message\":\"" + message +
                "\"}";
    }
}