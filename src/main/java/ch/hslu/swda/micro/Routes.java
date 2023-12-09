package ch.hslu.swda.micro;

/**
 * Holds all constants for message routes.
 */
public final class Routes {

    static public final String ARTICLE_DELIVERED = "article.delivered";
    static public final String ARTICLE_GET = "article.get";
    static public final String ARTICLE_RETURN = "article.return";
    static public final String LOG_EVENT = "log.event";
    static public final String NEW_ORDER = "order.warehouse";

    /**
     * No instance allowed.
     */
    private Routes() {
    }
}
