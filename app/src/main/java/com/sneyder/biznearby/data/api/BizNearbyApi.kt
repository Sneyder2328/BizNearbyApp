package com.sneyder.biznearby.data.api

import com.sneyder.biznearby.data.model.auth.LogInRequest
import com.sneyder.biznearby.data.model.auth.LogOutResponse
import com.sneyder.biznearby.data.model.user.UserProfile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

fun genRequestBody(content: String): RequestBody = RequestBody.create(MultipartBody.FORM, content)

interface BizNearbyApi {

    companion object {
        const val END_POINT = "https://biznearby.herokuapp.com/"

        const val SIGN_UP = "users"
        const val LOG_IN = "sessions"
        const val LOG_OUT = "sessions"
        const val GET_USER_PROFILE = "users/{userId}/"

    }

    @GET(GET_USER_PROFILE)
    suspend fun getUserProfile(
        @Header("Authorization") authorization: String?,
        @Path("userId") userId: String
    ): UserProfile
//
//    @GET(NO_APTOS)
//    suspend fun getNoAptos(
//        @Header("Authorization") authorization: String?,
//        @Path("iniDate") iniDate: String,
//        @Path("endDate") endDate: String
//    ): List<CedulasByDate>
//
//    @PUT(SEND_FIREBASE_TOKEN_ID)
//    suspend fun sendFirebaseTokenId(
//        @Path("userId") userId: String,
//        @Body firebaseTokenId: FirebaseTokenId
//    ): FirebaseTokenId

    @Multipart
    @POST(SIGN_UP)
    suspend fun signUp(
        @Part("id") id: RequestBody,
        @Part("email") email: RequestBody,
        @Part("fullname") fullname: RequestBody,
        @Part("password") password: RequestBody,
        @Part("typeLogin") typeLogin: RequestBody,
        @Part("thumbnailUrl") thumbnailUrl: RequestBody,
        @Part imageProfile: MultipartBody.Part? = null,
    ): Response<UserProfile>

    @POST(LOG_IN)
    suspend fun logIn(
        @Body request: LogInRequest
    ): Response<UserProfile>

    @DELETE(LOG_OUT)
    suspend fun logOut(
        @Header("Authorization") authorization: String?
    ): LogOutResponse


}
