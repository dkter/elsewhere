/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package me.davidteresi.elsewhere

data class WikipediaPageImageOriginal(
    val source: String?,
    val width: Int?,
    val height: Int?
)

data class WikipediaPageImagePage(
    val original: WikipediaPageImageOriginal?
)

data class WikipediaPageImageQuery(
    val pages: List<WikipediaPageImagePage>?
)

data class WikipediaPageImageResult(
    val query: WikipediaPageImageQuery?
)