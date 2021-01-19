package com.sneyder.biznearby.data.repository

import com.sneyder.biznearby.data.model.Result
import com.sneyder.biznearby.data.model.model.CityLocation

abstract class LocationsRepository {

    abstract suspend fun fetchLocations(query: String): Result<ArrayList<CityLocation>>

}