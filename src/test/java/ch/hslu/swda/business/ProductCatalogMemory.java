package ch.hslu.swda.business;

import ch.hslu.swda.entities.Article;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory implementation of the product catalog used for testing.
 */
public class ProductCatalogMemory implements ProductCatalog {

    private final Map<Long, Article> catalog = new HashMap<>();

    @Override
    public Article getById(long branchId, long articleId) {
        return branchId == 1 ? catalog.get(articleId) : null;
    }

    @Override
    public List<Article> getAll(long branchId) {
        return branchId == 1 ? new ArrayList<>(catalog.values()) : List.of();
    }

    @Override
    public Article create(long branchId, Article article) {
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
    public Article update(long branchId, long articleId, Article article) {
        Article updated = null;
        if (branchId == 1) {
            updated = new Article(articleId, article.name(), article.price(), article.minStock(), article.stock());
            if (catalog.containsKey(articleId)) {
                catalog.put(articleId, updated);
            } else {
                updated = create(branchId, updated);
            }
        }
        return updated;
    }

    @Override
    public boolean delete(long branchId, long articleId) {
        if (branchId == 1) {
            catalog.remove(articleId);
        }
        return branchId == 1;
    }
}
