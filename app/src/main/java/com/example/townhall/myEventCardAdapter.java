package com.example.townhall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class myEventCardAdapter extends RecyclerView.Adapter<myEventCardAdapter.ViewHolder> {

    private List<EventHelper> eventList;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public myEventCardAdapter(List<EventHelper> eventList, OnItemClickListener clickListener) {
        this.eventList = eventList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myevent_cardview, parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventHelper event = eventList.get(position);
        holder.eventImage.setImageResource(event.getImage());
        holder.eventDay.setText(event.getDay());
        holder.eventMonth.setText(event.getMonth());
        holder.eventYear.setText(event.getYear());
        holder.eventTitle.setText(event.getTitle());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventDay, eventMonth, eventTitle, eventYear;

        public ViewHolder(@NonNull View itemView, OnItemClickListener clickListener) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.event_image);
            eventDay = itemView.findViewById(R.id.event_date_day);
            eventMonth = itemView.findViewById(R.id.event_date_month);
            eventYear = itemView.findViewById(R.id.event_date_year);
            eventTitle = itemView.findViewById(R.id.event_title);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClick(position);
                    }
                }
            });
        }
    }
}
