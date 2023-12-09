package ch.hslu.swda.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for the reorder entity.
 */
class ReorderTest {

    @Test
    void testStatusInvalid() {
        assertThatThrownBy(() -> new Reorder(1L, null, "", 100000L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("status should not be null");
    }

    @Test
    void testStatusValid() {
        final Reorder reorder = new Reorder(1L, ReorderStatus.NEW, "", 100000L, 5);
        assertThat(reorder.status()).isEqualTo(ReorderStatus.NEW);
    }

    @Test
    void testDateEmpty() {
        final Reorder reorder = new Reorder(1L, ReorderStatus.NEW, "", 100000L, 5);
        assertThat(reorder.date()).matches("\\d{4}-\\d{2}-\\d{2}");
    }

    @Test
    void testDateInvalid() {
        assertThatThrownBy(() -> new Reorder(1L, ReorderStatus.NEW, "20231129", 100000L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("date should match required format yyyy-MM-dd");
    }

    @Test
    void testDateValid() {
        final Reorder reorder = new Reorder(1L, ReorderStatus.NEW, "2023-11-29", 100000L, 5);
        assertThat(reorder.date()).isEqualTo("2023-11-29");
    }

    @Test
    void testArticleIdInvalidMin() {
        assertThatThrownBy(() -> new Reorder(1L, ReorderStatus.NEW, "", 99999L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("articleId should not be lower than 100000");
    }

    @Test
    void testArticleIdInvalidMax() {
        assertThatThrownBy(() -> new Reorder(1L, ReorderStatus.NEW, "", Integer.MAX_VALUE + 1L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("articleId should not be higher than " + Integer.MAX_VALUE);
    }

    @Test
    void testArticleIdValid() {
        final Reorder reorder = new Reorder(1L, ReorderStatus.NEW, "", 100000L, 5);
        assertThat(reorder.articleId()).isEqualTo(100000L);
    }

    @Test
    void testQuantityInvalid() {
        String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        assertThatThrownBy(() -> new Reorder(1L, ReorderStatus.NEW, "", 100000L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("quantity should not be lower than 1");
    }

    @Test
    void testQuantityValid() {
        String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        final Reorder reorder = new Reorder(1L, ReorderStatus.NEW, "", 100000L, 1);
        assertThat(reorder.quantity()).isEqualTo(1);
    }

    @Test
    void testNotEqual() {
        final Reorder reorder1 = new Reorder(1L, ReorderStatus.NEW, "", 100000L, 1);
        final Reorder reorder2 = new Reorder(2L, ReorderStatus.WAITING, "", 100001L, 2);
        assertThat(reorder1).isNotEqualTo("");
        assertThat(reorder1).isNotEqualTo(reorder2);
    }

    @Test
    void testEqual() {
        final Reorder reorder1 = new Reorder(1L, ReorderStatus.NEW, "", 100000L, 1);
        final Reorder reorder2 = new Reorder(1L, ReorderStatus.WAITING, "", 100001L, 2);
        assertThat(reorder1).isEqualTo(reorder1);
        assertThat(reorder1).isEqualTo(reorder2);
    }

    @Test
    void testHashCodeDiffers() {
        final Reorder reorder1 = new Reorder(1L, ReorderStatus.NEW, "", 100000L, 1);
        final Reorder reorder2 = new Reorder(2L, ReorderStatus.WAITING, "", 100001L, 2);
        assertThat(reorder1).doesNotHaveSameHashCodeAs(reorder2);
    }

    @Test
    void testHashCode() {
        final Reorder reorder1 = new Reorder(1L, ReorderStatus.NEW, "", 100000L, 1);
        final Reorder reorder2 = new Reorder(1L, ReorderStatus.WAITING, "", 100001L, 2);
        assertThat(reorder1).hasSameHashCodeAs(reorder2);
    }

    @Test
    void testJsonObject() {
        String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        final Reorder reorder = new Reorder(1L, ReorderStatus.COMPLETED, date, 100000L, 5);
        String reorderJson = "{\"reorderId\":1,\"status\":\"COMPLETED\",\"date\":\"" + date +
                "\",\"articleId\":100000,\"quantity\":5}";
        try {
            assertThat(new ObjectMapper().writeValueAsString(reorder)).isEqualTo(reorderJson);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }

    @Test
    void testFromDocument() {
        String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Document document = new Document()
                .append("reorderId", 1L)
                .append("status", ReorderStatus.NEW.name())
                .append("date", new Date())
                .append("articleId", 100001L)
                .append("quantity", 5);
        final Reorder reorder = new Reorder(document);
        assertThat(reorder.reorderId()).isEqualTo(1L);
        assertThat(reorder.status()).isEqualTo(ReorderStatus.NEW);
        assertThat(reorder.date()).isEqualTo(date);
        assertThat(reorder.articleId()).isEqualTo(100001L);
        assertThat(reorder.quantity()).isEqualTo(5);
    }

    @Test
    void testToDocument() {
        final Reorder reorder = new Reorder(1L, ReorderStatus.NEW, "", 100001L, 5);
        Document document = reorder.toDocument();
        assertThat(document.getLong("reorderId")).isEqualTo(reorder.reorderId());
        assertThat(document.getString("status")).isEqualTo(reorder.status().name());
        assertThat(document.getDate("date")).isEqualTo(reorder.date());
        assertThat(document.getLong("articleId")).isEqualTo(reorder.articleId());
        assertThat(document.getInteger("quantity")).isEqualTo(reorder.quantity());
    }
}
