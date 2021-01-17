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

class HomeViewModel
@Inject constructor(
    private val userRepository: UserRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    val userProfile by lazy { MutableLiveData<UserProfile?>() }

    fun loadCurrentUserProfile() {
        val currentUserProfile = userRepository.getCurrentUserProfile()
        userProfile.value = currentUserProfile
        debug("currentUserProfile=$currentUserProfile")
        currentUserProfile?.let {
            viewModelScope.launch {
                val result = withContext(IO) { userRepository.fetchUserProfile(it.id) }
                if (result.error?.message == "HTTP 401 Unauthorized") logOut()
                debug("loadCurrentUserProfile result=$result")
            }
        }
    }

    fun logOut() {
        debug("logOut")
        viewModelScope.launch {
            val result = withContext(IO) { userRepository.logOut() }
            debug("userProfile.value = null")
            userProfile.value = null
        }
    }

}