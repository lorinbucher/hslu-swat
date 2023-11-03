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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory implementation of the product catalogue.
 */
@Singleton
public class ProductCatalogueMemory implements ProductCatalogue {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCatalogueMemory.class);
    private final Map<Long, Article> articles;

    /**
     * Constructor.
     */
    public ProductCatalogueMemory() {
        this.articles = new HashMap<>();
    }

    /**
     * @see ProductCatalogue#getById(long)
     */
    @Override
    public Article getById(final long id) {
        LOG.info("API: read article with id {}", id);
        return this.articles.get(id);
    }

    /**
     * @see ProductCatalogue#getAll()
     */
    @Override
    public List<Article> getAll() {
        LOG.info("API: read all {} articles", this.articles.size());
        return new ArrayList<>(this.articles.values());
    }

    /**
     * @see ProductCatalogue#create(Article)
     */
    @Override
    public Article create(final Article article) {
        this.articles.put(article.articleId(), article);
        LOG.info("API: created article with id {}", article.articleId());
        return article;
    }

    /**
     * @see ProductCatalogue#update(long, Article)
     */
    @Override
    public Article update(final long id, final Article article) {
        Article exists = this.articles.get(id);
        if (exists != null) {
            exists = new Article(id, article.name(), article.price(), article.stock());
            this.articles.put(id, exists);
            LOG.info("API: updated article with id {}", id);
        } else {
            Article newArticle = new Article(id, article.name(), article.price(), article.stock());
            exists = create(newArticle);
        }
        return exists;
    }

    /**
     * @see ProductCatalogue#delete(long)
     */
    @Override
    public boolean delete(final long id) {
        final Article student = articles.remove(id);
        LOG.info("API: {}removed article with id {}", student != null ? "" : "not ", id);
        return student != null;
    }
}
