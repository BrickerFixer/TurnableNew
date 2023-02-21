package com.freeui.player;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener;

import java.util.List;
import java.util.Map;

public class ExoplayerService extends Service {
    static ExoPlayer player;
    PlayerBinder binder = new PlayerBinder();
    MediaSessionCompat session;
    MediaSessionConnector sessionConnector;
    static AudioFocusRequest focusRequest;
    static AudioAttributes playbackAttributes;
    static AudioManager am;
    private PlayerNotificationManager playerNotificationManager;
    private int notificationId = 2;
    UampNotificationManager notificationManager;
    NotificationListener listener = new NotificationListener() {
        @Override
        public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
            NotificationListener.super.onNotificationCancelled(notificationId, dismissedByUser);
        }

        @Override
        public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
            NotificationListener.super.onNotificationPosted(notificationId, notification, ongoing);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    AudioManager.OnAudioFocusChangeListener changeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                player.play();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                player.pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                player.pause();
            }
        }
    };
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
            notificationManager.hideNotification();
        }
    }
    private void initExo(String mediaItemUri){
        if (player == null && mediaItemUri == null){
            am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            player = new ExoPlayer.Builder(getApplicationContext()).build();
            playbackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(false)
                    .setWillPauseWhenDucked(true)
                    .setForceDucking(true)
                    .setOnAudioFocusChangeListener(changeListener)
                    .build();
            am.requestAudioFocus(focusRequest);
            session = new MediaSessionCompat(this, "TurnableService");
            sessionConnector = new MediaSessionConnector(session);
            sessionConnector.setPlayer(player);
            MediaSessionConnector.MediaMetadataProvider provider = new MediaSessionConnector.MediaMetadataProvider() {
                @Override
                public MediaMetadataCompat getMetadata(Player player) {
                    return null;
                }
            };
            session.setMetadata(provider.getMetadata(player));
            session.setActive(true);
            notificationManager = new UampNotificationManager(this, session.getSessionToken(), listener);
            notificationManager.showNotificationForPlayer(player);

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