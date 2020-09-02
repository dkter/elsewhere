/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package me.davidteresi.elsewhere;

data class WikidataQueryImage(
    val type: String?,
    val value: String?
)

data class WikidataQueryBinding(
    val image: WikidataQueryImage?
)

data class WikidataQueryResults(
    val bindings: List<WikidataQueryBinding>?
)

data class WikidataQueryResult(
    val results: WikidataQueryResults?
)