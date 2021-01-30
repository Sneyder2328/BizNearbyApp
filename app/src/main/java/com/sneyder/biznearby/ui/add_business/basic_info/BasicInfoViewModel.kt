package com.sneyder.biznearby.ui.add_business.basic_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sneyder.biznearby.data.model.business.Category
import com.sneyder.biznearby.data.repository.BusinessRepository
import com.sneyder.biznearby.utils.base.BaseViewModel
import com.sneyder.biznearby.utils.coroutines.CoroutineContextProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BasicInfoViewModel
@Inject constructor(
    private val businessRepository: BusinessRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    val categories by lazy { MutableLiveData<ArrayList<Category>>() }

    fun fetchCategories() {
        viewModelScope.launch {
            val result = withContext(IO) { businessRepository.fetchCategories() }
            if (result.success != null) {
                categories.value = result.success
            }
        }
    }

}