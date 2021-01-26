package com.sneyder.biznearby.data.model.business

import com.google.gson.annotations.SerializedName

data class BizResult(
    @SerializedName("id") val id: String,
    /**
     * distance in meters of the business relative to where the search was made
     */
    @SerializedName("distance") val distance: Double,
    @SerializedName("address") val address: Address,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)