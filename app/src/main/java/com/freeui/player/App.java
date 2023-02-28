package com.freeui.player;

import android.app.Application;

import androidx.room.Room;

public class App extends Application {

    public static App instance;

    private TrackDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(getApplicationContext(), TrackDatabase.class, "trackdatabase")
                .allowMainThreadQueries()
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public TrackDatabase getDatabase() {
        return database;
    }
}
