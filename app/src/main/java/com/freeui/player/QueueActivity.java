package com.freeui.player;


import static com.freeui.player.ExoplayerService.dao;
import static com.freeui.player.R.string.rm_hint_everything;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MetadataRetriever;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Objects;

public class QueueActivity extends AppCompatActivity implements OnItemChildClickListener {
    Intent serviceIntent;
    String title;
    String artist;
    private ServiceConnection sConn;
    ExoPlayer player;
    QueueAdapter adapter;
    ArrayList<QueueData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_queue);
        list = new ArrayList<>();
        Button rm = findViewById(R.id.remove);
        RecyclerView queue = findViewById(R.id.queue);
        OnItemChildClickListener listener = this;
        serviceIntent = new Intent(this, ExoplayerService.class);
        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                list = addtoqueue();
                adapter = new QueueAdapter(list, listener);
                queue.setAdapter(adapter);
                queue.setLayoutManager(new LinearLayoutManager(getParent()));
                rm.setOnClickListener(v -> {
                    list.clear();
                    adapter.notifyItemRangeRemoved(0, player.getMediaItemCount());
                    player.removeMediaItems(0, player.getMediaItemCount());
                    dao.deleteAll();
                    Toast.makeText(getApplicationContext(), rm_hint_everything, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            @Override
            public void onBindingDied(ComponentName name) {
                ServiceConnection.super.onBindingDied(name);
            }

            @Override
            public void onNullBinding(ComponentName name) {
                ServiceConnection.super.onNullBinding(name);
            }
        };
        bindService(serviceIntent, sConn, BIND_AUTO_CREATE);
        player = ExoplayerService.player;
    }
    public ArrayList<QueueData> addtoqueue(){
        ArrayList<QueueData> list = new ArrayList<>();
        for (int i = 1; i <= player.getMediaItemCount(); i++){
            try {
                ListenableFuture<TrackGroupArray> trackGroupsFuture = MetadataRetriever.retrieveMetadata(getApplicationContext(), player.getMediaItemAt(i-1));
                title = Objects.requireNonNull(player.getMediaMetadata().title.toString()).toString();
            } catch (Exception e){
                title = getString(R.string.unknown_track);
                e.printStackTrace();
            }
           try {
                artist = Objects.requireNonNull(player.getMediaMetadata().artist.toString()).toString();
            } catch (Exception e){
                artist = getString(R.string.unknown_artist);
                e.printStackTrace();
            }
            list.add(new QueueData(title,
                    artist,
                    ""));
        }
        return list;
    }
    protected void onStart() {
        bindService(serviceIntent, sConn, BIND_AUTO_CREATE);
        super.onStart();
    }
    @Override
    public void onStop(){
        unbindService(sConn);
        super.onStop();
    }

    @Override
    public void removeClick(int position) {
        player.removeMediaItem(position);
        dao.delete(dao.getAll().get(position));
        list.remove(position);
        adapter.notifyItemRemoved(position);
        Toast.makeText(getApplicationContext(), getString(R.string.removeexacttrack) + (position + 1), Toast.LENGTH_LONG).show();
    }
}