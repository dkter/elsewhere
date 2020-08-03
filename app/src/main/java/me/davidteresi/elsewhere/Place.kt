package me.davidteresi.elsewhere

data class Coord(
    val lat: Double,
    val lon: Double
)

data class Place(
    val id: Int,
    val name: String,
    val state: String,
    val country: String,
    val coord: Coord
)