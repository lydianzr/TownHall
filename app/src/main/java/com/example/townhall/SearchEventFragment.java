package com.example.townhall;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class SearchEventFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventCardAdapter adapter;
    private ArrayList<EventHelper> eventList = new ArrayList<>(); // All events
    private ArrayList<EventHelper> filteredEventList = new ArrayList<>(); // Filtered events
    private FirebaseFirestore db;

    private SearchView searchView;

    private String searchQuery;

    public SearchEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve the search query passed from HomeFragment
        if (getArguments() != null) {
            searchQuery = getArguments().getString("searchQuery", "");
            Log.d("SearchEventFragment", "Search query: " + searchQuery);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.all_event_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchView = view.findViewById(R.id.searchView);

        // Fetch all events initially
        fetchAllEvents();

        // Handle search input
        setupSearchView();
    }

    private void fetchAllEvents() {
        db.collection("event")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("eventTitle");
                            String day = document.getString("day");
                            String month = document.getString("month");
                            String year = document.getString("year");
                            String location = document.getString("location");
                            String state = document.getString("state");
                            String category = document.getString("category");
                            String type = document.getString("type");
                            String dateAdded = document.getString("dateAdded");
                            String docId = document.getId();

                            int pic = R.drawable.pic_bannerview;
                            if ("Community Support Day".equals(title)) {
                                pic= R.drawable.pic_communitysupport;
                            } else if ("Health Screening Camp".equals(title)) {
                                pic= R.drawable.pic_healthscreening;
                            } else if ("Music Fest".equals(title)) {
                                pic= R.drawable.pic_musicfest;
                            } else if ("Art Expo".equals(title)) {
                                pic= R.drawable.pic_artexpo;
                            } else if ("Pet Adoption Drive".equals(title)) {
                                pic= R.drawable.pic_petadoption;
                            } else if ("Eco-Workshops for Youth".equals(title)) {
                                pic= R.drawable.pic_ecoworkshops;
                            } else if ("MAD".equals(title)) {
                                pic= R.drawable.pic_mad;
                            } else if ("River Cleanup".equals(title)) {
                                pic= R.drawable.pic_rivercleanup;
                            } else if ("Volunteer Health Camp".equals(title)) {
                                pic= R.drawable.pic_volhealthcamp;
                            } else if ("River Rescue Day 2024".equals(title)) {
                                pic= R.drawable.pic_riverrescueday;
                            }  else if ("VolRun".equals(title)) {
                                pic= R.drawable.pic_volrun;
                            }

                            eventList.add(new EventHelper(pic, day, month, year, title, location, state, category, type, dateAdded, docId));
                        }

                        // Show filtered events if searchQuery is not empty, otherwise show all
                        if (searchQuery != null && !searchQuery.isEmpty()) {
                            filterEvents(searchQuery);
                        } else {
                            filteredEventList.clear();
                            filteredEventList.addAll(eventList);
                            setupRecyclerView(filteredEventList);
                        }
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                        Toast.makeText(requireContext(), "Failed to fetch events. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterEvents(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // If search query is empty, show all events
                    filteredEventList.clear();
                    filteredEventList.addAll(eventList);
                    setupRecyclerView(filteredEventList);
                } else {
                    // Filter events based on query
                    filterEvents(newText);
                }
                return true;
            }
        });
    }

    private void filterEvents(String query) {
        filteredEventList.clear();
        for (EventHelper event : eventList) {
            if (event.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredEventList.add(event);
            }
        }
        setupRecyclerView(filteredEventList);
    }

    private void setupRecyclerView(ArrayList<EventHelper> eventList) {
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
        } else {
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "No events found.", Toast.LENGTH_SHORT).show();
        }
    }
}
