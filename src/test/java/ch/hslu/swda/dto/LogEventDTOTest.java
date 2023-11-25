package ch.hslu.swda.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the log event DTO.
 */
class LogEventDTOTest {

    @Test
    void testToJsonString() {
        LogEventDTO dto = new LogEventDTO(1L, "test", "test");
        String start = "{\"branchId\":1,\"type\":\"test\",\"message\":\"test\",\"datetime\":\"";
        String end = "\"}";
        try {
            String json = new ObjectMapper().writeValueAsString(dto);
            assertThat(json).startsWith(start);
            assertThat(json).containsPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+");
            assertThat(json).endsWith(end);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }
}
