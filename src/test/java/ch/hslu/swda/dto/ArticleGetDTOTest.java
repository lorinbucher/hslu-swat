package ch.hslu.swda.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for the article get DTO.
 */
class ArticleGetDTOTest {

    @Test
    void testFromJsonString() {
        String json = "{\"branchId\":1,\"orderNumber\":1,\"articles\":[100001,100002,100003]}";
        try {
            ArticleGetDTO dto = new ObjectMapper().readValue(json, ArticleGetDTO.class);
            assertThat(dto.orderNumber()).isEqualTo(1L);
            assertThat(dto.branchId()).isEqualTo(1L);
            assertThat(dto.articles()).hasSize(3);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }

    @Test
    void testNoArticles() {
        ArticleGetDTO dto = new ArticleGetDTO(1L, 1L, null);
        assertThat(dto.articles()).isEmpty();
    }

    @Test
    void testBranchIdInvalid() {
        List<Long> articles = List.of();
        assertThatThrownBy(() -> new ArticleGetDTO(0L, 1L, articles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("branchId should not be lower than 1");
    }

    @Test
    void testOrderNumberInvalid() {
        List<Long> articles = List.of();
        assertThatThrownBy(() -> new ArticleGetDTO(1L, 0L, articles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("orderNumber should not be lower than 1");
    }
}
