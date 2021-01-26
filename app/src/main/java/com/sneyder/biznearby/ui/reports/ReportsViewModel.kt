package com.sneyder.biznearby.ui.reports

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.business.Report
import com.sneyder.biznearby.data.repository.BusinessRepository
import com.sneyder.biznearby.utils.base.BaseViewModel
import com.sneyder.biznearby.utils.coroutines.CoroutineContextProvider
import com.sneyder.biznearby.utils.debug
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReportsViewModel
@Inject constructor(
    private val businessRepository: BusinessRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    val reports by lazy { MutableLiveData<com.sneyder.biznearby.data.model.Result<ArrayList<Report>>>() }

    fun fetchReports(type: String) {
        debug("fetchReports $type")
        reports.value = Result()
        viewModelScope.launch {
            val result = withContext(IO) { businessRepository.fetchReports(type) }
            reports.value = result
        }
    }

}