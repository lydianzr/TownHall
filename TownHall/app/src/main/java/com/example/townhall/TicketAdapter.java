package com.example.townhall;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
//import com.squareup.picasso.Picasso;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private List<Ticket> ticketList;

    public TicketAdapter(List<Ticket> tickets) {
        this.ticketList = tickets;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket_item, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        holder.category.setText("Category: " + ticket.getCategory());
        holder.date.setText("Date: " + ticket.getDate());
        holder.description.setText("Description: " + ticket.getDescription());
        holder.location.setText(ticket.getLocation());
        holder.receiver.setText("Receiver: " + ticket.getReceiver());
        holder.time.setText("Time: " + ticket.getTime());
        holder.id.setText("Ticket ID: " + ticket.getId());
        holder.status.setText("Status: " + ticket.getStatus());


//        String uriString = ticket.getImageUri(); // Get the URI string from the model
//        Log.d("TicketAdapter", "Image URI: " + uriString);
//
//        if (uriString != null && !uriString.isEmpty()) {
//            try {
//                Uri imageUri = Uri.parse(uriString); // Convert to Uri
//                Log.d("TicketAdapter", "Parsed Image URI: " + imageUri.toString());
//
//                Picasso.get()
//                        .load(imageUri)
//                        .placeholder(R.drawable.ic_placeholder) // Placeholder image
//                        .into(holder.ticketImageView);
//
//                Log.d("TicketAdapter", "Image successfully loaded for ticket: " + ticket.getId());
//            } catch (Exception e) {
//                Log.e("TicketAdapter", "Error loading image for ticket: " + ticket.getId(), e);
//            }
//        } else {
//            Log.e("TicketAdapter", "Invalid or empty URI for ticket: " + ticket.getId());
//        }
    }
    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView category, date, description, location, receiver, time, id, status;
        //ImageView ticketImageView; // Corrected type to ImageView

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.ticketCategory);
            date = itemView.findViewById(R.id.ticketDate);
            description = itemView.findViewById(R.id.ticketDescription);
            location = itemView.findViewById(R.id.ticketLocation);
            receiver = itemView.findViewById(R.id.ticketReceiver);
            time = itemView.findViewById(R.id.ticketTime);
            id = itemView.findViewById(R.id.ticketIdTextView);
            status = itemView.findViewById(R.id.statusTextView);
            //ticketImageView = itemView.findViewById(R.id.ticketImageView); // Initialize correctly
        }
    }
}