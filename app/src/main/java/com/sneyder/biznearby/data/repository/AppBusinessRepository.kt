package com.sneyder.biznearby.data.repository

import com.sneyder.biznearby.data.api.BizNearbyApi
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.business.Business
import com.sneyder.biznearby.data.model.business.Category
import com.sneyder.biznearby.utils.getMimeType
import com.sneyder.biznearby.utils.mapToResult
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class AppBusinessRepository
@Inject constructor(
    private val bizNearbyApi: BizNearbyApi
) : BusinessRepository() {

    override suspend fun fetchCategories(): Result<ArrayList<Category>> {
        return mapToResult({
            bizNearbyApi.fetchBusinessCategories()
        })
    }

    override suspend fun uploadBusinessImage(imgPath: String): Result<String> {
        val type: String = getMimeType(imgPath)
        val file = File(imgPath)
        val requestBody = RequestBody.create(MediaType.parse(type), file)
        // MultipartBody.Part is used to send also the actual file name
        val businessImage: MultipartBody.Part =
            MultipartBody.Part.createFormData("businessImage", file.name, requestBody)

        return mapToResult({
            bizNearbyApi.uploadBusinessImage(businessImage)
        })
    }

    override suspend fun addNewBusiness(business: Business): Result<Business> {
        return mapToResult({
            bizNearbyApi.addBusiness(business)
        })
    }
}