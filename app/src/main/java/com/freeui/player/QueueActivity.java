package com.freeui.player;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;

public class QueueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_queue);
        Button rm = findViewById(R.id.remove);
        Intent serviceIntent = new Intent(this, ExoplayerService.class);
        rm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackurl = "RMx63757272656e74";
                serviceIntent.putExtra("mediaitem", trackurl);
                startForegroundService(serviceIntent);
            }
        });
    }
}