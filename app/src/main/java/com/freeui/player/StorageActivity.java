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
        list.add(new DemoQueueData("Если у вас нету тёти",
                "Киноконцерн \"Мосфильм\"",
                "", "https://cdn.discordapp.com/attachments/1080089637529341992/1109117331671629894/7lR9nDzLKXE.opus"));
        list.add(new DemoQueueData("Остров невезения",
                "Киноконцерн \"Мосфильм\"",
                "", "https://cdn.discordapp.com/attachments/1080089637529341992/1109117394544250910/goFIxxWru78.opus"));
        list.add(new DemoQueueData("Песенка о медведях",
                "Киноконцерн \"Мосфильм\"",
                "", "https://cdn.discordapp.com/attachments/1080089637529341992/1109117445895094314/dExwT_pEr9w.opus"));
        list.add(new DemoQueueData("Если б я был султан",
                "Киноконцерн \"Мосфильм\"",
                "", "https://cdn.discordapp.com/attachments/1080089637529341992/1109117494146388120/39GD6_qgcLI.opus"));
        list.add(new DemoQueueData("Like When We Were Kids",
                "Harrison",
                "", "https://cdn.discordapp.com/attachments/1080089637529341992/1093487346936184872/Harrison_-_Like_When_We_Were_Kids.mp4"));
        list.add(new DemoQueueData("DAYDREAM",
                "ESPIRIT",
                "", "https://cdn.discordapp.com/attachments/1080089637529341992/1091701118997909554/ESPRIT___DAYDREAM.m4a"));
        list.add(new DemoQueueData("High Hopes (Harrison Edit)",
                "SOS Band & Harrison",
                "", "https://cdn.discordapp.com/attachments/1080089637529341992/1080157643101962240/SOS_Band_-_High_Hopes_Harrison_Edit.opus"));
        list.add(new DemoQueueData("Allure",
                "System96",
                "", "https://cdn.discordapp.com/attachments/1080089637529341992/1080157585749057626/System96_-_Allure.opus"));
        list.add(new DemoQueueData("Up",
                "Olly Murs",
                "", "https://cdn.discordapp.com/attachments/766991746856779806/1093563927859830916/Up.opus"));
        list.add(new DemoQueueData("Blossom",
                "Milky Chance",
                "", "https://cdn.discordapp.com/attachments/766991746856779806/1093563928711282781/Blossom.opus"));
        list.add(new DemoQueueData("broken",
                "lovelytheband",
                "", "https://cdn.discordapp.com/attachments/766991746856779806/1093563929055207464/broken.opus"));
        list.add(new DemoQueueData("Change",
                "ChurChill",
                "", "https://cdn.discordapp.com/attachments/766991746856779806/1093563928363151390/Change.opus"));
        return list;
    }

    @Override
    public void clickItem(int position) {
        serviceIntent.putExtra("mediaitem", list.get(position).uri);
        startForegroundService(serviceIntent);
    }
}