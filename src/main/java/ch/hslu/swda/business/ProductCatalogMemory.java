/*
 * Copyright 2023 Roland Gisler, HSLU Informatik, Switzerland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.hslu.swda.business;

import ch.hslu.swda.entities.Article;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * In-memory implementation of the product catalog.
 */
@Singleton
public class ProductCatalogMemory implements ProductCatalog {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCatalogMemory.class);
    private final Map<Long, List<Article>> articles;

    /**
     * Constructor.
     */
    public ProductCatalogMemory() {
        this.articles = new HashMap<>();
    }

    /**
     * @see ProductCatalog#getById(long, long)
     */
    @Override
    public Article getById(final long branchId, final long articleId) {
        LOG.info("DB: read article from branch {} with id {}", branchId, articleId);
        List<Article> branchCatalog = this.articles.get(branchId);
        return branchCatalog.stream().filter(a -> a.articleId() == articleId).findFirst().orElse(null);
    }

    /**
     * @see ProductCatalog#getAll(long)
     */
    @Override
    public List<Article> getAll(final long branchId) {
        LOG.info("DB: read all articles from branch {}", branchId);
        return new ArrayList<>(this.articles.computeIfAbsent(branchId, v -> new ArrayList<>()));
    }

    /**
     * @see ProductCatalog#create(long, Article)
     */
    @Override
    public Article create(final long branchId, final Article article) {
        this.articles.computeIfAbsent(branchId, v -> new ArrayList<>()).add(article);
        LOG.info("DB: created article for branch {} with id {}", branchId, article.articleId());
        return article;
    }

    /**
     * @see ProductCatalog#update(long, long, Article)
     */
    @Override
    public Article update(final long branchId, final long articleId, final Article article) {
        Article exists = this.articles
                .computeIfAbsent(branchId, v -> new ArrayList<>()).stream()
                .filter(a -> a.articleId() == articleId)
                .findFirst().orElse(null);
        if (exists != null) {
            exists = new Article(articleId, article.name(), article.price(), article.stock());
            this.articles.get(branchId).removeIf(a -> a.articleId() == articleId);
            this.articles.get(branchId).add(exists);
            LOG.info("DB: updated article from branch {} with id {}", branchId, articleId);
        } else {
            Article newArticle = new Article(articleId, article.name(), article.price(), article.stock());
            exists = create(branchId, newArticle);
        }
        return exists;
    }

    /**
     * @see ProductCatalog#delete(long, long)
     */
    @Override
    public boolean delete(final long branchId, final long articleId) {
        boolean removed = this.articles
                .computeIfAbsent(branchId, v -> new ArrayList<>())
                .removeIf(a -> a.articleId() == articleId);
        LOG.info("DB: {}removed article from branch {} with id {}", removed ? "" : "not ", branchId, articleId);
        return removed;
    }
}
