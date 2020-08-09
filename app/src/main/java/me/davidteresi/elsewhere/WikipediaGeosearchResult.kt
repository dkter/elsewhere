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