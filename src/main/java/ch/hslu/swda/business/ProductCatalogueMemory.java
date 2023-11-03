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
    private Map<Long, Article> articles;

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
        return this.articles.get(id);
    }

    /**
     * @see ProductCatalogue#getAll()
     */
    @Override
    public List<Article> getAll() {
        return new ArrayList<>(this.articles.values());
    }

    /**
     * @see ProductCatalogue#create(Article)
     */
    @Override
    public Article create(final Article article) {
        this.articles.put(article.articleId(), article);
        return article;
    }

    /**
     * @see ProductCatalogue#update(long, Article)
     */
    @Override
    public Article update(final long id, final Article article) {
        Article exists = this.articles.get(id);
        if (exists != null) {
            this.articles.put(id, article);
            exists = this.articles.get(id);
        } else {
            exists = create(article);
        }
        return exists;
    }

    /**
     * @see ProductCatalogue#delete(long)
     */
    @Override
    public boolean delete(final long id) {
        final Article student = articles.remove(id);
        return student != null;
    }
}
