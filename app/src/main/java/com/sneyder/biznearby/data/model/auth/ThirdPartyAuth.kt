package com.sneyder.biznearby.data.model.auth

sealed class ThirdPartyAuth(var userId: String, var token: String)
class GoogleAuth(userId: String, token: String): ThirdPartyAuth(userId, token)
class FacebookAuth(userId: String, token: String): ThirdPartyAuth(userId, token)