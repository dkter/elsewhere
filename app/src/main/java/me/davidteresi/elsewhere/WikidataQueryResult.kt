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