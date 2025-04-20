package com.example.townhall;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;


public class NotificationsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        // Set click listener for Complaints Updates
//        View complaintsUpdatesContainer = view.findViewById(R.id.complaints_updates_container);
//        complaintsUpdatesContainer.setOnClickListener(v -> {
//            Navigation.findNavController(v).navigate(R.id.action_NotificationsFragment_to_submittedTicketFragment);
//        });
//
//        View forumActivityContainer = view.findViewById(R.id.forum_activity_container);
//        forumActivityContainer.setOnClickListener(v -> {
//            Navigation.findNavController(v).navigate(R.id.action_NotificationsFragment_to_PostNotiFragment);
//        });
    }
}