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
        String json = "{\"orderNumber\":1,\"branchId\":1,\"articles\":[1,2,3]}";
        try {
            ArticleRequestDTO dto = new ObjectMapper().readValue(json, ArticleRequestDTO.class);
            assertThat(dto.orderNumber()).isEqualTo(1L);
            assertThat(dto.branchId()).isEqualTo(1L);
            assertThat(dto.articles()).hasSize(3);
            assertThat(dto.articles()).isEqualTo(List.of(1L, 2L, 3L));
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
    void testOrderNumberInvalid() {
        assertThatThrownBy(() -> new ArticleRequestDTO(0L, 1L, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("orderNumber should not be lower than 1");
    }

    @Test
    void testBranchIdInvalid() {
        assertThatThrownBy(() -> new ArticleRequestDTO(1L, 0L, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("branchId should not be lower than 1");
    }
}
