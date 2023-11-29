package ch.hslu.swda.micro;

import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.dto.ArticleOrderDTO;
import ch.hslu.swda.dto.ArticleGetDTO;
import ch.hslu.swda.dto.OrderDTO;
import ch.hslu.swda.entities.Article;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the article message processing.
 */
public final class ArticleMessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleMessageProcessor.class);

    private final ProductCatalog productCatalog;

    /**
     * Constructor.
     */
    public ArticleMessageProcessor(final ProductCatalog productCatalog) {
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
        ArticleGetDTO request = parseMessage(message);
        if (request == null) {
            throw new IllegalArgumentException("Cannot parse received article message");
        }

        List<ArticleOrderDTO> articles = new ArrayList<>();
        List<String> error = new ArrayList<>();
        for (long articleId : request.articles()) {
            Article a = productCatalog.getById(request.branchId(), articleId);
            if (a != null) {
                ArticleOrderDTO dto = new ArticleOrderDTO(a.articleId(), a.name(), a.price(), null);
                articles.add(dto);
            } else {
                error.add("article " + articleId + " not found in catalog");
            }
        }

        OrderDTO orderDTO = new OrderDTO(request.branchId(), request.orderNumber(), articles, error);
        return serializeMessage(orderDTO);
    }

    /**
     * Parses the article request message.
     *
     * @param message Article request message.
     * @return Article request.
     */
    private ArticleGetDTO parseMessage(final String message) {
        ArticleGetDTO dto = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            dto = mapper.readValue(message, ArticleGetDTO.class);
            LOG.info("Parsed article request message: {}", dto);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to parse article request message: {}", e.getMessage());
        }
        return dto;
    }

    /**
     * Serializes the article response message.
     *
     * @param orderDTO Article response.
     * @return Article response message.
     */
    private String serializeMessage(final OrderDTO orderDTO) {
        String response = "{}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(orderDTO);
            LOG.info("Serialized article response message: {}", response);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize article response message: {}", e.getMessage());
        }
        return response;
    }
}
