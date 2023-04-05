package com.freeui.player;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

public class QueueViewHolder
        extends RecyclerView.ViewHolder {
    TextView trackName;
    ShapeableImageView thumb;
    TextView artist;
    ImageButton rm;
    View view;
    CardView card;

    QueueViewHolder(View itemView)
    {
        super(itemView);
        trackName
                = (TextView)itemView
                .findViewById(R.id.tracknamecard);
        artist
                = (TextView)itemView
                .findViewById(R.id.artistcard);
        thumb
                = (ShapeableImageView)itemView
                .findViewById(R.id.thumbnailcard);
        rm
                = (ImageButton)itemView
                .findViewById(R.id.rmqueueitembtt);
        card
                =(CardView)itemView
                .findViewById(R.id.trackCardView);
        view  = itemView;
    }
}