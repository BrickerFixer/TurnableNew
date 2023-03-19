package com.freeui.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StorageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_storage);
        EditText mediaitemtf = (EditText) findViewById(R.id.queueItem);
        Button addtoqueuebtt = (Button) findViewById(R.id.addtoqueuebtt);
        Intent serviceIntent = new Intent(this, ExoplayerService.class);
        addtoqueuebtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackurl = mediaitemtf.getText().toString();
                serviceIntent.putExtra("mediaitem", trackurl);
                startForegroundService(serviceIntent);
            }
        });
    }
}