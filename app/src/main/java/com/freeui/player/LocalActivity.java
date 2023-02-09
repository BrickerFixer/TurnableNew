package com.freeui.player;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;

public class LocalActivity extends AppCompatActivity {
    Intent serviceIntent = new Intent(this, ExoplayerService.class);
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);
        Button addtr = (Button) findViewById(R.id.addfromlocbtt);
        addtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(intent, 2);
            }
        });
    }
}