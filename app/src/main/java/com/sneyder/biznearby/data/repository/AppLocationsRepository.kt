package com.sneyder.biznearby.data.repository

import com.sneyder.biznearby.data.api.BizNearbyApi
import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.model.CityLocation
import com.sneyder.biznearby.data.preferences.PreferencesHelper
import com.sneyder.biznearby.utils.mapToResult
import javax.inject.Inject

class AppLocationsRepository
    @Inject constructor(
        private val bizNearbyApi: BizNearbyApi,
        private val prefs: PreferencesHelper,
    ): LocationsRepository() {

    override suspend fun fetchLocations(query: String): Result<ArrayList<CityLocation>> {
        return mapToResult({
            bizNearbyApi.searchCityLocations(
                query = query,
                limit = 10
            )
        })
    }
}