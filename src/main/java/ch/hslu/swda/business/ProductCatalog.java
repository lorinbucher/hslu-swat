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
 * Management of the product catalog.
 */
public interface ProductCatalog {

    /**
     * Returns the article with the specified article ID in the branch's catalog.
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @return Article.
     */
    Article getById(long branchId, long articleId);

    /**
     * Returns all articles in the product catalog of the branch.
     *
     * @param branchId ID of the branch.
     * @return List of all articles.
     */
    List<Article> getAll(long branchId);

    /**
     * Adds an article to the catalog of the branch.
     *
     * @param branchId ID of the branch.
     * @param article  Article.
     * @return Article.
     */
    Article create(long branchId, Article article);

    /**
     * Updates an article in the catalog of the branch.
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @param article   Article.
     * @return Article.
     */
    Article update(long branchId, long articleId, Article article);

    /**
     * Deletes an article from the catalog of the branch.
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @return True if successful, false if not.
     */
    boolean delete(long branchId, long articleId);
}
