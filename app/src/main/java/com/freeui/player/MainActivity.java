package com.freeui.player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Tracks;
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
        artwork.setPlayer(player);
        addtoqueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackurl = mediaitem.getText().toString();
                if (player.getMediaItemCount() == 0) {
                    player.addMediaItem(MediaItem.fromUri(trackurl));
                    trackname.setText(player.getMediaMetadata().displayTitle);
                    artist.setText(player.getMediaMetadata().artist);
                    player.prepare();
                    player.play();
                } else {
                    player.addMediaItem(MediaItem.fromUri(trackurl));
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
                    int timeMs = (int) player.getDuration();
                    int totalSeconds = timeMs / 1000;
                    int totalMinutes = totalSeconds / 60;
                    int playingMs = (int) player.getCurrentPosition();
                    int playingSeconds = playingMs / 1000;
                    int playingMinutes = playingSeconds / 60;
                    progress.setProgress((int) ((player.getCurrentPosition() * 100) / player.getDuration()));
                    time.setText(String.format("%02d", playingMinutes % 60) + ":" + String.format("%02d", playingSeconds % 60) + "/" + String.format("%02d", totalMinutes % 60) + ":" + String.format("%02d", totalSeconds % 60));
                } else {
                    player.play();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.seekToNextMediaItem();
                int timeMs = (int) player.getDuration();
                int totalSeconds = timeMs / 1000;
                int totalMinutes = totalSeconds / 60;
                int playingMs = (int) player.getCurrentPosition();
                int playingSeconds = playingMs / 1000;
                int playingMinutes = playingSeconds / 60;
                progress.setProgress((int) ((player.getCurrentPosition() * 100) / player.getDuration()));
                time.setText(String.format("%02d", playingMinutes % 60) + ":" + String.format("%02d", playingSeconds % 60) + "/" + String.format("%02d", totalMinutes % 60) + ":" + String.format("%02d", totalSeconds % 60));
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.seekToPreviousMediaItem();
                int timeMs = (int) player.getDuration();
                int totalSeconds = timeMs / 1000;
                int totalMinutes = totalSeconds / 60;
                int playingMs = (int) player.getCurrentPosition();
                int playingSeconds = playingMs / 1000;
                int playingMinutes = playingSeconds / 60;
                progress.setProgress((int) ((player.getCurrentPosition() * 100) / player.getDuration()));
                time.setText(String.format("%02d", playingMinutes % 60) + ":" + String.format("%02d", playingSeconds % 60) + "/" + String.format("%02d", totalMinutes % 60) + ":" + String.format("%02d", totalSeconds % 60));
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
                int totalMinutes = totalSeconds / 60;
                int playingMs = (int) player.getCurrentPosition();
                int playingSeconds = playingMs / 1000;
                int playingMinutes = playingSeconds / 60;
                player.seekTo((progress.getProgress() * 1000) % player.getDuration());
                progress.setProgress((int) ((player.getCurrentPosition() * 100) / player.getDuration()));
                time.setText(String.format("%02d", playingMinutes % 60) + ":" + String.format("%02d", playingSeconds % 60) + "/" + String.format("%02d", totalMinutes % 60) + ":" + String.format("%02d", totalSeconds % 60));
            }
        });
    }
}