package com.sneyder.biznearby.ui.add_business_image

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.repository.BusinessRepository
import com.sneyder.biznearby.utils.base.BaseViewModel
import com.sneyder.biznearby.utils.coroutines.CoroutineContextProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddBizImageViewModel
@Inject constructor(
    private val businessRepository: BusinessRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    val imageUploadResult by lazy { MutableLiveData<Result<String>>() }

    fun uploadBusinessImage(imagePath: String) {
        imageUploadResult.value = Result()
        viewModelScope.launch {
            val result = withContext(IO) { businessRepository.uploadBusinessImage(imagePath) }
            imageUploadResult.value = result
        }

    }

}