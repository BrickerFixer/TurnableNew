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
    private OnItemChildClickListener onItemChildClickListener;
    private OnItemClickListener onItemClickListener;
    List<QueueData> list = Collections.emptyList();
    public QueueAdapter(List<QueueData> list, OnItemChildClickListener onItemChildClickListener, OnItemClickListener onItemClickListener){
        this.list = list;
        this.onItemChildClickListener = onItemChildClickListener;
        this.onItemClickListener = onItemClickListener;
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
        holder.rm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                onItemChildClickListener.removeClick(position);
            }
        });
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                onItemClickListener.clickItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
