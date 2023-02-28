package com.freeui.player;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = Track.class)
public abstract class TrackDatabase extends RoomDatabase {
    public abstract TrackDao trackDao();
}
