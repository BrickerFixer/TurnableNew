package com.freeui.player;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
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

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener;

import java.util.Objects;

public class ExoplayerService extends Service {
    static ExoPlayer player;
    PlayerBinder binder = new PlayerBinder();
    MediaSessionCompat session;
    MediaSessionConnector sessionConnector;
    static AudioFocusRequest focusRequest;
    static AudioAttributes playbackAttributes;
    static AudioManager am;
    static TrackDatabase tdb = App.getInstance().getDatabase();
    static TrackDao dao = tdb.trackDao();
    PlayerNotificationManager notificationManager;
    MediaControllerCompat mediaController;

    String NOW_PLAYING_CHANNEL_ID = "com.example.android.uamp.media.NOW_PLAYING";
    int NOW_PLAYING_NOTIFICATION_ID = 0xb339;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    AudioManager.OnAudioFocusChangeListener changeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                player.play();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                player.pause();
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {

    }

    public int onStartCommand(Intent intent, int flags, int startid) {
        String mediaItemUri = intent.getStringExtra("mediaitem");
        initExo(mediaItemUri);
        return super.onStartCommand(intent, flags, startid);
    }

    public void onDestroy() {
        if (player != null) {
            player.release();
            notificationManager.setPlayer(null);
        }
    }

    private void initExo(String mediaItemUri) {
        if (player == null && mediaItemUri == null) {
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
            session.setActive(true);
            NotificationListener notificationListener = new NotificationListener() {
                @Override
                public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                    stopForeground(true);
                    NotificationListener.super.onNotificationCancelled(notificationId, dismissedByUser);
                }

                @Override
                public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                    startForeground(notificationId, notification);
                    NotificationListener.super.onNotificationPosted(notificationId, notification, ongoing);
                }
            };
            mediaController = new MediaControllerCompat(this, session.getSessionToken());

            if (dao.getAll().size() > 0) {
                Toast.makeText(getApplicationContext(), R.string.resume_tip,
                        Toast.LENGTH_SHORT).show();
                for (int i = 1; i <= dao.getAll().size(); i++) {
                    try{
                        player.addMediaItem(MediaItem.fromUri(Objects.requireNonNull(dao.getById(i)).getTrackuri()));
                    }catch (Exception e){
                        Toast.makeText(this, R.string.NullPointerErr, Toast.LENGTH_LONG);
                        e.printStackTrace();
                    }
                }
                player.prepare();
                player.play();
            }
            notificationManager = new PlayerNotificationManager.Builder(this, NOW_PLAYING_NOTIFICATION_ID, NOW_PLAYING_CHANNEL_ID)
                    .setMediaDescriptionAdapter(new DescriptionAdapter(mediaController))
                    .setNotificationListener(notificationListener)
                    .setChannelNameResourceId(R.string.channel_name)
                    .setChannelDescriptionResourceId(R.string.channel_desc)
                    .build();
            notificationManager.setPlayer(player);
            notificationManager.setMediaSessionToken(session.getSessionToken());
            notificationManager.setSmallIcon(R.drawable.ic_launcher_foreground);
            PositionLiveData liveData = new PositionLiveData(player);
        } else if (player.getMediaItemCount() == 0) {
            playMediaItem(mediaItemUri, player);
        } else {
            addMediaItem(mediaItemUri, player);
        }
    }

    public void addMediaItem(String mediaItemUri, ExoPlayer player) {
        player.addMediaItem(MediaItem.fromUri(mediaItemUri));
        Track tr = new Track(null, mediaItemUri);
        dao.insert(tr);
        player.prepare();
        Toast.makeText(getApplicationContext(), R.string.added_tip,
                Toast.LENGTH_SHORT).show();
    }

    public void playMediaItem(String mediaItemUri, ExoPlayer player) {
        player.addMediaItem(MediaItem.fromUri(mediaItemUri));
        Track tr = new Track(null, mediaItemUri);
        dao.insert(tr);
        player.prepare();
        Toast.makeText(getApplicationContext(), R.string.starting_tip,
                Toast.LENGTH_SHORT).show();
        player.play();
    }

    private void externalDevicesController() {
    }

    class PlayerBinder extends Binder {
        ExoplayerService getService() {
            return ExoplayerService.this;
        }
    }

    class DescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {

        private final MediaControllerCompat controller;

        private DescriptionAdapter(MediaControllerCompat controller) {
            this.controller = controller;
        }

        @Override
        public CharSequence getCurrentContentTitle(Player player) {
            if (player.getMediaItemCount() != 0 && player != null && player.getMediaMetadata().title != null || player.getMediaMetadata().title != ""){
                return player.getMediaMetadata().title;
            }
            return getString(R.string.unknown_track);
        }

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            return controller.getSessionActivity();
        }

        @Nullable
        @Override
        public CharSequence getCurrentContentText(Player player) {
            if (player.getMediaItemCount() != 0 && player != null && player.getMediaMetadata().artist != null || player.getMediaMetadata().title != ""){
                return player.getMediaMetadata().artist;
            }
            return getString(R.string.unknown_artist);
        }

        @Nullable
        @Override
        public CharSequence getCurrentSubText(Player player) {
            return PlayerNotificationManager.MediaDescriptionAdapter.super.getCurrentSubText(player);
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            return null;
        }
    }
}