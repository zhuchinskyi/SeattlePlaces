package com.dzhucinski.seattleplaces.repository

import com.dzhucinski.seattleplaces.storage.PlacesDatabase
import com.dzhucinski.seattleplaces.storage.Venue
import java.util.concurrent.*

/**
 * Created by Denis Zhuchinski on 4/10/19.
 *
 * The entry point to favorite/unfavorite venue.
 *
 * Pretty simple for now, has potential to scale up in the future.
 *
 * Acts as an abstraction layer between data and UI.
 *
 */
interface FavoritesRepository {
    suspend fun getItemIds(): Set<String>
    fun add(id: String)
    fun remove(id: String)
    suspend fun isInFavorites(id: String): Venue?
}


class FavoritesRepositoryImpl(private val placesDatabase: PlacesDatabase, private val executor: Executor) :
    FavoritesRepository {

    override suspend fun isInFavorites(id: String): Venue? = placesDatabase.venueDao().isFavorite(id)

    override fun add(id: String) {
        executor.execute { placesDatabase.venueDao().insertVenue(Venue(id)) }
    }

    override fun remove(id: String) {
        executor.execute { placesDatabase.venueDao().deleteById(id) }
    }

    override suspend fun getItemIds(): Set<String> {
        return toSet(placesDatabase.venueDao().getVenuesAsync())
    }

    private fun toSet(venues: List<Venue>): Set<String> {
        val set = hashSetOf<String>()
        venues.forEach {
            set.add(it.id)
        }
        return set
    }
}