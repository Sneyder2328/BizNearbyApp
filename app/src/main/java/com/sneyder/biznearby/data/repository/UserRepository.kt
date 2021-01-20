package com.sneyder.biznearby.data.repository

import com.sneyder.biznearby.data.model.user.UserProfile
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.auth.LogInRequest
import com.sneyder.biznearby.data.model.auth.LogOutResponse
import com.sneyder.biznearby.data.model.auth.SignUpRequest

abstract class UserRepository {

    abstract fun getCurrentUserProfile(): UserProfile?

    abstract suspend fun fetchModerators(): Result<List<UserProfile>>
    abstract suspend fun fetchUserProfile(userId: String): Result<UserProfile>
    abstract suspend fun signUp(request: SignUpRequest): Result<UserProfile>
    abstract suspend fun logIn(request: LogInRequest): Result<UserProfile>
    abstract suspend fun logOut(): Result<LogOutResponse>

}