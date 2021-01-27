package com.sneyder.biznearby.data.api

import com.sneyder.biznearby.data.model.auth.LogInRequest
import com.sneyder.biznearby.data.model.auth.LogOutResponse
import com.sneyder.biznearby.data.model.business.BizResult
import com.sneyder.biznearby.data.model.business.Business
import com.sneyder.biznearby.data.model.business.Category
import com.sneyder.biznearby.data.model.business.Report
import com.sneyder.biznearby.data.model.model.CityLocation
import com.sneyder.biznearby.data.model.user.UserProfile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface BizNearbyApi {

    companion object {
        const val END_POINT = "https://biznearby.herokuapp.com/"

        const val SIGN_UP = "users"
        const val LOG_IN = "sessions"
        const val LOG_OUT = "sessions"
        const val GET_USER_PROFILE = "users/{userId}"

        const val SEARCH_LOCATION = "locations"

        const val ADD_BUSINESS = "businesses"
        const val GET_BUSINESS_CATEGORIES = "categories"
        const val BUSINESS_IMAGE = "businesses/images"

        const val GET_MODERATORS = "moderators"
        const val ADD_MODERATORS = "moderators/{email}"

        const val GET_REPORTS = "reports"
        const val SEARCH_BUSINESSES = "businesses"
        const val GET_BUSINESS_DETAILS = "businesses/{businessId}"

    }

    @GET(GET_BUSINESS_DETAILS)
    suspend fun getBusinessDetails(
        @Path("businessId") businessId: String
    ): Business

    @GET(SEARCH_BUSINESSES)
    suspend fun searchBusinesses(
        @Query("query") query: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Int
    ): ArrayList<BizResult>

    @GET(GET_REPORTS)
    suspend fun getReports(
        @Query("type") type: String
    ): ArrayList<Report>

    @POST(ADD_MODERATORS)
    suspend fun addModerator(
        @Path("email") email: String
    ): Boolean

    @GET(GET_MODERATORS)
    suspend fun getModerators(): List<UserProfile>

    @POST(ADD_BUSINESS)
    suspend fun addBusiness(
        @Body business: Business
    ): Business

    @GET(SEARCH_LOCATION)
    suspend fun searchCityLocations(
        @Query("query") query: String,
        @Query("limit") limit: Int,
    ): ArrayList<CityLocation>

    @GET(GET_BUSINESS_CATEGORIES)
    suspend fun fetchBusinessCategories(): ArrayList<Category>

    @GET(GET_USER_PROFILE)
    suspend fun getUserProfile(
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
    @POST(BUSINESS_IMAGE)
    suspend fun uploadBusinessImage(
        @Part businessImage: MultipartBody.Part
    ): String

    @Multipart
    @POST(SIGN_UP)
    suspend fun signUp(
        @Part("id") id: RequestBody,
        @Part("email") email: RequestBody,
        @Part("fullname") fullname: RequestBody,
        @Part("password") password: RequestBody? = null,
        @Part("googleAuth.token") googleToken: RequestBody? = null,
        @Part("googleAuth.userId") googleUserId: RequestBody? = null,
        @Part("typeLogin") typeLogin: RequestBody,
        @Part("thumbnailUrl") thumbnailUrl: RequestBody,
        @Part imageProfile: MultipartBody.Part? = null,
    ): Response<UserProfile>

    @POST(LOG_IN)
    suspend fun logIn(
        @Body request: LogInRequest
    ): Response<UserProfile>

    @DELETE(LOG_OUT)
    suspend fun logOut(): LogOutResponse


}
