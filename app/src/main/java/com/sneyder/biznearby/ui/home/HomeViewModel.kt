package com.sneyder.biznearby.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sneyder.biznearby.data.model.user.UserProfile
import com.sneyder.biznearby.data.repository.UserRepository
import com.sneyder.biznearby.utils.base.BaseViewModel
import com.sneyder.biznearby.utils.coroutines.CoroutineContextProvider
import com.sneyder.biznearby.utils.debug
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.auth.LogOutResponse

class HomeViewModel
@Inject constructor(
    private val userRepository: UserRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    val userProfile by lazy { MutableLiveData<Result<UserProfile?>?>() }
    val logOutResponse by lazy { MutableLiveData<Result<LogOutResponse>>() }

    fun loadCurrentUserProfile() {
        val currentUserProfile = userRepository.getCurrentUserProfile()
        userProfile.value = Result(success = currentUserProfile)
        debug("currentUserProfile=$currentUserProfile")
        currentUserProfile?.let {
            viewModelScope.launch {
                val result = withContext(IO) { userRepository.fetchUserProfile(it.id) }
                if (result.error?.message == "HTTP 401 Unauthorized") logOut()
                userProfile.value = result
                debug("loadCurrentUserProfile result=$result")
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            val result = withContext(IO) { userRepository.logOut() }
            logOutResponse.value = result
            userProfile.value = null
        }
    }
}