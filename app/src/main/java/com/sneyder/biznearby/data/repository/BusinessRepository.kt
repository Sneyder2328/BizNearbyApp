package com.sneyder.biznearby.data.repository

import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.business.Business
import com.sneyder.biznearby.data.model.business.Category

abstract class BusinessRepository {

    abstract suspend fun fetchCategories(): Result<ArrayList<Category>>

    abstract suspend fun uploadBusinessImage(imgPath: String): Result<String>

    abstract suspend fun addNewBusiness(business: Business): Result<Business>

}