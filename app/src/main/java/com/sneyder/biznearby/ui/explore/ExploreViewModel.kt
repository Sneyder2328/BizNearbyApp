package com.sneyder.biznearby.ui.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sneyder.biznearby.utils.base.BaseViewModel
import javax.inject.Inject

class ExploreViewModel @Inject constructor() : BaseViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}