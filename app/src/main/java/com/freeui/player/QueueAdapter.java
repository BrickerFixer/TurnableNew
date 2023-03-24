package com.freeui.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;

import java.util.Collections;
import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueViewHolder> {
    List<QueueData> list = Collections.emptyList();
    public QueueAdapter(List<QueueData> list){
        this.list = list;
    }
    @NonNull
    @Override
    public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tracklist_card, parent, false);
        return new QueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueViewHolder holder, int position) {
        holder.artist.setText(list.get(position).artist);
        holder.trackName.setText(list.get(position).trackName);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
