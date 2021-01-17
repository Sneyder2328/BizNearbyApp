package com.sneyder.biznearby.data.repository

import android.webkit.MimeTypeMap
import com.google.gson.GsonBuilder
import com.sneyder.biznearby.data.api.BizNearbyApi
import com.sneyder.biznearby.data.api.genRequestBody
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.auth.LogInRequest
import com.sneyder.biznearby.data.model.auth.LogOutResponse
import com.sneyder.biznearby.data.model.auth.SignUpRequest
import com.sneyder.biznearby.data.model.user.UserProfile
import com.sneyder.biznearby.data.preferences.AppPreferencesHelper.Companion.ACCESS_TOKEN
import com.sneyder.biznearby.data.preferences.AppPreferencesHelper.Companion.USER
import com.sneyder.biznearby.data.preferences.PreferencesHelper
import com.sneyder.biznearby.utils.debug
import com.sneyder.biznearby.utils.mapToResult
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppUserRepository
@Inject constructor(
    private val bizNearbyApi: BizNearbyApi,
    private val prefs: PreferencesHelper,
//    private val appDatabase: AppDatabase
) : UserRepository() {

    override fun getCurrentUserProfile(): UserProfile? {
        val json: String = prefs[USER] ?: return null
        return try {
            GsonBuilder().serializeNulls().create()
                .fromJson(json, UserProfile::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getAccessToken(): String {
        val token: String = prefs[ACCESS_TOKEN] ?: ""
        return "Bearer $token"
    }

    override suspend fun fetchUserProfile(userId: String): Result<UserProfile> {
        return mapToResult({
            bizNearbyApi.getUserProfile(
                authorization = getAccessToken(),
                userId = userId
            )
        })
    }


    /**
     * More info at: https://stackoverflow.com/questions/8589645/how-to-determine-mime-type-of-file-in-android/39923767#39923767
     */
    private fun getMimeType(url: String?): String {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.ROOT))
        }
        if (type == null) {
            type = "image/*" // fallback type. You might set it to */*
        }
        return type
    }

    override suspend fun signUp(request: SignUpRequest): Result<UserProfile> {
        val imageProfile: MultipartBody.Part? = request.imageProfilePath?.let {
            val type: String = getMimeType(it) ?: return@let null
            val file = File(it)
            val requestBody = RequestBody.create(MediaType.parse(type), file)
            // MultipartBody.Part is used to send also the actual file name
            return@let MultipartBody.Part.createFormData("imageProfile", file.name, requestBody)
        }

        val response = bizNearbyApi.signUp(
            id = genRequestBody(request.id),
            email = genRequestBody(request.email),
            password = genRequestBody(request.password ?: ""),
            typeLogin = genRequestBody(request.typeLogin),
            fullname = genRequestBody(request.fullname),
            thumbnailUrl = genRequestBody(request.thumbnailUrl ?: ""),
            imageProfile = imageProfile
        )
        response.headers().get(ACCESS_TOKEN)?.let {
            prefs[ACCESS_TOKEN] = it
        }
        response.body()?.let {
            val toJson = GsonBuilder().serializeNulls().create().toJson(it)
            debug("user saved to prefs=$toJson")
            prefs[USER] = toJson
        }

        return mapToResult({
            response.body()
        })
    }

    override suspend fun logIn(request: LogInRequest): Result<UserProfile> {
        val response = bizNearbyApi.logIn(request)
         response.headers().get(ACCESS_TOKEN)?.let {
            prefs[ACCESS_TOKEN] = it
        }
        response.body()?.let {
            val toJson = GsonBuilder().serializeNulls().create().toJson(it)
            debug("user saved to prefs=$toJson")
            prefs[USER] = toJson
        }

        return mapToResult({
            response.body()
        })
    }

    override suspend fun logOut(): Result<LogOutResponse> {
        return mapToResult({
            bizNearbyApi.logOut(authorization = getAccessToken())
        }, onFinally = {
            prefs[USER] = ""
            prefs[ACCESS_TOKEN] = ""
        })
    }

    //
//
//    override fun getAuthInfo(): AuthInfo {
//        return AuthInfo(
//            logIn = prefs[AppPreferencesHelper.LOGIN],
//            token = prefs[AppPreferencesHelper.TOKEN]
//        )
//    }
//
//    override fun getUserData(): UserData? {
//        val token = getAuthInfo().token
//        if (token.isNullOrEmpty()) return null
//        return Gson().fromJson<UserData>(token.decoded().payload, UserData::class.java)
//    }
//
//    override suspend fun getFirebaseTokenId(): String? {
//        return suspendCoroutine { cont ->
//            FirebaseInstanceId.getInstance().instanceId
//                .addOnCompleteListener(OnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        debug("FirebaseInstanceId getInstance failed ${task.exception}")
//                        cont.resume(null)
//                        return@OnCompleteListener
//                    }
//                    val token = task.result?.token
//                    debug("FirebaseInstanceId getInstance token=$token")
//                    cont.resume(token)
//                })
//        }
//    }
//
//    override suspend fun sendFirebaseTokenId(firebaseTokenId: FirebaseTokenId): Result<FirebaseTokenId> {
//        return mapToResult {
//            bizNearbyApi.sendFirebaseTokenId(
//                userId = getUserData()?.id ?: throw Exception("getUserData.id is null"),
//                firebaseTokenId = firebaseTokenId
//            ).also { prefs[AppPreferencesHelper.TOKEN_UPLOADED] = true }
//        }
//    }
//
//    override suspend fun logIn(user: String, password: String): Result<LogInResponse> {
//        return mapToResult {
//            //logOut() didnt work :u
//            bizNearbyApi.logIn(
//                LogInRequest(
//                    usuario = user,
//                    clave = password
//                )
//            ).apply {
//                prefs[AppPreferencesHelper.LOGIN] = logIn
//                prefs[AppPreferencesHelper.USER] = user
//                prefs[AppPreferencesHelper.TOKEN] = token
//                prefs[AppPreferencesHelper.TOKEN_UPLOADED] = false
//            }
//        }
//    }
//
//    override suspend fun logOut(): Result<LogOutResponse> {
//        return mapToResult {
//            bizNearbyApi.logOut(LogOutRequest(usuario = prefs[AppPreferencesHelper.USER]))
//                .apply {
//                    prefs[AppPreferencesHelper.LOGIN] = logIn
//                    prefs[AppPreferencesHelper.TOKEN] = ""
//                    prefs[AppPreferencesHelper.TOKEN_UPLOADED] = false
//                }.also { appDatabase.clearAllTables() }
//        }
//    }

}