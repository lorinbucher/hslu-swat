package ch.hslu.swda.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for the order DTO.
 */
class OrderDTOTest {

    @Test
    void testToJsonString() {
        ArticleOrderDTO articleOrderDTO = new ArticleOrderDTO(1L, "Test", new BigDecimal("50.25"), 2, null);
        OrderDTO orderDTO = new OrderDTO(1L, 1L, List.of(articleOrderDTO), List.of("Error"));
        String start = "{\"branchId\":1,\"orderNumber\":1,\"articles\":[{";
        String end = "}],\"error\":[\"Error\"]}";
        try {
            String json = new ObjectMapper().writeValueAsString(orderDTO);
            assertThat(json).startsWith(start);
            assertThat(json).endsWith(end);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }

    @Test
    void testBranchIdInvalid() {
        assertThatThrownBy(() -> new OrderDTO(0L, 1L, List.of(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("branchId should not be lower than 1");
    }

    @Test
    void testOrderNumberInvalid() {
        assertThatThrownBy(() -> new OrderDTO(1L, 0L, List.of(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("orderNumber should not be lower than 1");
    }

    @Test
    void testNoArticles() {
        OrderDTO dto = new OrderDTO(1L, 1L, null, null);
        assertThat(dto.articles()).hasSize(0);
    }
}
