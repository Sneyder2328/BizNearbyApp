package com.sneyder.biznearby.data.model.business

import com.google.gson.annotations.SerializedName

data class Hour(
    @SerializedName("day") val day: Int,
    @SerializedName("openTime") val openTime: String,
    @SerializedName("closeTime") val closeTime: String
)
