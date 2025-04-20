package com.example.townhall;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.FirebaseFirestore;

public class EventFragment extends Fragment {

    private String eventDocId;
    private String eventTitle, eventDate, eventLocation, eventDesc;
    private int eventImage; // For now, using a placeholder image
    private FirebaseFirestore db;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve the event document ID passed to the fragment
        if (getArguments() != null) {
            eventDocId = getArguments().getString("eventDocId");
        }

        // Fetch event details from Firestore using the document ID
        if (eventDocId != null) {
            fetchEventDetails(eventDocId);
        } else {
            Log.e("EventFragment", "Event document ID is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Register button click listener
        Button BtnRegister = view.findViewById(R.id.ButtonRegister);
        BtnRegister.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("eventDocId", eventDocId); // Pass the document ID to the registration fragment
            Navigation.findNavController(view).navigate(R.id.navigation_event_register, bundle);
        });
    }

    // Fetch event details from Firestore based on the document ID
    private void fetchEventDetails(String docId) {
        db.collection("event").document(docId).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                // Log entire document data for debugging
                Log.d("EventFragment", "Document data: " + document.getData());

                // Fetch fields from the Firestore document
                eventTitle = document.getString("eventTitle");
                eventDesc = document.getString("description");

                if (eventDesc == null) {
                    Log.e("EventFragment", "Description field is null or missing.");
                    eventDesc = "Description not available."; // Set default if description is missing
                }

                String day = document.getString("day");
                String month = document.getString("month");
                String year = document.getString("year");
                eventDate = day + " " + month + " " + year;
                eventLocation = document.getString("location") + ", " + document.getString("state");

                // Set a default image for now
                // Set a default image for now
                eventImage = R.drawable.pic_bannerview;
                if ("Community Support Day".equals(eventTitle)) {
                    eventImage= R.drawable.pic_communitysupport;
                } else if ("Health Screening Camp".equals(eventTitle)) {
                    eventImage= R.drawable.pic_healthscreening;
                } else if ("Music Fest".equals(eventTitle)) {
                    eventImage= R.drawable.pic_musicfest;
                } else if ("Art Expo".equals(eventTitle)) {
                    eventImage= R.drawable.pic_artexpo;
                } else if ("Pet Adoption Drive".equals(eventTitle)) {
                    eventImage= R.drawable.pic_petadoption;
                } else if ("Eco-Workshops for Youth".equals(eventTitle)) {
                    eventImage= R.drawable.pic_ecoworkshops;
                } else if ("MAD".equals(eventTitle)) {
                    eventImage= R.drawable.pic_mad;
                } else if ("River Cleanup".equals(eventTitle)) {
                    eventImage= R.drawable.pic_rivercleanup;
                } else if ("Volunteer Health Camp".equals(eventTitle)) {
                    eventImage= R.drawable.pic_volhealthcamp;
                } else if ("River Rescue Day 2024".equals(eventTitle)) {
                    eventImage= R.drawable.pic_riverrescueday;
                }  else if ("VolRun".equals(eventTitle)) {
                    eventImage= R.drawable.pic_volrun;
                }

                // Update UI after fetching details
                View view = getView();
                if (view != null) {
                    updateUI(view);
                }
            } else {
                Log.e("EventFragment", "Document not found for ID: " + docId);
            }
        }).addOnFailureListener(e -> Log.e("EventFragment", "Error fetching document: " + e.getMessage()));
    }


    // Update UI with event details
    private void updateUI(View view) {
        TextView titleTextView = view.findViewById(R.id.event_title);
        TextView descTextView = view.findViewById(R.id.textdescription);
        TextView dateTextView = view.findViewById(R.id.event_date);
        TextView locationTextView = view.findViewById(R.id.event_location);
        ImageView imageView = view.findViewById(R.id.eventdetails_image);

        titleTextView.setText(eventTitle);
        descTextView.setText(eventDesc);
        dateTextView.setText(eventDate);
        locationTextView.setText(eventLocation);
        imageView.setImageResource(eventImage); // Display the image from drawable
    }
}
