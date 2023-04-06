package com.freeui.player;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.google.android.exoplayer2.ExoPlayer;

public class PositionLiveData extends LiveData<Long> {

    private final int updateInterval = 1000; // Update interval in milliseconds
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final ExoPlayer player;

    public PositionLiveData(ExoPlayer player) {
        this.player = player;
    }
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (player != null){
                setValue(player.getCurrentPosition());
            }else{
                setValue(0000L);
            }
            handler.postDelayed(this, updateInterval);
        }
    };

    @Override
    protected void onActive() {
        super.onActive();
        handler.postDelayed(runnable, updateInterval);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        handler.removeCallbacks(runnable);
    }
}
