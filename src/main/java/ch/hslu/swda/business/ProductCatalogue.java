/*
 * Copyright 2023, Roland Gisler, Hochschule Luzern - Informatik.
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

import java.util.List;

/**
 * Management of the product catalogue.
 */
public interface ProductCatalogue {

    /**
     * Returns the article with the specified id.
     *
     * @param id id of the article.
     * @return Article.
     */
    Article getById(long id);

    /**
     * Returns all articles in the product catalogue.
     *
     * @return List of all articles.
     */
    List<Article> getAll();

    /**
     * Adds an article.
     *
     * @param article Article.
     * @return Article.
     */
    Article create(Article article);

    /**
     * Updates an article.
     *
     * @param id      id.
     * @param article Article.
     * @return Article.
     */
    Article update(long id, Article article);

    /**
     * Deletes an article.
     *
     * @param id id of the article.
     * @return True if successful, false if not.
     */
    boolean delete(long id);
}
