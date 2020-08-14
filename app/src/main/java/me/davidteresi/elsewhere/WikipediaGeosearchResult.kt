/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package me.davidteresi.elsewhere

data class WikipediaGeosearchPage(
    val title: String?
)

data class WikipediaGeosearchQuery(
    val geosearch: List<WikipediaGeosearchPage>?
)

data class WikipediaGeosearchResult(
    val query: WikipediaGeosearchQuery?
)