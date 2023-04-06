package com.freeui.player;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class DemoQueueAdapter extends RecyclerView.Adapter<DemoQueueViewHolder> {
    private OnItemClickListener onItemClickListener;
    List<DemoQueueData> list = Collections.emptyList();
    public DemoQueueAdapter(List<DemoQueueData> list, OnItemClickListener onItemClickListener){
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }
    @NonNull
    @Override
    public DemoQueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tracklist_demo_card, parent, false);
        return new DemoQueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DemoQueueViewHolder holder, int position) {
        holder.artist.setText(list.get(position).artist);
        holder.trackName.setText(list.get(position).trackName);
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
