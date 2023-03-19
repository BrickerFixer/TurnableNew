package com.freeui.player

import androidx.room.*

@Dao
interface TrackDao {
    @Query("SELECT trackuri FROM Track")
    fun getAll(): List<Track>

    @Query("SELECT trackuri FROM track WHERE id = :id")
    fun getById(id: Int): Track
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(track: Track)
    @Delete
    fun delete(track: Track)
    @Query("DELETE FROM Track")
    fun deleteAll()
}