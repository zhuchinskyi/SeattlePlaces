package com.dzhucinski.seattleplaces.repository

import androidx.lifecycle.LiveData

/**
 * Created by Denis Zhuchinski on 4/10/19.
 *
 * Simple repository to get venues and venue details relevant to the search input
 */

interface PlacesRepository {

    fun search(query: String, near: String, limit: Int)

    fun getDetails(id: String): LiveData<VenueResponse>

    fun getSearchResultLiveData(): LiveData<PlacesResponse>
}