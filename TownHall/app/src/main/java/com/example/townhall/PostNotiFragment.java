package com.example.townhall;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * This fragment displays post notifications. It listens for changes in the Firestore database
 * to update the list of notifications for the posts that the user interacts with.
 */
public class PostNotiFragment extends Fragment {

    private static final String TAG = "PostNotiFragment"; // Tag for Logcat
    private RecyclerView recyclerView;
    private PostNotiAdapter postNotiAdapter;
    private List<PostNoti> postNotiList;

    /**
     * This method is called when the fragment's view is created. It sets up the RecyclerView
     * and starts listening for changes in the Firestore database.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called.");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post_noti, container, false);

        // Initialize RecyclerView and the list to hold the notifications
        recyclerView = rootView.findViewById(R.id.recyclerViewPostNoti);
        postNotiList = new ArrayList<>();
        postNotiAdapter = new PostNotiAdapter(postNotiList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postNotiAdapter);

        // Log the RecyclerView setup
        Log.d(TAG, "RecyclerView set up.");

        // Start listening for changes in Firestore
        listenForPostNotifications();

        return rootView;
    }

    /**
     * This method listens for changes in the Firestore collection and updates the RecyclerView.
     * It listens for new notifications, updates, and deletions of notifications related to posts.
     */
    private void listenForPostNotifications() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query to listen for changes in the post notifications
        db.collection("postNotifications")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen failed.", e);
                        return;
                    }

                    if (querySnapshot != null) {
                        for (DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            PostNoti postNoti = dc.getDocument().toObject(PostNoti.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New notification added: " + postNoti.getMessage());
                                    postNotiList.add(postNoti);
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Notification modified: " + postNoti.getMessage());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Notification removed: " + postNoti.getMessage());
                                    postNotiList.remove(postNoti);
                                    break;
                            }
                        }
                        // Notify the adapter that the data has changed
                        postNotiAdapter.notifyDataSetChanged();
                    }
                });

        Log.d(TAG, "Started listening for post notifications.");
    }
}