package com.sneyder.biznearby.data.repository

import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.business.BizResult
import com.sneyder.biznearby.data.model.business.Business
import com.sneyder.biznearby.data.model.business.Category
import com.sneyder.biznearby.data.model.business.Report

abstract class BusinessRepository {

    abstract suspend fun searchBusinesses(query: String, latitude: Double, longitude: Double, radius: Int): Result<ArrayList<BizResult>>

    abstract suspend fun fetchReports(type: String): Result<ArrayList<Report>>

    abstract suspend fun fetchBusiness(businessId: String): Result<Business>

    abstract suspend fun fetchCategories(): Result<ArrayList<Category>>

    abstract suspend fun uploadBusinessImage(imgPath: String): Result<String>

    abstract suspend fun addNewBusiness(business: Business): Result<Business>

}