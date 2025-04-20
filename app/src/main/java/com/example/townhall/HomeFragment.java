//package com.example.townhall;
//
//import android.app.AlertDialog;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.SearchView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewpager.widget.ViewPager;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import java.util.ArrayList;
//import java.util.Timer;
//import java.util.TimerTask;
//public class HomeFragment extends Fragment {
//
//    ViewPager viewPager;
//    RecyclerView featutedRecyclerView, recentlyAddedRecyclerView;
//    RecyclerView.Adapter eventAdapter;
//    ArrayList<EventHelper> featured_eventList = new ArrayList<>();
//    ArrayList<EventHelper> recentlyadded_eventList = new ArrayList<>();
//    ArrayList<EventHelper> eventList = new ArrayList<>();
//    int images[] = new int[]{R.drawable.pic_bannerview}; // Use a static image
//    int currentPageCounter = 0;
//
//    FirebaseFirestore db;
//
//    FirebaseUser currentUser;
//    FirebaseAuth auth;
//
//    public HomeFragment() {
//        // Required empty public constructor
//    }
//
//    public static HomeFragment newInstance(String param1, String param2) {
//        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        args.putString("param1", param1);
//        args.putString("param2", param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            // Handle arguments if necessary
//        }
//        // Initialize Firestore
//        db = FirebaseFirestore.getInstance();
//
//        auth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//    }
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
////        auth.signInWithEmailAndPassword("farah@gmail.com", "#Rossid01")
////                .addOnCompleteListener(task -> {
////                    if (task.isSuccessful()) {
////                        // User is signed in, you can fetch the current user now
////                        FirebaseUser currentUser = auth.getCurrentUser();
////                        if (currentUser != null) {
////                            Toast.makeText(requireContext(), "Signed in as: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
////                        }
////                    } else {
////                        Toast.makeText(requireContext(), "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
////                    }
////                });
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//
//        TextView welcomeUserText = view.findViewById(R.id.WelcUserText);
//        // Get the current user's ID from Firebase Authentication
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String userId = currentUser.getUid();
//            // Fetch the user's name from Firestore
//            FirebaseFirestore.getInstance()
//                    .collection("User") // Replace with your Firestore collection for users
//                    .document(userId)
//                    .get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            String username = documentSnapshot.getString("name"); // Replace "username" with your field name
//                            if (username != null) {
//                                welcomeUserText.setText("Welcome, " + username);
//                            } else {
//                                // If no username exists, display the UID
//                                welcomeUserText.setText("Welcome, " + userId);
//                            }
//                        } else {
//                            // If the user document doesn't exist, display the UID
//                            welcomeUserText.setText("Welcome, " + userId);
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        Log.e("Firestore", "Error fetching user name", e);
//                        // On failure, also display the UID
//                        welcomeUserText.setText("Welcome, " + userId);
//                    });
//        } else {
//            // If no user is logged in, default message
//            welcomeUserText.setText("Welcome!");
//        }
//
//
//
//        // Initialize ViewPager and set adapter
//        viewPager = view.findViewById(R.id.viewpager);
//        viewPager.setAdapter(new SliderAdapter(images, requireContext()));
//
//        // Set up auto-scroll for the ViewPager
//        final Handler handler = new Handler();
//        final Runnable update = new Runnable() {
//            @Override
//            public void run() {
//                if (currentPageCounter == images.length) {
//                    currentPageCounter = 0;
//                }
//                viewPager.setCurrentItem(currentPageCounter++, true);
//            }
//        };
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(update);
//            }
//        }, 1000, 1000);
//
//        // RecyclerView for featured events
//        featutedRecyclerView = view.findViewById(R.id.event_recycler_view1);
//        featutedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
//
//        // RecyclerView for recently added events
//        recentlyAddedRecyclerView = view.findViewById(R.id.event_recycler_view2);
//        recentlyAddedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
//
//        // Fetch events from Firestore
//        fetchEventsFromFirestore(view);
//
//        // "See More" buttons logic
//        TextView seeMoreFeatured = view.findViewById(R.id.see_more_featured_events);
//        seeMoreFeatured.setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//            bundle.putParcelableArrayList("allEvents", featured_eventList);  // Pass only featured events
//            Navigation.findNavController(view).navigate(R.id.NextToAllEvents, bundle);
//        });
//
//        TextView seeMoreRecentlyAdded = view.findViewById(R.id.see_more_recently_added_events);
//        seeMoreRecentlyAdded.setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//            bundle.putParcelableArrayList("allEvents", recentlyadded_eventList);  // Pass only recently added events
//            Navigation.findNavController(view).navigate(R.id.NextToAllEvents, bundle);
//        });
//
//        // Button actions
//        Button BtnTickets = view.findViewById(R.id.ButtonTickets);
//        BtnTickets.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.TicketFragment));
//        Button BtnCall = view.findViewById(R.id.ButtonCall);
//        BtnCall.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.emergencyCallFragment));
//
//        Button BtnLocation = view.findViewById(R.id.ButtonLocation);
//        BtnLocation.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.locationFragment));
//
//        ImageButton categoryFilterEventButton = view.findViewById(R.id.CategoryFilterEventButton);
//        categoryFilterEventButton.setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//            bundle.putParcelableArrayList("allEvents", eventList);  // Pass the entire eventList
//            Navigation.findNavController(v).navigate(R.id.navigation_all_events, bundle);
//        });
//
//        // SearchView for event search
//        SearchView searchView = view.findViewById(R.id.searchView);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // Navigate to SearchEventFragment and pass the query
//                Bundle bundle = new Bundle();
//                bundle.putString("searchQuery", query);
//                Navigation.findNavController(view).navigate(R.id.navigation_search_event, bundle);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false; // Keep this false; the filtering will happen in SearchEventFragment
//            }
//        });
//
//
//        // State filter handling
//        TextView StateViewText = view.findViewById(R.id.StateViewText);
//
//        StateViewText.setOnClickListener(v -> {
//            String[] states = {"Johor", "Kedah", "Kelantan", "Melaka", "Negeri Sembilan", "Pahang", "Pulau Pinang", "Perak", "Perlis", "Sabah", "Sarawak", "Selangor", "Terengganu", "Kuala Lumpur", "Labuan", "Putrajaya"};
//
//            // Create AlertDialog for state selection
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            builder.setTitle("Select a State")
//                    .setItems(states, (dialog, which) -> {
//                        String selectedState = states[which];
//                        StateViewText.setText(selectedState);
//
//                        // Filter events based on the selected state
//                        filterEventsByState(selectedState);
//
//                        // Update RecyclerViews after filtering
//                        updateRecyclerViews();
//                    })
//                    .show();
//        });
//    }
//
//    private void fetchEventsFromFirestore(View view) {
//        db.collection("event")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        eventList.clear();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String day = document.getString("day");
//                            String month = document.getString("month");
//                            String year = document.getString("year");
//                            String title = document.getString("eventTitle");
//                            String location = document.getString("location");
//                            String state = document.getString("state");
//                            String category = document.getString("category");
//                            String type = document.getString("type");
//                            String dateAdded = document.getString("dateAdded");
//                            String docId = document.getId(); // Get document ID
//
//                            eventList.add(new EventHelper(R.drawable.pic_bannerview, day, month, year, title, location, state, category, type, dateAdded, docId));
//                        }
//                        filterEventsByState("Kuala Lumpur"); // Default state filter
//                        updateRecyclerViews();
//                    } else {
//                        Log.e("Firestore", "Error getting documents: ", task.getException());
//                    }
//                });
//    }
//
//
//    private void filterEventsByState(String state) {
//        featured_eventList.clear();
//        recentlyadded_eventList.clear();
//
//        for (EventHelper event : eventList) {
//            if (event.getState().equalsIgnoreCase(state)) {
//                if ("featured".equals(event.getType())) {
//                    featured_eventList.add(event);
//                }
//                long currentTime = System.currentTimeMillis();
//                long eventDate = event.getDateAdded();  // Assuming event.getDateAdded() returns the timestamp in milliseconds
//                if ((currentTime - eventDate) <= 30L * 24 * 60 * 60 * 1000) { // 30 days in milliseconds
//                    recentlyadded_eventList.add(event);
//                }
//            }
//        }
//    }
//
//    private void updateRecyclerViews() {
//        featutedRecyclerView.setAdapter(new EventCardAdapter(featured_eventList, position -> {
//            EventHelper clickedEvent = featured_eventList.get(position);
//            Bundle bundle = new Bundle();
//            bundle.putString("eventDocId", clickedEvent.getDocId()); // Pass the document ID
//            Navigation.findNavController(requireView()).navigate(R.id.navigation_event, bundle);
//        }));
//
//        recentlyAddedRecyclerView.setAdapter(new EventCardAdapter(recentlyadded_eventList, position -> {
//            EventHelper clickedEvent = recentlyadded_eventList.get(position);
//            Bundle bundle = new Bundle();
//            bundle.putString("eventDocId", clickedEvent.getDocId()); // Pass the document ID
//            Navigation.findNavController(requireView()).navigate(R.id.navigation_event, bundle);
//        }));
//    }
//}

