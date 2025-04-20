package com.example.townhall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter class for displaying post notifications in a RecyclerView.
 * This adapter is responsible for binding the data (PostNoti) to the views.
 */
public class PostNotiAdapter extends RecyclerView.Adapter<PostNotiAdapter.ViewHolder> {

    // List to hold the notifications
    private List<PostNoti> postNotiList;
    private static final String TAG = "PostNotiAdapter"; // Tag for Logcat debugging

    /**
     * Constructor to initialize the adapter with a list of notifications.
     *
     * @param postNotiList A list of PostNoti objects to be displayed in the RecyclerView.
     */
    public PostNotiAdapter(List<PostNoti> postNotiList) {
        this.postNotiList = postNotiList;
        Log.d(TAG, "Adapter initialized with " + postNotiList.size() + " notifications.");
    }

    /**
     * Called when a new ViewHolder is created. This method inflates the layout for each item in the list.
     *
     * @param parent   The parent view group (RecyclerView) where the new item will be placed.
     * @param viewType The type of view to create (not used in this case).
     * @return A new ViewHolder for the item view.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Log creation of a new view holder
        Log.d(TAG, "onCreateViewHolder called.");

        // Inflate the item layout (XML) and create the item view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_noti, parent, false);

        // Return a new ViewHolder instance with the inflated view
        return new ViewHolder(itemView);
    }

    /**
     * Called to bind data to the view elements for each item.
     * This is where the PostNoti object is used to populate the UI.
     *
     * @param holder   The ViewHolder that holds references to the views for each item.
     * @param position The position of the item in the list.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position: " + position);

        // Get the PostNoti object at the current position
        PostNoti postNoti = postNotiList.get(position);
        if (postNoti != null) {
            // Set the notification message text
            holder.messageTextView.setText(postNoti.getMessage());

            // Log the notification message for debugging
            Log.d(TAG, "Notification message: " + postNoti.getMessage());

            // Set the timestamp text
            holder.timestampTextView.setText(String.valueOf(postNoti.getTimestamp()));

            // Log the timestamp to verify the correct data is being set
            Log.d(TAG, "Notification timestamp: " + postNoti.getTimestamp());
        } else {
            // Log an error if the PostNoti object is null
            Log.e(TAG, "PostNoti object is null at position " + position);
        }
    }

    /**
     * Returns the total number of items (notifications) in the list.
     *
     * @return The number of items in the notification list.
     */
    @Override
    public int getItemCount() {
        int itemCount = postNotiList.size();
        Log.d(TAG, "getItemCount called. Total items: " + itemCount);
        return itemCount;
    }

    /**
     * ViewHolder class that holds the references to the views for each item in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // References to the TextViews in the item layout
        public TextView messageTextView;
        public TextView timestampTextView;

        /**
         * Constructor to initialize the views for each item.
         *
         * @param itemView The view representing a single item in the list.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            // Log the creation of a new ViewHolder instance
            Log.d(TAG, "ViewHolder created for item view: " + itemView);

            // Initialize the views based on their IDs from the layout
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);

            // Log the initialization of the views
            Log.d(TAG, "Views initialized: messageTextView, timestampTextView");
        }
    }
}