package com.sneyder.biznearby.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.auth.SignUpRequest
import com.sneyder.biznearby.data.model.auth.TypeLogin
import com.sneyder.biznearby.data.model.user.UserProfile
import com.sneyder.biznearby.data.repository.UserRepository
import com.sneyder.biznearby.utils.base.BaseViewModel
import com.sneyder.biznearby.utils.coroutines.CoroutineContextProvider
import com.sneyder.biznearby.utils.debug
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class SignUpViewModel
@Inject constructor(
    private val userRepository: UserRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    val userCreated by lazy { MutableLiveData<Result<UserProfile>>() }

    fun signUp(
        email: String,
        password: String?,
        fullname: String,
        typeLogin: TypeLogin,
        imageProfilePath: String? = null,
        phoneNumber: String
    ) {
        debug("signUp")
        viewModelScope.launch {
            val result = withContext(IO) {
                userRepository.signUp(
                    SignUpRequest(
                        id = UUID.randomUUID().toString(),
                        email = email,
                        password = password,
                        fullname = fullname,
                        typeLogin = typeLogin.type,
                        imageProfilePath = imageProfilePath,
                        phoneNumber = phoneNumber
                    )
                )
            }
            userCreated.value = result
        }
    }
}