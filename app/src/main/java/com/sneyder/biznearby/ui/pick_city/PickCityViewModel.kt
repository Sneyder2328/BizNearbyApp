package com.sneyder.biznearby.ui.pick_city

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.model.CityLocation
import com.sneyder.biznearby.data.repository.LocationsRepository
import com.sneyder.biznearby.utils.base.BaseViewModel
import com.sneyder.biznearby.utils.coroutines.CoroutineContextProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PickCityViewModel
@Inject constructor(
    private val locationsRepository: LocationsRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    val locations by lazy { MutableLiveData<Result<ArrayList<CityLocation>>>() }

    fun getLocations(query: String){
        viewModelScope.launch {
            val result = withContext(IO){ locationsRepository.fetchLocations(query) }
            locations.value = result
        }
    }

}