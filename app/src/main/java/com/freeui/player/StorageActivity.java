package com.freeui.player;

import static com.freeui.player.ExoplayerService.player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MetadataRetriever;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Objects;

public class StorageActivity extends AppCompatActivity implements OnItemClickListener {
    private ArrayList<DemoQueueData> list;
    private DemoQueueAdapter adapter;
    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_storage);
        EditText mediaitemtf = (EditText) findViewById(R.id.queueItem);
        Button addtoqueuebtt = (Button) findViewById(R.id.addtoqueuebtt);
        RecyclerView queue = findViewById(R.id.demoQueue);
        serviceIntent = new Intent(this, ExoplayerService.class);
        list = addtoqueue();
        OnItemClickListener cardlistener = this;
        adapter = new DemoQueueAdapter(list, cardlistener);
        queue.setAdapter(adapter);
        queue.setLayoutManager(new GridLayoutManager(getParent(), 2));
        addtoqueuebtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackurl = mediaitemtf.getText().toString();
                serviceIntent.putExtra("mediaitem", trackurl);
                startForegroundService(serviceIntent);
            }
        });
    }
    public ArrayList<DemoQueueData> addtoqueue() {
        ArrayList<DemoQueueData> list = new ArrayList<>();
        list.add(new DemoQueueData("Example",
                "Test",
                "", "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"));
        return list;
    }

    @Override
    public void clickItem(int position) {
        serviceIntent.putExtra("mediaitem", list.get(position).uri);
        startForegroundService(serviceIntent);
    }
}