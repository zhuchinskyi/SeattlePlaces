package com.dzhucinski.seattleplaces.repository

import androidx.lifecycle.LiveData
import com.dzhucinski.seattleplaces.storage.Venue

/**
 * Created by Denis Zhuchinski on 4/10/19.
 *
 * The entry point to favorite/unfavorite venue.
 *
 * Pretty simple for now, has potential to scale up in the future.
 *
 */
interface FavoritesRepository {

    fun getItemIds(): LiveData<Set<String>>

    fun add(id: String)

    fun remove(id: String)

    fun isInFavorites(id: String): LiveData<Venue?>
}