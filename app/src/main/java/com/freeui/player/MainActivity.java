package com.freeui.player;

import static com.freeui.player.ExoplayerService.am;
import static com.freeui.player.ExoplayerService.dao;
import static com.freeui.player.ExoplayerService.focusRequest;
import static com.freeui.player.ExoplayerService.player;
import static com.freeui.player.ExoplayerService.session;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;



import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    public static final int PICK_AUDIO_REQUEST = 1;
    Uri uri;
    ServiceConnection sConn;
    Intent serviceIntent = App.serviceIntent;
    private boolean bound;
    Intent intent = new Intent();

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Intent serviceIntent = new Intent(this, ExoplayerService.class);
            uri = data.getData();
            serviceIntent.putExtra("mediaitem", uri.toString());
            try {
                getApplicationContext().grantUriPermission(getApplicationContext().getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            }
            catch (SecurityException e) {
                Log.e("", e.toString());
            }
            try {
                int takeFlags = intent.getFlags();
                takeFlags &= (Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getApplicationContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);
            } catch (SecurityException e) {
                Log.e("", e.toString());
            }
            startForegroundService(serviceIntent);
        }
    }
    public void openFileChooser(){
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }
    public void openFolderChooser(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }
    @Override
    protected void onStart() {
        bindService(serviceIntent, sConn, BIND_AUTO_CREATE);
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_main);
        ImageButton play = findViewById(R.id.playBtn);
        ImageButton next = findViewById(R.id.nextBtn);
        ImageButton prev = findViewById(R.id.prevBtn);
        TextView trackname = findViewById(R.id.artistcard);
        TextView artist = findViewById(R.id.composerName);
        TextView time = findViewById(R.id.timecode);
        SeekBar progress = findViewById(R.id.seekBar);
        StyledPlayerView artwork = findViewById(R.id.imageView);
        ImageButton repeat = findViewById(R.id.repeat);
        ImageButton shuffle = findViewById(R.id.shuffle);
        ImageButton local = findViewById(R.id.local);
        ImageButton net = findViewById(R.id.net);
        ImageButton queue = findViewById(R.id.queue);
        ImageButton settings = findViewById(R.id.settings);
        ImageView status = findViewById(R.id.status);
        Intent toStorage = new Intent(this, StorageActivity.class);
        Intent toSettings = new Intent(this, PlayerSettingsActivity.class);
        Intent toQueue = new Intent(this, QueueActivity.class);
        ConstraintLayout grad = findViewById(R.id.grad);
        AnimationDrawable anim = (AnimationDrawable) grad.getBackground();
        anim.setEnterFadeDuration(3000);
        anim.setExitFadeDuration(3000);
        PositionLiveData positionLiveData = new PositionLiveData(player);
        positionLiveData.observe(this, position -> {
            long timeMs = player.getDuration();
            if (player.getDuration() <= 0L){
                timeMs = 0L;
            }
            long totalSeconds = timeMs / 1000;
            long totalMinutes = totalSeconds / 60;
            long playingMs = player.getCurrentPosition();
            long playingSeconds = playingMs / 1000;
            long playingMinutes = playingSeconds / 60;
            time.setText(String.format("%02d", playingMinutes % 60) + ":" + String.format("%02d", playingSeconds % 60) + "/" + String.format("%02d", totalMinutes % 60) + ":" + String.format("%02d", totalSeconds % 60));
            progress.setProgress((int) ((player.getCurrentPosition() * 100) / player.getDuration()));
        });
        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                ExoplayerService exoService = ((ExoplayerService.PlayerBinder)binder).getService();
                bound = true;
                player.addListener(new Player.Listener() {
                    @Override
                    public void onIsLoadingChanged(boolean isLoading) {
                        if (player.isLoading()){
                            play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sync_48px));
                        } else {
                            play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_48px));
                        }
                    }
                });
                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlayerError(@NonNull PlaybackException error) {
                        status.setVisibility(View.VISIBLE);
                        status.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.err));
                        trackname.setText(R.string.error);
                        artist.setText(R.string.error_hint);
                        Toast.makeText(getApplicationContext(), error.getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                        player.removeMediaItem(player.getCurrentMediaItemIndex());
                        dao.deleteAll();
                        Toast.makeText(getApplicationContext(), R.string.err_db_tip,
                                Toast.LENGTH_LONG).show();
                    }
                });
                artwork.setPlayer(player);
                shuffle.setOnClickListener(view -> {
                    if(player.getShuffleModeEnabled()){
                        player.setShuffleModeEnabled(false);
                        shuffle.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shuffle_48px));
                    }else if (!player.getShuffleModeEnabled()){
                        player.setShuffleModeEnabled(true);
                        shuffle.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shuffle_on_48px));
                    }
                });
                local.setOnClickListener(view -> openFileChooser());
                local.setOnLongClickListener(v -> {
                    openFolderChooser();
                    return false;
                });
                net.setOnClickListener(view -> startActivity(toStorage));
                queue.setOnClickListener(view -> startActivity(toQueue));
                settings.setOnClickListener(view -> startActivity(toSettings));
                repeat.setOnClickListener(view -> {
                    if (player.getRepeatMode() == Player.REPEAT_MODE_OFF){
                        player.setRepeatMode(Player.REPEAT_MODE_ALL);
                        repeat.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.repeat_on_48px));
                    }else if(player.getRepeatMode() == Player.REPEAT_MODE_ALL){
                        player.setRepeatMode(Player.REPEAT_MODE_ONE);
                        repeat.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.repeat_one_on_48px));
                    }else{
                        player.setRepeatMode(Player.REPEAT_MODE_OFF);
                        repeat.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.repeat_48px));
                    }
                });
                player.addListener(new Player.Listener() {
                    @Override
                    public void onTracksChanged(Tracks tracks) {
                        if (player.getMediaItemCount() == 0){
                            status.setVisibility(View.VISIBLE);
                        }
                        if (player.getMediaMetadata().title == null) {
                            try {
                                trackname.setText(Objects.requireNonNull(Objects.requireNonNull(player.getCurrentMediaItem()).localConfiguration).uri.getLastPathSegment());
                            } catch (NullPointerException e){
                                trackname.setText(R.string.unknown_track);
                            }
                        } else {
                            trackname.setText(player.getMediaMetadata().title);
                        }
                        if (player.getMediaMetadata().title == null) {
                            artist.setText(R.string.unknown_artist);
                        } else {
                            artist.setText(player.getMediaMetadata().artist);
                        }
                        status.setVisibility(View.INVISIBLE);
                    }
                });
                player.addListener(new Player.Listener() {
                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        if (isPlaying) {
                            play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_48px));
                            anim.start();
                        } else {
                            play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_arrow_48px));
                            anim.stop();
                        }
                    }
                });
                play.setOnClickListener(view -> {
                    if (player.isPlaying()) {
                        player.pause();
                    } else {
                        am.requestAudioFocus(focusRequest);
                        player.play();
                    }
                });
                next.setOnClickListener(v -> player.seekToNextMediaItem());
                prev.setOnClickListener(v -> player.seekToPreviousMediaItem());
                progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if(player.getMediaItemCount() != 0) {
                            player.seekTo((progress.getProgress() * player.getDuration()) / 100);
                        }
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                bound = false;

            }
        };
        bindService(serviceIntent, sConn, BIND_AUTO_CREATE);
        ExoPlayer player = ExoplayerService.player;
        AudioFocusRequest focusRequest = ExoplayerService.focusRequest;
        AudioManager am = ExoplayerService.am;
    }

    @Override
    public void onStop(){
        unbindService(sConn);
        super.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
