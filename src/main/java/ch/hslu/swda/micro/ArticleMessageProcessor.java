package ch.hslu.swda.micro;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.dto.ArticleOrderDTO;
import ch.hslu.swda.dto.ArticleRequestDTO;
import ch.hslu.swda.dto.ArticleResponseDTO;
import ch.hslu.swda.entities.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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

        ArticleResponseDTO resDto = new ArticleResponseDTO(request.branchId(), request.orderNumber(), articles, error);
        if (error.isEmpty()) {
            boolean deliveryProcessed = processDelivery(request);
            if (!deliveryProcessed) {
                List<String> errors = resDto.error();
                errors.add("processing delivery failed");
                resDto = new ArticleResponseDTO(resDto.branchId(), resDto.orderNumber(), resDto.articles(), errors);
            }
        }
        return serializeMessage(resDto);
    }

    /**
     * Registers the delivery for the order for further processing.
     *
     * @param request Order details.
     * @return True if the delivery was processed successfully, false if not.
     */
    private boolean processDelivery(final ArticleRequestDTO request) {
        List<DeliveryArticle> deliveryArticles = request.articles().stream()
                .map(a -> new DeliveryArticle(a.articleId(), a.quantity(), DeliveryArticleStatus.PROCESSING))
                .toList();

        boolean result;
        Delivery exists = deliveries.getById(request.branchId(), request.orderNumber());
        if (deliveryArticles.isEmpty() && exists == null) {
            LOG.info("Not registering new delivery {} for branch {}", request.orderNumber(), request.branchId());
            result = false;
        } else if (exists == null) {
            LOG.info("Registering new delivery {} for branch {}", request.orderNumber(), request.branchId());
            Delivery delivery = new Delivery(request.orderNumber(), DeliveryStatus.NEW, deliveryArticles);
            result = deliveries.create(request.branchId(), delivery) != null;
        } else {
            LOG.info("Updating delivery {} for branch {}", request.orderNumber(), request.branchId());
            result = updateDelivery(request.branchId(), exists, deliveryArticles);
        }
        return result;
    }

    /**
     * Updates an existing delivery based on the current state of the delivery.
     *
     * @param branchId ID of the branch.
     * @param existing Existing delivery.
     * @param articles Updated list of delivery articles.
     * @return True if the delivery was updated successfully.
     */
    private boolean updateDelivery(final long branchId, final Delivery existing, final List<DeliveryArticle> articles) {
        Delivery delivery = null;
        List<DeliveryArticle> deliveryArticles = articles;
        switch (existing.status()) {
            case NEW:
                delivery = new Delivery(existing.orderNumber(), DeliveryStatus.NEW, deliveryArticles);
                break;
            case CHANGED:
                deliveryArticles = Stream.concat(
                        existing.articles().stream().filter(a -> a.status() != DeliveryArticleStatus.PROCESSING),
                        articles.stream()).toList();
                delivery = new Delivery(existing.orderNumber(), DeliveryStatus.CHANGED, deliveryArticles);
                break;
            case COMPLETED:
                LOG.error("Delivery {} for branch {} already completed", existing.orderNumber(), branchId);
                break;
            default:
                deliveryArticles = Stream.concat(existing.articles().stream(), articles.stream()).toList();
                delivery = new Delivery(existing.orderNumber(), DeliveryStatus.CHANGED, deliveryArticles);
                break;
        }

        boolean result = false;
        if (delivery != null) {
            result = deliveries.update(branchId, existing.orderNumber(), delivery) != null;
        }
        return result;
    }

    /**
     * Parses the article request message.
     *
     * @param message Article request message.
     * @return Article request.
     */
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

    /**
     * Serializes the article response message.
     *
     * @param responseDTO Article response.
     * @return Article response message.
     */
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
