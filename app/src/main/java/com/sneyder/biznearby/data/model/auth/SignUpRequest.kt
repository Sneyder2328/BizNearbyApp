package com.sneyder.biznearby.data.model.auth

data class SignUpRequest(
    var email: String,
    var password: String? = null,
    var typeLogin: String,
    var googleAuth: GoogleAuth? = null,
    var facebookAuth: FacebookAuth? = null,
    var id: String,
    var fullname: String,
    var phoneNumber: String? = null,
    var thumbnailUrl: String? = null,
    var imageProfilePath: String? = null
)
