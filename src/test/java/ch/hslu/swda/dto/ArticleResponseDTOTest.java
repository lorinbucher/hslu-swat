package ch.hslu.swda.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the article response DTO.
 */
class ArticleResponseDTOTest {

    @Test
    void testToJsonString() {
        ArticleOrderDTO orderDTO = new ArticleOrderDTO(1L, "Test", new BigDecimal("50.25"));
        ArticleResponseDTO responseDTO = new ArticleResponseDTO(1L, 1L, List.of(orderDTO), List.of("Error"));
        String start = "{\"orderNumber\":1,\"branchId\":1,\"articles\":[{";
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
