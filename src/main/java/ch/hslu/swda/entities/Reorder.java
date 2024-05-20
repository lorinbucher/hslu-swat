package ch.hslu.swda.entities;

import com.mongodb.lang.Nullable;
import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * A reorder from the central warehouse of a branch.
 *
 * @param reorderId ID of the reorder.
 * @param status    Status of the reorder.
 * @param date      Date of the reorder.
 * @param articleId ID of the article.
 * @param quantity  Quantity of the article.
 */
public record Reorder(@Nullable long reorderId, ReorderStatus status, String date, long articleId, int quantity)
        implements Entity<Reorder> {
    public Reorder {
        if (status == null) {
            throw new IllegalArgumentException("status should not be null");
        }
        if (articleId < 100000) {
            throw new IllegalArgumentException("articleId should not be lower than 100000");
        }
        if (articleId > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("articleId should not be higher than " + Integer.MAX_VALUE);
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity should not be lower than 1");
        }

        if (date.isBlank()) {
            date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        parseDateFromString(date);
    }

    /**
     * Creates a reorder from a MongoDB document.
     *
     * @param document MongoDB document.
     */
    public Reorder(final Document document) {
        this(
                document.getLong("reorderId"),
                ReorderStatus.valueOf(document.getString("status")),
                new SimpleDateFormat("yyyy-MM-dd").format(document.getDate("date")),
                document.getLong("articleId"),
                document.getInteger("quantity")
        );
    }

    /**
     * Creates a MongoDB document from a reorder.
     *
     * @return MongoDB document.
     */
    @Override
    public Document toDocument() {
        return new Document()
                .append("reorderId", reorderId)
                .append("status", status.name())
                .append("date", parseDateFromString(date))
                .append("articleId", articleId)
                .append("quantity", quantity);
    }

    /**
     * Reorders are equal if they have the same reorder ID.
     *
     * @param obj The reorder to compare against.
     * @return True if the reorder ID is the same.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof Reorder other
                && this.reorderId == other.reorderId;
    }

    /**
     * Returns the hashcode based on the reorder ID.
     *
     * @return Hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.reorderId);
    }

    /**
     * Parses the date from a string in iso format.
     *
     * @param dateString Date string in iso format.
     */
    private Date parseDateFromString(final String dateString) {
        Date parsedDate;
        try {
            parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("date should match required format yyyy-MM-dd");
        }
        return parsedDate;
    }
}
