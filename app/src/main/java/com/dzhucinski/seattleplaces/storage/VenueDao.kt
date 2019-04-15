package com.dzhucinski.seattleplaces.storage

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Created by Denis Zhuchinski on 4/13/19.
 */
@Dao
interface VenueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVenue(venue: Venue)

    @Query("DELETE FROM Venue WHERE id = :id")
    fun deleteById(id: String)

    @Query("SELECT * FROM Venue")
    fun getVenues(): LiveData<List<Venue>>

    @Query("SELECT * FROM Venue WHERE id LIKE :id")
    fun isFavorite(id: String): LiveData<Venue?>
}