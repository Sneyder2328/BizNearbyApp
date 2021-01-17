package com.sneyder.biznearby.data.model.user

import com.google.gson.annotations.SerializedName
import com.sneyder.biznearby.data.model.TypeUser
import com.sneyder.biznearby.utils.debug
import java.util.*

data class UserProfile(
    @SerializedName("id") var id: String,
    @SerializedName("fullname") var fullname: String,
    @SerializedName("email") var email: String,
    @SerializedName("thumbnailUrl") var thumbnailUrl: String,
    @SerializedName("typeUser") var typeUser: String
){

    fun getTypeUser(): TypeUser {
        return TypeUser.valueOf(typeUser.toUpperCase(Locale.ROOT)).also {
            debug("getTypeUser=$it")
        }
    }
}
