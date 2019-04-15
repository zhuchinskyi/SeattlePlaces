package com.dzhucinski.seattleplaces.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.dzhucinski.seattleplaces.storage.PlacesDatabase
import com.dzhucinski.seattleplaces.storage.Venue


class FavoritesRepositoryImpl(private val placesDatabase: PlacesDatabase) : FavoritesRepository {

    override fun isInFavorites(id: String): LiveData<Venue?> = placesDatabase.venueDao().isFavorite(id)

    override fun add(id: String) {
        Thread(Runnable { placesDatabase.venueDao().insertVenue(Venue(id)) }).start()
    }

    override fun remove(id: String) {
        Thread(Runnable { placesDatabase.venueDao().deleteById(id) }).start()
    }

    override fun getItemIds(): LiveData<Set<String>> {
        return Transformations.map(placesDatabase.venueDao().getVenues(), ::toSet)
    }

    private fun toSet(venues: List<Venue>): Set<String> {
        val set = hashSetOf<String>()
        venues.forEach {
            set.add(it.id)
        }
        return set
    }
}