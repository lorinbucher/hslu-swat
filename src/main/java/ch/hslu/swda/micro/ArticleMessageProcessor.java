package ch.hslu.swda.micro;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.dto.ArticleOrderDTO;
import ch.hslu.swda.dto.ArticleRequestDTO;
import ch.hslu.swda.dto.ArticleResponseDTO;
import ch.hslu.swda.entities.Article;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements article message processing.
 */
public final class ArticleMessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleMessageProcessor.class);

    private final Deliveries deliveries;
    private final ProductCatalog productCatalog;

    /**
     * Constructor.
     */
    public ArticleMessageProcessor(final Deliveries deliveries, final ProductCatalog productCatalog) {
        this.deliveries = deliveries;
        this.productCatalog = productCatalog;
    }

    /**
     * Queries the articles from the DB and returns the response message.
     *
     * @param message Received article request message.
     * @return Article response message.
     * @throws IllegalArgumentException If the message cannot be parsed.
     */

    public String process(final String message) throws IllegalArgumentException {
        ArticleRequestDTO request = parseMessage(message);
        if (request == null) {
            throw new IllegalArgumentException("Cannot parse received article message");
        }

        List<ArticleOrderDTO> articles = new ArrayList<>();
        List<String> error = new ArrayList<>();
        for (ArticleOrderDTO articleDto : request.articles()) {
            Article a = productCatalog.getById(request.branchId(), articleDto.articleId());
            if (a != null) {
                ArticleOrderDTO dto = new ArticleOrderDTO(a.articleId(), a.name(), a.price(), articleDto.quantity());
                articles.add(dto);
            } else {
                error.add("article " + articleDto.articleId() + " not found in catalog");
            }
        }

        ArticleResponseDTO resDto = new ArticleResponseDTO(request.orderNumber(), request.branchId(), articles, error);
        return serializeMessage(resDto);
    }

    private ArticleRequestDTO parseMessage(final String message) {
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

    private String serializeMessage(final ArticleResponseDTO responseDTO) {
        String response = "{}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(responseDTO);
            LOG.info("Serialized article response message: {}", response);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize article response message: {}", e.getMessage());
        }
        return response;
    }
}