package com.example.townhall;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
public class HomeFragment extends Fragment {

    ViewPager viewPager;
    RecyclerView featutedRecyclerView, recentlyAddedRecyclerView;
    RecyclerView.Adapter eventAdapter;
    ArrayList<EventHelper> featured_eventList = new ArrayList<>();
    ArrayList<EventHelper> recentlyadded_eventList = new ArrayList<>();
    ArrayList<EventHelper> eventList = new ArrayList<>();
    int images[] = new int[]{R.drawable.pic_bannerview}; // Use a static image
    int currentPageCounter = 0;

    FirebaseFirestore db;

    FirebaseUser currentUser;
    FirebaseAuth auth;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle arguments if necessary
        }
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        auth.signInWithEmailAndPassword("farah@gmail.com", "#Rossid01")
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // User is signed in, you can fetch the current user now
//                        FirebaseUser currentUser = auth.getCurrentUser();
//                        if (currentUser != null) {
//                            Toast.makeText(requireContext(), "Signed in as: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(requireContext(), "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView welcomeUserText = view.findViewById(R.id.WelcUserText);
        // Get the current user's ID from Firebase Authentication
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Fetch the user's name from Firestore
            FirebaseFirestore.getInstance()
                    .collection("User") // Replace with your Firestore collection for users
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username"); // Replace "username" with your field name
                            if (username != null) {
                                welcomeUserText.setText("Welcome, " + username);
                            } else {
                                // If no username exists, display the UID
                                welcomeUserText.setText("Welcome, " + userId);
                            }
                        } else {
                            // If the user document doesn't exist, display the UID
                            welcomeUserText.setText("Welcome, " + userId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error fetching user name", e);
                        // On failure, also display the UID
                        welcomeUserText.setText("Welcome, " + userId);
                    });
        } else {
            // If no user is logged in, default message
            welcomeUserText.setText("Welcome!");
        }

        // Initialize ViewPager
        viewPager = view.findViewById(R.id.viewpager);
        int images[] = new int[]{R.drawable.pic_mad, R.drawable.pic_ecoworkshops};
        viewPager.setAdapter(new SliderAdapter(images, requireContext()));

        // Auto-scroll logic
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (images.length > 0) {
                    if (currentPageCounter == images.length) {
                        currentPageCounter = 0;
                    }
                    viewPager.setCurrentItem(currentPageCounter++, true);
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 3000, 5000); // Start delay: 3 seconds, Interval: 5 seconds


        // RecyclerView for featured events
        featutedRecyclerView = view.findViewById(R.id.event_recycler_view1);
        featutedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        // RecyclerView for recently added events
        recentlyAddedRecyclerView = view.findViewById(R.id.event_recycler_view2);
        recentlyAddedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        // Fetch events from Firestore
        fetchEventsFromFirestore(view);

        // "See More" buttons logic
        TextView seeMoreFeatured = view.findViewById(R.id.see_more_featured_events);
        seeMoreFeatured.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("allEvents", featured_eventList);  // Pass only featured events
            Navigation.findNavController(view).navigate(R.id.NextToAllEvents, bundle);
        });
        TextView seeMoreRecentlyAdded = view.findViewById(R.id.see_more_recently_added_events);
        seeMoreRecentlyAdded.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("allEvents", recentlyadded_eventList);  // Pass only recently added events
            Navigation.findNavController(view).navigate(R.id.NextToAllEvents, bundle);
        });


        // Button actions
        Button BtnTickets = view.findViewById(R.id.ButtonTickets);
        BtnTickets.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.TicketFragment));
        Button BtnCall = view.findViewById(R.id.ButtonCall);
        BtnCall.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.emergencyCallFragment));

        Button BtnLocation = view.findViewById(R.id.ButtonLocation);
        BtnLocation.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.locationFragment));

        ImageButton categoryFilterEventButton = view.findViewById(R.id.CategoryFilterEventButton);
        categoryFilterEventButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("allEvents", eventList);  // Pass the entire eventList
            Navigation.findNavController(v).navigate(R.id.navigation_all_events, bundle);
        });

        // SearchView for event search
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Navigate to SearchEventFragment and pass the query
                Bundle bundle = new Bundle();
                bundle.putString("searchQuery", query);
                Navigation.findNavController(view).navigate(R.id.navigation_search_event, bundle);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false; // Keep this false; the filtering will happen in SearchEventFragment
            }
        });


        // State filter handling
        TextView StateViewText = view.findViewById(R.id.StateViewText);

        StateViewText.setOnClickListener(v -> {
            String[] states = {"Johor", "Kedah", "Kelantan", "Melaka", "Negeri Sembilan", "Pahang", "Pulau Pinang", "Perak", "Perlis", "Sabah", "Sarawak", "Selangor", "Terengganu", "Kuala Lumpur", "Labuan", "Putrajaya"};

            // Create AlertDialog for state selection
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Select a State")
                    .setItems(states, (dialog, which) -> {
                        String selectedState = states[which];
                        StateViewText.setText(selectedState);

                        // Filter events based on the selected state
                        filterEventsByState(selectedState);

                        // Update RecyclerViews after filtering
                        updateRecyclerViews();
                    })
                    .show();
        });
    }
    private void fetchEventsFromFirestore(View view) {
        db.collection("event")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String day = document.getString("day");
                            String month = document.getString("month");
                            String year = document.getString("year");
                            String title = document.getString("eventTitle");
                            String location = document.getString("location");
                            String state = document.getString("state");
                            String category = document.getString("category");
                            String type = document.getString("type");
                            String dateAdded = document.getString("dateAdded");
                            String docId = document.getId(); // Get document ID

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
                        filterEventsByState("Kuala Lumpur"); // Default state filter
                        updateRecyclerViews();
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }




    private void filterEventsByState(String state) {
        featured_eventList.clear();
        recentlyadded_eventList.clear();

        // Get the current year and month
        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy", Locale.getDefault());
        String currentMonthYear = sdf.format(System.currentTimeMillis());

        for (EventHelper event : eventList) {
            String eventState = event.getState();
            if (eventState != null && eventState.equalsIgnoreCase(state)) {
                if ("featured".equals(event.getType())) {
                    featured_eventList.add(event);
                }
                // Check if the event was added in the current month
                String eventDateAdded = event.getDateAdded();
                if (eventDateAdded != null) {
                    try {
                        // Extract the month and year from the event's dateAdded
                        SimpleDateFormat eventDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Date eventDate = eventDateFormat.parse(eventDateAdded);

                        if (eventDate != null) {
                            String eventMonthYear = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(eventDate);
                            if (eventMonthYear.equals(currentMonthYear)) {
                                recentlyadded_eventList.add(event);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("DateParsing", "Invalid date format for event: " + event.getTitle(), e);
                    }
                }
            }
        }
    }

    private void updateRecyclerViews() {
        // Limit the number of events to display for horizontal RecyclerView
        ArrayList<EventHelper> limitedFeaturedEvents = new ArrayList<>(featured_eventList.subList(0, Math.min(5, featured_eventList.size())));
        ArrayList<EventHelper> limitedRecentlyAddedEvents = new ArrayList<>(recentlyadded_eventList.subList(0, Math.min(5, recentlyadded_eventList.size())));

        featutedRecyclerView.setAdapter(new EventCardAdapter(limitedFeaturedEvents, position -> {
            EventHelper clickedEvent = limitedFeaturedEvents.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("eventDocId", clickedEvent.getDocId()); // Pass the document ID
            Navigation.findNavController(requireView()).navigate(R.id.navigation_event, bundle);
        }));

        recentlyAddedRecyclerView.setAdapter(new EventCardAdapter(limitedRecentlyAddedEvents, position -> {
            EventHelper clickedEvent = limitedRecentlyAddedEvents.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("eventDocId", clickedEvent.getDocId()); // Pass the document ID
            Navigation.findNavController(requireView()).navigate(R.id.navigation_event, bundle);
        }));
    }

}