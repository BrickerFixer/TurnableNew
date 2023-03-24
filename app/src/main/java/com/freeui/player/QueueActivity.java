package com.freeui.player;


import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;

import com.google.android.exoplayer2.ExoPlayer;

public class QueueActivity extends AppCompatActivity {

    private ServiceConnection sConn;
    ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_queue);
        Button rm = findViewById(R.id.remove);
        Intent serviceIntent = new Intent(this, ExoplayerService.class);
        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                rm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       player.removeMediaItem(player.getCurrentMediaItemIndex());
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
    @Override
    public void onStop(){
        unbindService(sConn);
        super.onStop();
    }
}