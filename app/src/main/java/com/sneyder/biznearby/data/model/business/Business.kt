package com.sneyder.biznearby.data.model.business

import com.google.gson.annotations.SerializedName
import java.util.*

data class BasicInfo(
    var name: String = "",
    var description: String = "",
    var bannerUrl: String? = "",
    var categories: List<Int> = emptyList(),
)

data class AddressForm(
    var id: String = "",
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var cityCode: Int = 0,
)

fun AddressForm.toAddress(): Address {
    return Address(
        id = UUID.randomUUID().toString(),
        address = address,
        latitude = latitude,
        longitude = longitude,
        cityCode = cityCode
    )
}


data class Business(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
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