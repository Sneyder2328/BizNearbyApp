package com.sneyder.biznearby.ui.business_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sneyder.biznearby.data.model.business.Business
import com.sneyder.biznearby.data.repository.BusinessRepository
import com.sneyder.biznearby.utils.base.BaseViewModel
import com.sneyder.biznearby.utils.coroutines.CoroutineContextProvider
import com.sneyder.biznearby.data.model.Result
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BusinessDetailsViewModel
@Inject constructor(
    private val businessRepository: BusinessRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    val business by lazy { MutableLiveData<Result<Business>>() }

    fun loadBusinessById(businessId: String){
        business.value = Result()
        viewModelScope.launch {
            val result = withContext(IO){ businessRepository.fetchBusiness(businessId) }
            business.value = result
        }
    }

}