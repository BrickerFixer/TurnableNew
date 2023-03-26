package com.freeui.player

import androidx.room.*

@Dao
interface TrackDao {
    @Query("SELECT * FROM Track")
    fun getAll(): List<Track>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(track: Track)
    @Delete
    fun delete(track: Track)
    @Query("DELETE FROM Track")
    fun deleteAll()
}