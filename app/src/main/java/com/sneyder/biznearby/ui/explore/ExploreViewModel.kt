package com.sneyder.biznearby.ui.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.business.BizResult
import com.sneyder.biznearby.data.repository.BusinessRepository
import com.sneyder.biznearby.utils.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExploreViewModel
@Inject constructor(
    private val businessRepository: BusinessRepository
) : BaseViewModel() {

    val resultBusinesses by lazy {
        MutableLiveData<Result<ArrayList<BizResult>>>()
    }

    fun searchBusinesses(query: String, latitude: Double, longitude: Double, radius: Int) {
        resultBusinesses.value = Result()
        viewModelScope.launch {
            val result = businessRepository.searchBusinesses(query, latitude, longitude, radius)
            resultBusinesses.value = result
        }
    }
}