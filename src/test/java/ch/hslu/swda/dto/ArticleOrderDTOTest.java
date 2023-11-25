package ch.hslu.swda.dto;

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
        ArticleOrderDTO dto = new ArticleOrderDTO(1L, "Test", new BigDecimal("50.25"));
        String json = "{\"articleId\":1,\"name\":\"Test\",\"price\":50.25}";
        try {
            assertThat(new ObjectMapper().writeValueAsString(dto)).isEqualTo(json);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }
}
