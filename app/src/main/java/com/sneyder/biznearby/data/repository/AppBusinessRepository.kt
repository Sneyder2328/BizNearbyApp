package com.sneyder.biznearby.data.repository

import com.sneyder.biznearby.data.api.BizNearbyApi
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.business.*
import com.sneyder.biznearby.data.preferences.PreferencesHelper
import com.sneyder.biznearby.utils.debug
import com.sneyder.biznearby.utils.getMimeType
import com.sneyder.biznearby.utils.safeApiCall
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class AppBusinessRepository
@Inject constructor(
    private val bizNearbyApi: BizNearbyApi,
    private val prefs: PreferencesHelper
) : BusinessRepository() {

    override suspend fun getMyBusinesses(): Result<List<Business>> {
        return safeApiCall({
            bizNearbyApi.getMyBusinesses(prefs.getCurrentUserProfile()!!.id)
        })
    }

    override suspend fun fetchBusiness(businessId: String): Result<Business> {
        return safeApiCall({
            bizNearbyApi.getBusinessDetails(businessId)
        })
    }

    override suspend fun searchBusinesses(
        query: String,
        latitude: Double,
        longitude: Double,
        radius: Int
    ): Result<ArrayList<BizResult>> {
        debug("searchBusinesses API")
        return safeApiCall({
            bizNearbyApi.searchBusinesses(query, latitude, longitude, radius)
        })
    }

    override suspend fun fetchReports(type: String): Result<ArrayList<Report>> {
        return safeApiCall({
            bizNearbyApi.getReports(type)
        })
    }

    override suspend fun fetchCategories(): Result<ArrayList<Category>> {
        return safeApiCall({
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
        return safeApiCall({
            bizNearbyApi.uploadBusinessImage(businessImage)
        })
    }

    override suspend fun addNewBusiness(business: Business): Result<Business> {
        return safeApiCall({
            bizNearbyApi.addBusiness(business)
        })
    }

    override suspend fun addNewReport(report: ReportRequest): Result<Report> {
        return safeApiCall({
            bizNearbyApi.addReport(report)
        })
    }

    override suspend fun deleteReport(reportId: String): Result<Boolean> {
        return safeApiCall({
            bizNearbyApi.deleteReport(reportId)
        })
    }

    override suspend fun reviewReport(reportId: String, reviewReportRequest: ReviewReportRequest): Result<Report> {
        return safeApiCall({
            bizNearbyApi.reviewReport(reportId, reviewReportRequest)
        })
    }

}