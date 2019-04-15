package com.dzhucinski.seattleplaces.storage

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Created by Denis Zhuchinski on 4/13/19.
 */
const val DB_NAME = "places.db"

@Database(entities = [Venue::class], version = 1)
abstract class PlacesDatabase : RoomDatabase() {
    abstract fun venueDao(): VenueDao
}