package com.sneyder.biznearby.data.model.business

import com.google.gson.annotations.SerializedName

data class Business(
    @SerializedName("userId") val userId: String,
    @SerializedName("businessId") val businessId: String,
    @SerializedName("address") val address: Address,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("bannerUrl") val bannerUrl: String,
    @SerializedName("hours") val hours: List<Hour>,
    @SerializedName("phoneNumbers") val phoneNumbers: List<String>,
    @SerializedName("categories") val categories: List<Int>,
    @SerializedName("images") val images: List<String>
)

data class Address(
    @SerializedName("id") val id: String,
    @SerializedName("address") val address: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("cityCode") val cityCode: Int,
)