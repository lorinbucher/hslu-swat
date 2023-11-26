package ch.hslu.swda.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for the article request DTO.
 */
class ArticleRequestDTOTest {

    @Test
    void testFromJsonString() {
        String json = "{\"branchId\":1,\"orderNumber\":1,\"articles\":[{\"articleId\":100001,\"quantity\":1}," +
                "{\"articleId\":100002,\"quantity\":2},{\"articleId\":100003,\"quantity\":3}]}";
        try {
            ArticleRequestDTO dto = new ObjectMapper().readValue(json, ArticleRequestDTO.class);
            assertThat(dto.orderNumber()).isEqualTo(1L);
            assertThat(dto.branchId()).isEqualTo(1L);
            assertThat(dto.articles()).hasSize(3);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }

    @Test
    void testNoArticles() {
        ArticleRequestDTO dto = new ArticleRequestDTO(1L, 1L, null);
        assertThat(dto.articles()).hasSize(0);
    }

    @Test
    void testBranchIdInvalid() {
        assertThatThrownBy(() -> new ArticleRequestDTO(0L, 1L, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("branchId should not be lower than 1");
    }

    @Test
    void testOrderNumberInvalid() {
        assertThatThrownBy(() -> new ArticleRequestDTO(1L, 0L, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("orderNumber should not be lower than 1");
    }
}
