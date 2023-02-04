package com.freeui.player;


import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

public class ExoplayerService extends Service {
    ExoPlayer player;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onCreate(Bundle savedInstanceState){

    }
    public int onStartCommand(Intent intent, int flags, int startid){
        String mediaItemUri = intent.getStringExtra("mediaitem");
        initExo(mediaItemUri);
        return super.onStartCommand(intent, flags, startid);
    }

    public void onDestroy(){

    }
    private void  initExo(String mediaItemUri){
        if (player == null){
            player = new ExoPlayer.Builder(getApplicationContext()).build();
        }else
        if (player.getMediaItemCount() == 0){
            playMediaItem(mediaItemUri, player);
        }else{
            addMediaItem(mediaItemUri, player);
        }
    }
    public void  addMediaItem(String mediaItemUri, ExoPlayer player){
        player.addMediaItem(MediaItem.fromUri(mediaItemUri));
        player.prepare();
        Toast.makeText(getApplicationContext(), "Added track to queue...",
                Toast.LENGTH_SHORT).show();
    }
    public void playMediaItem(String mediaItemUri, ExoPlayer player){
        player.addMediaItem(MediaItem.fromUri(mediaItemUri));
        player.prepare();
        Toast.makeText(getApplicationContext(), "Added track, starting playback...",
                Toast.LENGTH_SHORT).show();
        player.play();
    }
}