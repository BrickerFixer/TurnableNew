package com.freeui.player;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.google.android.exoplayer2.ExoPlayer;

public class PositionLiveData extends LiveData<Integer> {

    private int updateInterval = 1000; // Update interval in milliseconds
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (player != null){
                setValue((int) player.getCurrentPosition());
            }else{
                setValue(0);
            }
            handler.postDelayed(this, updateInterval);
        }
    };
    private ExoPlayer player;

    public PositionLiveData(ExoPlayer player) {
        this.player = player;
    }

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
