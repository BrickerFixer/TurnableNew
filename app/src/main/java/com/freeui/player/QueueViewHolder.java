package com.freeui.player;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

public class QueueViewHolder
        extends RecyclerView.ViewHolder {
    TextView trackName;
    ShapeableImageView thumb;
    TextView artist;
    View view;

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
        view  = itemView;
    }
}