package com.sneyder.biznearby.ui.moderators

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.user.UserProfile
import com.sneyder.biznearby.data.repository.UserRepository
import com.sneyder.biznearby.utils.base.BaseViewModel
import com.sneyder.biznearby.utils.coroutines.CoroutineContextProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ModeratorsViewModel
@Inject constructor(
    private val userRepository: UserRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    val moderators by lazy { MutableLiveData<Result<List<UserProfile>>>() }

    fun fetchModerators() {
        moderators.value = Result()
        viewModelScope.launch {
            val result = withContext(IO) { userRepository.fetchModerators() }
            moderators.value = result
        }
    }

}