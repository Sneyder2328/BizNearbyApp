package com.sneyder.biznearby.ui.add_business

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.business.Address
import com.sneyder.biznearby.data.model.business.Business
import com.sneyder.biznearby.data.model.business.Category
import com.sneyder.biznearby.data.model.business.Hour
import com.sneyder.biznearby.data.repository.BusinessRepository
import com.sneyder.biznearby.data.repository.UserRepository
import com.sneyder.biznearby.utils.base.BaseViewModel
import com.sneyder.biznearby.utils.coroutines.CoroutineContextProvider
import com.sneyder.biznearby.utils.debug
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddBusinessViewModel
@Inject constructor(
    private val userRepository: UserRepository,
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

    val businessCreated by lazy { MutableLiveData<Result<Business>>() }

    fun addNewBusiness(
        businessId: String,
        name: String,
        description: String,
        bannerUrl: String,
        address: Address,
        hours: List<Hour> = listOf(Hour(1, "06:30", "12:45")),
        phoneNumbers: List<String> = listOf("+5854632178"),
        categories: List<Int>,
        images: List<String>
    ) {
        debug("addNewBusiness view model")
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserProfile()?.id ?: return@launch
            businessCreated.value = Result()
            val result = withContext(IO) {
                businessRepository.addNewBusiness(
                    Business(
                        userId = userId,
                        businessId = businessId,
                        address = address,
                        name =name,
                        description=description,
                        bannerUrl=bannerUrl,
                        hours=hours,
                        phoneNumbers=phoneNumbers,
                        categories=categories,
                        images=images
                    )
                )
            }
            businessCreated.value = result
        }
    }

}