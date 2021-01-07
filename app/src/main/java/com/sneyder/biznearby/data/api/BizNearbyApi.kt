package com.sneyder.biznearby.data.api

import retrofit2.http.*

interface BizNearbyApi {

    companion object {
        const val END_POINT = "https://biznearby.herokuapp.com/"

//        const val FIND_PERSONS = "maestros/persons"
//        const val ADD_PERSON = "maestros/persons/"
    }

//    @GET(APTOS)
//    suspend fun getAptos(
//        @Header("Authorization") authorization: String?,
//        @Path("iniDate") iniDate: String,
//        @Path("endDate") endDate: String
//    ): List<CedulasByDate>
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
//
//    @POST(LOG_IN)
//    suspend fun logIn(
//        @Body logInRequest: LogInRequest
//    ): LogInResponse
//
//    @POST(LOG_OUT)
//    suspend fun logOut(
//        @Body logOutRequest: LogOutRequest
//    ): LogOutResponse


}
