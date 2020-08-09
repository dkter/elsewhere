package me.davidteresi.elsewhere

data class WikipediaPageImageOriginal(
    val source: String?
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