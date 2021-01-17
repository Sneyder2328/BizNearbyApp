package com.sneyder.biznearby.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.auth.LogInRequest
import com.sneyder.biznearby.data.model.auth.TypeLogin
import com.sneyder.biznearby.data.model.user.UserProfile
import com.sneyder.biznearby.data.repository.UserRepository
import com.sneyder.biznearby.utils.base.BaseViewModel
import com.sneyder.biznearby.utils.coroutines.CoroutineContextProvider
import com.sneyder.biznearby.utils.debug
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogInViewModel
@Inject constructor(
    private val userRepository: UserRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    val userLoggedIn by lazy { MutableLiveData<Result<UserProfile>>() }

    fun logIn(
        email: String,
        password: String,
        typeLogin: TypeLogin
    ) {
        debug("logIn $email $password $typeLogin")
        viewModelScope.launch {
            val result = withContext(IO) {
                userRepository.logIn(
                    LogInRequest(
                        email = email,
                        password = password,
                        typeLogin = typeLogin.type
                    )
                )
            }
            userLoggedIn.value = result
        }
    }

}