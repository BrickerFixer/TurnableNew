package com.freeui.player;


import static com.freeui.player.ExoplayerService.dao;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;
import java.util.Objects;

public class QueueActivity extends AppCompatActivity {
    Intent serviceIntent;
    String title;
    String artist;
    String uri;
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
        serviceIntent = new Intent(this, ExoplayerService.class);
        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                list = addtoqueue();
                adapter = new QueueAdapter(list);
                queue.setAdapter(adapter);
                queue.setLayoutManager(new LinearLayoutManager(getParent()));
                rm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       player.removeMediaItem(player.getCurrentMediaItemIndex());
                       dao.delete(dao.getAll().get(player.getCurrentMediaItemIndex()));
                        Toast.makeText(getApplicationContext(), R.string.tip_remove, Toast.LENGTH_LONG);
                    }
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
}