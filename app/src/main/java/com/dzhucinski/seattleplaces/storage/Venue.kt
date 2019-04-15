package com.dzhucinski.seattleplaces.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Denis Zhuchinski on 4/13/19.
 *
 * `A bit` simplified venue model to be stored on the disk with the aim to scale in the future.
 *
 */
@Entity
data class Venue(@PrimaryKey val id: String)
