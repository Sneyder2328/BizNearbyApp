package com.sneyder.biznearby.ui.add_report

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.business.Report
import com.sneyder.biznearby.data.model.business.ReportRequest
import com.sneyder.biznearby.data.repository.BusinessRepository
import com.sneyder.biznearby.utils.base.BaseViewModel
import com.sneyder.biznearby.utils.coroutines.CoroutineContextProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class AddReportViewModel
@Inject constructor(
    private val businessRepository: BusinessRepository,
    coroutineContextProvider: CoroutineContextProvider
) :
    BaseViewModel(coroutineContextProvider) {

    val reportCreated by lazy { MutableLiveData<Result<Report>>() }

    fun addReport(businessId: String, title: String, description: String) {
        reportCreated.value = Result()
        viewModelScope.launch {
            val result = withContext(IO) {
                businessRepository.addNewReport(
                    ReportRequest(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        businessId = businessId
                    )
                )
            }
            reportCreated.value = result
        }
    }

}