package com.sneyder.biznearby.data.model.user

import com.google.gson.annotations.SerializedName
import com.sneyder.biznearby.data.model.TypeUser

data class UserProfile(
    @SerializedName("id") var id: String,
    @SerializedName("fullname") var fullname: String,
    @SerializedName("email") var email: String,
    @SerializedName("thumbnailUrl") var thumbnailUrl: String,
    @SerializedName("typeUser") var typeUser: TypeUser
)
