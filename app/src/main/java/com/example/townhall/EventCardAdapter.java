package com.example.townhall;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class EventCardAdapter extends RecyclerView.Adapter<EventCardAdapter.ViewHolder> {

    private ArrayList<EventHelper> eventList;
    private OnItemClickListener onItemClickListener;

    public EventCardAdapter(ArrayList<EventHelper> eventList, OnItemClickListener onItemClickListener) {
        this.eventList = eventList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_cardview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventHelper event = eventList.get(position);
        holder.eventImage.setImageResource(event.getImage());
        holder.dateDay.setText(event.getDay());
        holder.dateMonth.setText(event.getMonth());
        holder.eventTitle.setText(event.getTitle());
        holder.eventLocation.setText(event.getLocation());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView dateDay, dateMonth, eventTitle, eventLocation;

        public ViewHolder(View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.event_image);
            dateDay = itemView.findViewById(R.id.date_day);
            dateMonth = itemView.findViewById(R.id.date_month);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventLocation = itemView.findViewById(R.id.event_location);
        }
    }
}

