package com.freeui.player;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

public class DemoQueueViewHolder
        extends RecyclerView.ViewHolder {
    TextView trackName;
    ShapeableImageView thumb;
    TextView artist;
    View view;
    CardView card;

    DemoQueueViewHolder(View itemView)
    {
        super(itemView);
        trackName
                = (TextView)itemView
                .findViewById(R.id.demotracknamecard);
        artist
                = (TextView)itemView
                .findViewById(R.id.demoartistcard);
        thumb
                = (ShapeableImageView)itemView
                .findViewById(R.id.demoThumbnailcard);
        card
                =(CardView)itemView
                .findViewById(R.id.demoTrackCardView);
        view  = itemView;
    }
}