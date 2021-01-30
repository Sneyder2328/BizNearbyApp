package com.sneyder.biznearby.data.repository

import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.business.*

abstract class BusinessRepository {

    abstract suspend fun getMyBusinesses(): Result<List<Business>>

    abstract suspend fun searchBusinesses(query: String, latitude: Double, longitude: Double, radius: Int): Result<ArrayList<BizResult>>

    abstract suspend fun fetchReports(type: String): Result<ArrayList<Report>>

    abstract suspend fun fetchBusiness(businessId: String): Result<Business>

    abstract suspend fun fetchCategories(): Result<ArrayList<Category>>

    abstract suspend fun uploadBusinessImage(imgPath: String): Result<String>

    abstract suspend fun addNewBusiness(business: Business): Result<Business>

    abstract suspend fun addNewReport(report: ReportRequest): Result<Report>

    abstract suspend fun deleteReport(reportId: String): Result<Boolean>

}