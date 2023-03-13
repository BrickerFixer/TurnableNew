package com.freeui.player;

import android.app.Application;
import android.content.Intent;

import androidx.room.Room;

public class App extends Application {

    public static App instance;

    private TrackDatabase database;

    public static Intent serviceIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceIntent = new Intent(this, ExoplayerService.class);
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
