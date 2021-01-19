package com.sneyder.biznearby.data.model.model

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: Int
)

data class State(
    @SerializedName("name") val name: String
)

data class Country(
    @SerializedName("name") val name: String
)

data class CityLocation(
    @SerializedName("city") val city: City,
    @SerializedName("state") val state: State,
    @SerializedName("country") val country: Country
)
