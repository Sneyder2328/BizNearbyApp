package com.sneyder.biznearby.data.model.auth

enum class TypeLogin(var type: String) {
    FACEBOOK("facebook"), GOOGLE("google"), EMAIL("email")
}