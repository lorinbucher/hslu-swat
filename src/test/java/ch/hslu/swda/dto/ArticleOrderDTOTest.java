package ch.hslu.swda.dto;

import ch.hslu.swda.entities.DeliveryArticleStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the article order DTO.
 */
class ArticleOrderDTOTest {

    @Test
    void testToJsonString() {
        ArticleOrderDTO dto = new ArticleOrderDTO(100001L, "Test",
                new BigDecimal("50.25"), 1, DeliveryArticleStatus.ADD);
        String json = "{\"articleId\":100001,\"name\":\"Test\",\"price\":50.25,\"quantity\":1,\"action\":\"ADD\"}";
        try {
            assertThat(new ObjectMapper().writeValueAsString(dto)).isEqualTo(json);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }

    @Test
    void testToJsonStringQuantityNull() {
        ArticleOrderDTO dto = new ArticleOrderDTO(100001L, "Test",
                new BigDecimal("50.25"), null, DeliveryArticleStatus.ADD);
        String json = "{\"articleId\":100001,\"name\":\"Test\",\"price\":50.25,\"action\":\"ADD\"}";
        try {
            assertThat(new ObjectMapper().writeValueAsString(dto)).isEqualTo(json);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }

    @Test
    void testToJsonStringActionNull() {
        ArticleOrderDTO dto = new ArticleOrderDTO(100001L, "Test", new BigDecimal("50.25"), 1, null);
        String json = "{\"articleId\":100001,\"name\":\"Test\",\"price\":50.25,\"quantity\":1}";
        try {
            assertThat(new ObjectMapper().writeValueAsString(dto)).isEqualTo(json);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }
}
