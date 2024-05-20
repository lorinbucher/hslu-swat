package ch.hslu.swda.dto;

import java.time.LocalDateTime;

/**
 * A log event message.
 *
 * @param branchId ID of the branch.
 * @param type     Message type.
 * @param message  Message.
 * @param datetime Message time.
 */
public record LogEventDTO(long branchId, String type, String message, String datetime) {
    public LogEventDTO(final long branchId, final String type, final String message) {
        this(branchId, type, message, LocalDateTime.now().toString());
    }
}
