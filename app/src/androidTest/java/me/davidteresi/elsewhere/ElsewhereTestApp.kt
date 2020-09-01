package me.davidteresi.elsewhere

class ElsewhereTestApp: ElsewhereApp() {
    override val owmHost = "http://127.0.0.1:8080"
    override val wikipediaHost = "http://127.0.0.1:8080"
    override val wikidataHost = "http://127.0.0.1:8080"

    override fun initialize() {
        stateManager = FakeStateManager(this, false)
        placeDataSource = FakePlaceDataSource(this, test_attrs.place)
    }
}