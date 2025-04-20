package com.example.townhall;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class AllEventsFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    ArrayList<EventHelper> allEventList;
    ArrayList<EventHelper> filteredEventList;
    FirebaseFirestore db;

    public AllEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        // Initialize the event lists
        allEventList = new ArrayList<>();
        filteredEventList = new ArrayList<>();

//        // Fetch all events from Firestore
//        fetchAllEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.all_event_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        // Retrieve passed events list
        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<EventHelper> allEvents = bundle.getParcelableArrayList("allEvents");
            if (allEvents != null) {
                // If all events are passed, use them directly
                allEventList.addAll(allEvents);
                filteredEventList.addAll(allEventList);
                setupRecyclerView(filteredEventList);
            }
        }

        // Spinner for category filter
        Spinner categorySpinner = view.findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.volunteer_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = parentView.getItemAtPosition(position).toString();
                filterEventsByCategory(allEventList, selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No action required
            }
        });


    }
    // Fetch all events from Firestore
//    private void fetchAllEvents() {
//        db.collection("event")
//                .orderBy("dateAdded", Query.Direction.DESCENDING) // Order by dateAdded or another field if required
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (DocumentSnapshot document : task.getResult()) {
//                            // Retrieve event data from Firestore
//                            String day = document.getString("day");
//                            String month = document.getString("month");
//                            String year = document.getString("year");
//                            String title = document.getString("eventTitle");
//                            String location = document.getString("location");
//                            String state = document.getString("state");
//                            String category = document.getString("category");
//                            String type = document.getString("type");
//                            String dateAddedString = document.getString("dateAdded");
//                            String docId = document.getId();
//
//                            // Create an EventHelper object
//                            EventHelper event = new EventHelper(
//                                    R.drawable.pic_bannerview, // Set default image, change if dynamic image required
//                                    day, month, year, title, location, state, category, type, dateAddedString, docId
//                            );
//
//                            // Add to the list
//                            allEventList.add(event);
//                        }
//
//                        // After fetching all events, apply category filter
//                        filteredEventList.addAll(allEventList);
//                        setupRecyclerView(filteredEventList);
//                    } else {
//                        Log.e("AllEventsFragment", "Error getting documents: ", task.getException());
//                    }
//                });
//    }

    // Filter the events based on the selected category
    private void filterEventsByCategory(ArrayList<EventHelper> events, String category) {
        ArrayList<EventHelper> filteredEvents = new ArrayList<>();
        for (EventHelper event : events) {
            if (event.getCategory().equals(category) || category.equals("All")) {
                filteredEvents.add(event);
            }
        }
        // Update the RecyclerView with the filtered list
        setupRecyclerView(filteredEvents);
    }

    // Set up the RecyclerView with the filtered list
    private void setupRecyclerView(ArrayList<EventHelper> eventList) {
        // Check if event list is empty and show a Toast message
        if (eventList != null && !eventList.isEmpty()) {
            adapter = new EventCardAdapter(eventList, position -> {
                EventHelper clickedEvent = eventList.get(position);

                // Navigate to event details fragment with the clicked event data
                Bundle bundle = new Bundle();
                bundle.putString("eventDocId", clickedEvent.getDocId()); // Pass the document ID
                Navigation.findNavController(requireView()).navigate(R.id.navigation_event, bundle);
            });

            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);

//        }  else {
//            // Show a message or handle empty events list if needed
//            recyclerView.setVisibility(View.GONE);
//            Toast.makeText(requireContext(), "No events available.", Toast.LENGTH_SHORT).show();
        }

    }
}