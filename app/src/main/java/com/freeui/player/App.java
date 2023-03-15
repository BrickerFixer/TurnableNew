package com.freeui.player;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.IBinder;

import androidx.room.Room;

import com.google.android.exoplayer2.ExoPlayer;

public class App extends Application {

    public static App instance;

    private TrackDatabase database;

    public static Intent serviceIntent;


    @Override
    public void onCreate() {
        super.onCreate();
        serviceIntent = new Intent(this, ExoplayerService.class);
        startForegroundService(serviceIntent);
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
