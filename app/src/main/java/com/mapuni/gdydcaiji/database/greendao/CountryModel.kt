package com.mapuni.gdydcaiji.database.greendao


data class CountryModel(
    val type: String,
    val geometry: Geometry,
    val properties: Properties
)

data class Geometry(
    val type: String,
    val coordinates: List<List<List<Double>>>
)

data class Properties(
    val Type: Int,
    val Subtype: Int,
    val Name: String
)