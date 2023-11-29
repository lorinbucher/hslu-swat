package ch.hslu.swda.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the article return DTO.
 */
class ArticleReturnDTOTest {

    @Test
    void testToJsonString() {
        ArticleOrderDTO orderDTO = new ArticleOrderDTO(1L, "Test", new BigDecimal("50.25"), null);
        ArticleReturnDTO responseDTO = new ArticleReturnDTO(1L, 1L, List.of(orderDTO), List.of("Error"));
        String start = "{\"branchId\":1,\"orderNumber\":1,\"articles\":[{";
        String end = "}],\"error\":[\"Error\"]}";
        try {
            String json = new ObjectMapper().writeValueAsString(responseDTO);
            assertThat(json).startsWith(start);
            assertThat(json).endsWith(end);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }
}
