package com.freeui.player;

import static com.freeui.player.ExoplayerService.player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_main);
        BitmapDrawable bitmapDrawable  = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.img));
        ImageView bg = findViewById(R.id.imageView2);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        bg.setImageDrawable(bitmapDrawable);
        ImageButton play = findViewById(R.id.playBtn);
        ImageButton next = findViewById(R.id.nextBtn);
        ImageButton prev = findViewById(R.id.prevBtn);
        TextView trackname = findViewById(R.id.trackName);
        TextView artist = findViewById(R.id.composerName);
        TextView time = findViewById(R.id.timecode);
        SeekBar progress = findViewById(R.id.seekBar);
        StyledPlayerView artwork = findViewById(R.id.imageView);
        ImageButton repeat = findViewById(R.id.repeat);
        ImageButton shuffle = findViewById(R.id.shuffle);
        ImageButton local = findViewById(R.id.local);
        ImageButton net = findViewById(R.id.net);
        ImageButton eq = findViewById(R.id.EQ);
        ImageButton settings = findViewById(R.id.settings);
        ImageView status = findViewById(R.id.status);
        Intent toStorage = new Intent(this, StorageActivity.class);
        Intent toLocal = new Intent(this, LocalActivity.class);
        Intent serviceIntent = new Intent(this, ExoplayerService.class);
        startService(serviceIntent);
        ServiceConnection sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                ExoplayerService exoService = ((ExoplayerService.PlayerBinder)binder).getService();
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
                    }
                });
                player.addListener(new MyEventListener(time, player, progress));
                artwork.setPlayer(player);
                shuffle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(player.getShuffleModeEnabled()){
                            player.setShuffleModeEnabled(false);
                            shuffle.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shuffle_48px));
                        }else if (!player.getShuffleModeEnabled()){
                            player.setShuffleModeEnabled(true);
                            shuffle.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shuffle_on_48px));
                        }
                    }
                });
                local.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(toLocal);
                    }
                });
                net.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(toStorage);
                    }
                });
                eq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                repeat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
                    }
                });
                player.addListener(new Player.Listener() {
                    @Override
                    public void onTracksChanged(Tracks tracks) {
                        if (player.getMediaMetadata().title == null) {
                            trackname.setText(R.string.unknown_track);
                        } else {
                            trackname.setText(player.getMediaMetadata().title);
                        }
                        if (player.getMediaMetadata().title == null) {
                            artist.setText(R.string.unknown_artist);
                        } else {
                            artist.setText(player.getMediaMetadata().artist);
                        }
                    }
                });
                player.addListener(new Player.Listener() {
                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        if (isPlaying) {
                            play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_48px));
                            status.setVisibility(View.INVISIBLE);
                        } else {
                            if (player.isLoading()){
                                play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sync_48px));
                            }else {
                                play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_arrow_48px));
                            }
                        }
                    }
                });
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (player.isPlaying() == true) {
                            player.pause();
                        } else {
                            player.play();
                        }
                    }
                });
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        player.seekToNextMediaItem();
                    }
                });
                prev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        player.seekToPreviousMediaItem();
                    }
                });
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
                            player.seekTo((progress.getProgress() * (int) player.getDuration()) / 100);
                        }
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(serviceIntent, sConn, BIND_AUTO_CREATE);
        ExoPlayer player = ExoplayerService.player;
    }
}
class MyEventListener implements ExoPlayer.Listener {
    private TextView mTextView;
    private ExoPlayer exoPlayer;
    private SeekBar mSeekBar;


    MyEventListener(TextView textView, ExoPlayer exoPlayer, SeekBar seekBar) {
        mTextView = textView;
        this.exoPlayer = exoPlayer;
        mSeekBar = seekBar;
    }
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            int timeMs = (int) exoPlayer.getDuration();
            int totalSeconds = timeMs / 1000;
            int totalMinutes = totalSeconds / 60;
            int playingMs = (int) exoPlayer.getCurrentPosition();
            int playingSeconds = playingMs / 1000;
            int playingMinutes = playingSeconds / 60;
            mTextView.setText(String.format("%02d", playingMinutes % 60) + ":" + String.format("%02d", playingSeconds % 60) + "/" + String.format("%02d", totalMinutes % 60) + ":" + String.format("%02d", totalSeconds % 60));
            mSeekBar.setProgress((int) ((exoPlayer.getCurrentPosition() * 100) / exoPlayer.getDuration()));
            mHandler.postDelayed(this, 1000);
        }
    };
    @Override
    public void onTimelineChanged(Timeline timeline, int reason) {
        // update the TextView with the current position
        mHandler.post(mUpdateTimeTask);
    }
}
