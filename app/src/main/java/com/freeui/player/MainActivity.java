package com.freeui.player;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_main);
        ExoPlayer player = new ExoPlayer.Builder(this).build();
        FloatingActionButton play = findViewById(R.id.playBtn);
        FloatingActionButton next = findViewById(R.id.nextBtn);
        FloatingActionButton prev = findViewById(R.id.prevBtn);
        TextView trackname = findViewById(R.id.trackName);
        TextView artist = findViewById(R.id.composerName);
        TextView time = findViewById(R.id.timecode);
        SeekBar progress = findViewById(R.id.seekBar);
        EditText mediaitem = findViewById(R.id.MediaItem);
        Button addtoqueue = findViewById(R.id.addtoqueue);
        StyledPlayerView artwork = findViewById(R.id.imageView);
        ImageButton repeat = findViewById(R.id.repeat);
        ImageButton shuffle = findViewById(R.id.shuffle);
        ImageButton local = findViewById(R.id.local);
        ImageButton net = findViewById(R.id.net);
        ImageButton eq = findViewById(R.id.EQ);
        ImageButton settings = findViewById(R.id.settings);
        ImageView status = findViewById(R.id.status);
        Intent myIntent = new Intent(this, StorageActivity.class);
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                status.setVisibility(View.VISIBLE);
                status.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.err));
                trackname.setText("Playback error!");
                artist.setText("Probably it's because the link doesn't have a raw mediafile, the mediaItem has been removed to not cause issues.");
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
                if(player.getShuffleModeEnabled() == true){
                    player.setShuffleModeEnabled(false);
                    shuffle.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shuffle_48px));
                }else if (player.getShuffleModeEnabled() == false){
                    player.setShuffleModeEnabled(true);
                    shuffle.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shuffle_on_48px));
                }
            }
        });
        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(myIntent);
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
        addtoqueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackurl = mediaitem.getText().toString();
                if (player.getMediaItemCount() == 0) {
                    player.addMediaItem(MediaItem.fromUri(trackurl));
                    trackname.setText(player.getMediaMetadata().displayTitle);
                    artist.setText(player.getMediaMetadata().artist);
                    Toast.makeText(getApplicationContext(), "Added track, starting playback...",
                            Toast.LENGTH_SHORT).show();
                    player.prepare();
                    player.play();
                } else {
                    player.addMediaItem(MediaItem.fromUri(trackurl));
                    Toast.makeText(getApplicationContext(), "Added track to queue...",
                            Toast.LENGTH_SHORT).show();
                    player.prepare();
                }
            }
        });
        player.addListener(new Player.Listener() {
            @Override
            public void onTracksChanged(Tracks tracks) {
                if (player.getMediaMetadata().title == null) {
                    trackname.setText("Unknown Track");
                } else {
                    trackname.setText(player.getMediaMetadata().title);
                }
                if (player.getMediaMetadata().title == null) {
                    artist.setText("Unknown Artist");
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
                    play.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_arrow_48px));
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
                int timeMs = (int) player.getDuration();
                int totalSeconds = timeMs / 1000;
                player.seekTo(1000*((progress.getProgress() * 200) / totalSeconds));
            }
        });
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