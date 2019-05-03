package com.dzhucinski.seattleplaces.repository

import com.dzhucinski.seattleplaces.network.DetailsResponse
import com.dzhucinski.seattleplaces.network.FoursquareService
import com.dzhucinski.seattleplaces.network.SearchResponse
import retrofit2.Response

/**
 * Created by Denis Zhuchinski on 4/10/19.
 *
 * Simple repository to get venues and venue details relevant to the search input
 */

interface PlacesRepository {
    suspend fun search(query: String, near: String, limit: Int): Response<SearchResponse>
    suspend fun getDetails(id: String): Response<DetailsResponse>
}

class PlacesRepositoryImpl(
    private val foursquareService: FoursquareService
) : PlacesRepository {

    override suspend fun search(query: String, near: String, limit: Int) =
        foursquareService.searchAsync(query, near, limit).await()

    override suspend fun getDetails(id: String): Response<DetailsResponse> =
        foursquareService.getDetailsAsync(id).await()
}