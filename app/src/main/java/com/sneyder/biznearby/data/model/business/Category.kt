package com.sneyder.biznearby.data.model.business

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("code") val code: Int,
    @SerializedName("category") val category: String,
)
