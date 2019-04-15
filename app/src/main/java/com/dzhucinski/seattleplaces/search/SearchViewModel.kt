package com.dzhucinski.seattleplaces.search

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.*
import com.dzhucinski.seattleplaces.repository.PlacesRepository
import com.dzhucinski.seattleplaces.network.Venue
import com.dzhucinski.seattleplaces.repository.PlacesResponse
import com.dzhucinski.seattleplaces.repository.FavoritesRepository
import com.dzhucinski.seattleplaces.util.zip

/**
 * Created by Denis Zhuchinski on 4/11/19.
 *
 */
const val SEATTLE_LAT = 47.60621
const val SEATTLE_LNG = -122.33207
const val DEFAULT_IMAGE_SIZE = "88"
const val SEARCH_LOCATION = "Seattle,+WA"
const val DEFAULT_ITEMS_LIMIT = 30

class SearchViewModel(
    private val placesRepository: PlacesRepository,
    private val favoritesRepository: FavoritesRepository
) :
    ViewModel() {

    private var query: String = ""

    val liveData = MutableLiveData<List<VenueItem>>()
    val progressLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()

    private val searchResultObserver: Observer<Pair<PlacesResponse, Set<String>>> = Observer {
        val places = it.first
        val favorites = it.second

        progressLiveData.value = false
        if (query.isEmpty()) {
            liveData.value = emptyList()
        } else {
            liveData.value = convertToViewModel(places.venues, favorites)
        }

        if (places.errorMsg != null) {
            errorLiveData.value = places.errorMsg
        }
    }

    private val searchResultLiveData = placesRepository
        .getSearchResultLiveData()
        .zip(favoritesRepository.getItemIds())

    init {
        searchResultLiveData.observeForever(searchResultObserver)
    }

    fun performSearch(query: String) {
        this.query = query

        progressLiveData.value = true

        placesRepository.search(query, SEARCH_LOCATION, DEFAULT_ITEMS_LIMIT)
    }

    fun addToFavorites(id: String) {
        favoritesRepository.add(id)
    }

    fun removeFromFavorites(id: String) {
        favoritesRepository.remove(id)
    }

    private fun convertToViewModel(values: List<Venue>, favorites: Set<String>): List<VenueItem> {
        val venuesItemList = mutableListOf<VenueItem>()
        values.forEach {
            val venueItem =
                VenueItem(
                    it.id,
                    it.name,
                    getCategoryTitle(it.categories),
                    it.description ?: "",
                    it.location?.address ?: "",
                    it.location?.lat ?: 0.0,
                    it.location?.lng ?: 0.0,
                    it.canonicalUrl ?: "",
                    formatDistanceToString(getDistanceMiles(it.location?.lat, it.location?.lng)),
                    favorites.contains(it.id),
                    getImageUrl(it.categories)
                )
            venuesItemList.add(venueItem)
        }

        return venuesItemList
    }

    private fun getImageUrl(categories: List<Venue.Category>?): String {
        val primaryCategories = categories?.filter { it.primary }
        return if (primaryCategories.isNullOrEmpty()) {
            ""
        } else {
            primaryCategories[0].icon?.prefix + DEFAULT_IMAGE_SIZE + primaryCategories[0].icon?.suffix
        }
    }


    private fun getCategoryTitle(categories: List<Venue.Category>?): String {
        val items = categories?.filter { it.primary }
        return if (items.isNullOrEmpty()) {
            ""
        } else {
            items[0].name
        }
    }

    private fun formatDistanceToString(distanceMiles: Double): String {
        return String.format("%.2f miles of the city center", distanceMiles)
    }

    private fun getDistanceMiles(lat: Double?, lng: Double?): Double {
        return if (lat == null || lng == null || SEATTLE_LAT == lat && SEATTLE_LNG == lng) {
            0.0
        } else {
            val theta = SEATTLE_LNG - lng
            var dist = Math.sin(Math.toRadians(SEATTLE_LAT)) * Math.sin(Math.toRadians(lat)) +
                    Math.cos(Math.toRadians(SEATTLE_LAT)) * Math.cos(Math.toRadians(lat)) * Math.cos(
                Math.toRadians(theta)
            )
            dist = Math.acos(dist)
            dist = Math.toDegrees(dist)
            dist *= 60.0 * 1.1515
            dist
        }
    }

    data class VenueItem(
        val id: String,
        val title: String,
        val categoryTitle: String,
        val description: String,
        val address: String,
        val lat: Double,
        val lng: Double,
        val url: String,
        val distanceTitle: String,
        val isSelected: Boolean,
        val imageUrl: String
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(title)
            parcel.writeString(categoryTitle)
            parcel.writeString(description)
            parcel.writeString(address)
            parcel.writeDouble(lat)
            parcel.writeDouble(lng)
            parcel.writeString(url)
            parcel.writeString(distanceTitle)
            parcel.writeByte(if (isSelected) 1 else 0)
            parcel.writeString(imageUrl)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<VenueItem> {
            override fun createFromParcel(parcel: Parcel): VenueItem {
                return VenueItem(parcel)
            }

            override fun newArray(size: Int): Array<VenueItem?> {
                return arrayOfNulls(size)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        searchResultLiveData.removeObserver(searchResultObserver)
    }
}