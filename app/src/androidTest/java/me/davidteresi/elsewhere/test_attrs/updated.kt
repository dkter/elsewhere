/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package test_attrs.updated

import me.davidteresi.elsewhere.Coord
import me.davidteresi.elsewhere.Place
import me.davidteresi.elsewhere.Weather
import me.davidteresi.elsewhere.WeatherMain
import me.davidteresi.elsewhere.WeatherCondition
import me.davidteresi.elsewhere.WeatherWind
import me.davidteresi.elsewhere.WikidataQueryBinding
import me.davidteresi.elsewhere.WikidataQueryImage
import me.davidteresi.elsewhere.WikidataQueryResult
import me.davidteresi.elsewhere.WikidataQueryResults
import me.davidteresi.elsewhere.WikipediaGeosearchPage
import me.davidteresi.elsewhere.WikipediaGeosearchQuery
import me.davidteresi.elsewhere.WikipediaGeosearchResult

val imageUrl = "https://commons.wikimedia.org/wiki/File:Catedral_de_Pula,_Pula,_Croacia,_2017-04-17,_DD_65-67_HDR.jpg"
val place = Place(
    id = 4,
    name = "Updated Place",
    state = "YY",
    country = "US",
    coord = Coord(10f, 10f)
)
val weather = Weather(
    main = WeatherMain(273f, 22f),
    weather = listOf(WeatherCondition("idk", "Sunny")),
    wind = WeatherWind(10f),
    timezone = 0
)
var wikipediaTitle = "Croatia"
val wikipediaGeosearchResult = WikipediaGeosearchResult(
    query = WikipediaGeosearchQuery(
        geosearch = listOf(
            WikipediaGeosearchPage(wikipediaTitle)
        )
    )
)
val wikidataQueryResult = WikidataQueryResult(
    results = WikidataQueryResults(
        bindings = listOf(
            WikidataQueryBinding(
                image = WikidataQueryImage(
                    type = "uri",  // i don't remember what this is supposed to be, but it's never used
                    value = imageUrl
                )
            )
        )
    )
)