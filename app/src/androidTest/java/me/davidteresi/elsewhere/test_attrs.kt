/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package test_attrs

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

val imageUrl = "https://www.w3.org/2008/site/images/logo-w3c-mobile-lg.png"
val place = Place(
    id = 2,
    name = "Test Place",
    state = "XX",
    country = "CA",
    coord = Coord(2f, 2f)
)
val weather = Weather(
    main = WeatherMain(42f, 100f),
    weather = listOf(WeatherCondition("Main", "Snowing")),
    wind = WeatherWind(10000f),
    timezone = 32400
)
var wikipediaTitle = "Wikipedia"
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