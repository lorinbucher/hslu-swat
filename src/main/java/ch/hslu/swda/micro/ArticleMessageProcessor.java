package ch.hslu.swda.micro;

import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.dto.ArticleOrderDTO;
import ch.hslu.swda.dto.ArticleGetDTO;
import ch.hslu.swda.dto.OrderDTO;
import ch.hslu.swda.entities.Article;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the article message processing.
 */
public final class ArticleMessageProcessor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleMessageProcessor.class);

    private final MessageListener messageListener;
    private final MessagePublisher<OrderDTO> messagePublisher;

    private final ProductCatalog productCatalog;

    /**
     * Constructor.
     *
     * @param listener       Message listener.
     * @param publisher      Message publisher.
     * @param productCatalog Product catalog warehouse.
     */
    public ArticleMessageProcessor(final MessageListener listener, final MessagePublisher<OrderDTO> publisher,
                                   final ProductCatalog productCatalog) {
        this.messageListener = listener;
        this.messagePublisher = publisher;
        this.productCatalog = productCatalog;
    }

    /**
     * Listens for incoming messages and processes them.
     */
    @Override
    public void run() {
        messageListener.receiveMessages(Routes.ARTICLE_GET, this::process);
    }

    /**
     * Queries the articles from the DB and returns the response message.
     *
     * @param message Received article request message.
     */
    private void process(final String message) throws IllegalArgumentException {
        ArticleGetDTO request = parseMessage(message);
        if (request != null) {
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
            messagePublisher.sendMessage(Routes.ARTICLE_RETURN, orderDTO);
        } else {
            LOG.error("Parsing message failed, not sending a response");
        }
    }

    /**
     * Parses the article request message.
     *
     * @param message Article request message.
     * @return Article request.
     */
    private ArticleGetDTO parseMessage(final String message) {
        ArticleGetDTO dto = null;
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            dto = mapper.readValue(message, ArticleGetDTO.class);
            LOG.info("Parsed article request message: {}", dto);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to parse article request message: {}", e.getMessage());
        }
        return dto;
    }
}
