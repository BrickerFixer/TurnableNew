package com.freeui.player;


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

public class QueueActivity extends AppCompatActivity {
    Intent serviceIntent;

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
            list.add(new QueueData(player.getMediaMetadata().title.toString(),
                    player.getMediaMetadata().artist.toString(),
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