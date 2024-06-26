package ch.hslu.swda.business;

import ch.hslu.swda.entities.Article;
import ch.hslu.swda.entities.WarehouseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory implementation of the product catalog used for testing.
 */
public final class ProductCatalogMemory implements ProductCatalog {

    private final Map<Long, Article> catalog = new HashMap<>();

    @Override
    public Article getById(final long branchId, final long articleId) {
        return branchId == 1 ? catalog.get(articleId) : null;
    }

    @Override
    public Map<Long, Article> getById(final long branchId, final List<Long> articleIds) {
        Map<Long, Article> articles = new HashMap<>();
        for (long articleId : articleIds) {
            if (branchId == 1 && catalog.containsKey(articleId)) {
                articles.put(articleId, catalog.get(articleId));
            }
        }
        return articles;
    }

    @Override
    public List<Article> getAll(final long branchId) {
        return branchId == 1 ? new ArrayList<>(catalog.values()) : List.of();
    }

    @Override
    public Article create(final long branchId, final Article article) {
        Article created = null;
        if (branchId == 1) {
            if (!catalog.containsKey(article.articleId())) {
                catalog.put(article.articleId(), article);
                created = article;
            } else {
                created = catalog.get(article.articleId());
            }
        }
        return created;
    }

    @Override
    public Article update(final long branchId, final long articleId, final String name,
                          final BigDecimal price, final int minStock) {
        Article updated = null;
        if (branchId == 1 && catalog.containsKey(articleId)) {
            Article exists = catalog.get(articleId);
            updated = new Article(articleId, name, price, minStock, exists.stock(), exists.reserved());
            catalog.put(articleId, updated);
        }
        return updated;
    }

    @Override
    public boolean delete(final long branchId, final long articleId) {
        if (branchId == 1) {
            catalog.remove(articleId);
        }
        return branchId == 1;
    }

    @Override
    public boolean changeStock(final long branchId, final long articleId, final int amount) {
        boolean result = false;
        Article article = catalog.get(articleId);
        if (branchId == 1 && article != null) {
            int newStock = article.stock() + amount;
            if (newStock >= 0) {
                article = new Article(articleId, article.name(), article.price(), article.minStock(),
                        newStock, article.reserved());
                catalog.put(articleId, article);
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean changeReserved(final long branchId, final long articleId, final int amount) {
        boolean result = false;
        Article article = catalog.get(articleId);
        if (branchId == 1 && article != null) {
            int newReserved = article.reserved() + amount;
            if (newReserved >= 0) {
                article = new Article(articleId, article.name(), article.price(), article.minStock(),
                        article.stock(), newReserved);
                catalog.put(articleId, article);
                result = true;
            }
        }
        return result;
    }

    @Override
    public List<WarehouseEntity<Article>> getLowStock() {
        return catalog.values().stream()
                .filter(article -> (article.stock() - article.reserved()) < article.minStock())
                .map(article -> new WarehouseEntity<>(1L, article))
                .toList();
    }
}
