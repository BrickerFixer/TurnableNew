package com.freeui.player;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;

public class ExoplayerService extends Service {
    static ExoPlayer player;
    PlayerBinder binder = new PlayerBinder();
    MediaSessionCompat session;
    MediaSessionConnector sessionConnector;
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    public void onCreate(Bundle savedInstanceState){

    }
    public int onStartCommand(Intent intent, int flags, int startid){
        String mediaItemUri = intent.getStringExtra("mediaitem");
        initExo(mediaItemUri);
        return super.onStartCommand(intent, flags, startid);
    }

    public void onDestroy(){
        if (player != null){
            player.release();
        }
    }
    private void initExo(String mediaItemUri){
        if (player == null && mediaItemUri == null){
            player = new ExoPlayer.Builder(getApplicationContext()).build();
            session = new MediaSessionCompat(this, "TurnableService");
            sessionConnector = new MediaSessionConnector(session);
            sessionConnector.setPlayer(player);
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
    private void externalDevicesController(){
    }
    class PlayerBinder extends Binder {
        ExoplayerService getService(){
            return ExoplayerService.this;
        }
    }
}