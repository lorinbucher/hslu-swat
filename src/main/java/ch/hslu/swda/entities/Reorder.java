package ch.hslu.swda.entities;

import com.mongodb.lang.Nullable;

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
public record Reorder(@Nullable long reorderId, ReorderStatus status, String date, long articleId, int quantity) {
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
        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("date should match required format yyyy-MM-dd");
        }
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
}
