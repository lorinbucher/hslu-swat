package ch.hslu.swda.micro;

import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.dto.ArticleRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements article message processing.
 */
public final class ArticleMessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleMessageProcessor.class);

    @Inject
    private ProductCatalog productCatalog;

    /**
     * Queries the articles from the DB and returns the response message.
     *
     * @param message Received article request message.
     * @return Article response message.
     * @throws IllegalArgumentException If the message cannot be parsed.
     */

    public String process(String message) throws IllegalArgumentException {
        ArticleRequestDTO articleGet = parseMessage(message);
        if (articleGet == null) {
            throw new IllegalArgumentException("Cannot parse received article message");
        }
        return "";
    }

    private ArticleRequestDTO parseMessage(String message) {
        ArticleRequestDTO dto = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            dto = mapper.readValue(message, ArticleRequestDTO.class);
            LOG.info("Parsed article request message: {}", dto);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to parse article request message: {}", e.getMessage());
        }
        return dto;
    }
}
