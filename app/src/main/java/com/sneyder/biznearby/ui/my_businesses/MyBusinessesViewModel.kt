package com.sneyder.biznearby.ui.my_businesses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.business.Business
import com.sneyder.biznearby.data.repository.BusinessRepository
import com.sneyder.biznearby.utils.base.BaseViewModel
import com.sneyder.biznearby.utils.coroutines.CoroutineContextProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MyBusinessesViewModel
@Inject constructor(
    private val businessRepository: BusinessRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    val myBusinesses by lazy { MutableLiveData<Result<List<Business>>>() }

    fun loadMyBusinesses() {
        myBusinesses.value = Result()
        viewModelScope.launch {
            val result = withContext(IO) { businessRepository.getMyBusinesses() }
            myBusinesses.value = result
        }
    }

}