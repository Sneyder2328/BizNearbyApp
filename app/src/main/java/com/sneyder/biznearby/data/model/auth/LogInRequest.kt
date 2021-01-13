package com.sneyder.biznearby.data.model.auth


data class LogInRequest(
    var email: String,
    var password: String? = null,
    var typeLogin: TypeLogin,
    var googleAuth: GoogleAuth? = null,
    var facebookAuth: FacebookAuth? = null
)
