package com.sneyder.biznearby.data.model.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LogOutResponse(
    @SerializedName("logOut") @Expose var logOut: Boolean
)