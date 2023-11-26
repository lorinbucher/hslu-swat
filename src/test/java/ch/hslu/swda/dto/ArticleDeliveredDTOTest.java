package ch.hslu.swda.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the article delivered DTO.
 */
class ArticleDeliveredDTOTest {

    @Test
    void testToJsonString() {
        ArticleDeliveredDTO dto = new ArticleDeliveredDTO(1L, 1L);
        String json = "{\"branchId\":1,\"orderNumber\":1}";
        try {
            assertThat(new ObjectMapper().writeValueAsString(dto)).isEqualTo(json);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }
}
