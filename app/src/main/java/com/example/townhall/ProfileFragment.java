package com.example.townhall;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private myEventCardAdapter adapter;
    private MaterialCalendarView calendarView;
    private ArrayList<EventHelper> eventList;
    private ArrayList<EventHelper> filteredEventList;
    private FirebaseFirestore db;
    private TextView userUsername;
    private ImageView profile_image;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ImageButton settings = view.findViewById(R.id.settingsButton);
        settings.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_profileFragment_to_settingsFragment);
        });

        db = FirebaseFirestore.getInstance();

        TextView user_name = view.findViewById(R.id.user_name);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        FirebaseFirestore.getInstance()
                .collection("User")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("name");
                        if (username != null) {
                            user_name.setText(username);
                        } else {
                            user_name.setText(userId);
                        }
                    } else {
                        user_name.setText(userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching user name", e);
                    user_name.setText(userId);
                });

        profile_image = view.findViewById(R.id.profile_image);
        userUsername = view.findViewById(R.id.user_username);
        fetchAndSetUsername();

        recyclerView = view.findViewById(R.id.myeventcardlayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventList = new ArrayList<>();
        filteredEventList = new ArrayList<>();

        adapter = new myEventCardAdapter(filteredEventList, position -> {
            EventHelper clickedEvent = filteredEventList.get(position);
            Toast.makeText(getContext(), "Clicked: " + clickedEvent.getTitle(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);

        fetchRegisteredEvents();

        calendarView = view.findViewById(R.id.calendarView);

        CalendarDay today = CalendarDay.today();
        calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.equals(today);
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new android.text.style.ForegroundColorSpan(Color.parseColor("#008080")));
            }
        });

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            filterEventsByDate(date);
        });

        TextView myEventsText = view.findViewById(R.id.myevent);
        myEventsText.setOnClickListener(v -> {
            filteredEventList.clear();
            filteredEventList.addAll(eventList);
            adapter.notifyDataSetChanged();
        });

        return view;
    }

    private void fetchRegisteredEvents() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("User").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("registeredEvent")) {
                ArrayList<Map<String, Object>> registeredEvents =
                        (ArrayList<Map<String, Object>>) documentSnapshot.get("registeredEvent");

                if (registeredEvents != null && !registeredEvents.isEmpty()) {
                    for (Map<String, Object> eventMap : registeredEvents) {
                        String eventDocId = (String) eventMap.get("eventDocId");
                        Log.d("ProfileFragment", "Fetching event details for eventDocId: " + eventDocId);
                        fetchEventDetails(eventDocId);
                    }
                } else {
                    Toast.makeText(getContext(), "No registered events found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "No registered events found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("ProfileFragment", "Error fetching user document: " + e.getMessage());
        });
    }

    private void fetchEventDetails(String eventDocId) {
        if (eventDocId == null || eventDocId.isEmpty()) {
            Log.e("ProfileFragment", "Invalid eventDocId: " + eventDocId);
            return;
        }

        db.collection("event").document(eventDocId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("ProfileFragment", "Event details found for eventDocId: " + eventDocId);

                String title = documentSnapshot.getString("eventTitle");
                String day = documentSnapshot.getString("day");
                String month = documentSnapshot.getString("month");
                String year = documentSnapshot.getString("year");
                String location = documentSnapshot.getString("location");
                String state = documentSnapshot.getString("state");
                String category = documentSnapshot.getString("category");
                String type = documentSnapshot.getString("type");
                String dateAdded = documentSnapshot.getString("dateAdded");

                int pic = R.drawable.pic_bannerview;
                if ("Community Support Day".equals(title)) {
                    pic = R.drawable.pic_communitysupport;
                } else if ("Health Screening Camp".equals(title)) {
                    pic = R.drawable.pic_healthscreening;
                } else if ("Music Fest".equals(title)) {
                    pic = R.drawable.pic_musicfest;
                } else if ("Art Expo".equals(title)) {
                    pic = R.drawable.pic_artexpo;
                } else if ("Pet Adoption Drive".equals(title)) {
                    pic = R.drawable.pic_petadoption;
                } else if ("Eco-Workshops for Youth".equals(title)) {
                    pic = R.drawable.pic_ecoworkshops;
                } else if ("MAD".equals(title)) {
                    pic = R.drawable.pic_mad;
                } else if ("River Cleanup".equals(title)) {
                    pic = R.drawable.pic_rivercleanup;
                } else if ("Volunteer Health Camp".equals(title)) {
                    pic = R.drawable.pic_volhealthcamp;
                } else if ("River Rescue Day 2024".equals(title)) {
                    pic = R.drawable.pic_riverrescueday;
                } else if ("VolRun".equals(title)) {
                    pic = R.drawable.pic_volrun;
                }

                EventHelper event = new EventHelper(
                        pic, day, month, year, title,
                        location, state, category, type, dateAdded, eventDocId
                );

                eventList.add(event);
                filteredEventList.add(event);
                adapter.notifyDataSetChanged();

                int eventDay = Integer.parseInt(day);
                int eventMonth = getMonthIndex(month);
                int eventYear = Integer.parseInt(year);
                calendarView.addDecorator(new EventDecorator(requireContext(), Color.parseColor("#008080"),
                        new HashSet<>(java.util.Collections.singleton(CalendarDay.from(eventYear, eventMonth, eventDay)))));
            } else {
                Log.e("ProfileFragment", "Event document not found for eventDocId: " + eventDocId);
            }
        }).addOnFailureListener(e -> {
            Log.e("ProfileFragment", "Error fetching event details for eventDocId: " + eventDocId + ". Error: " + e.getMessage());
        });
    }

    private void filterEventsByDate(CalendarDay selectedDate) {
        filteredEventList.clear();
        for (EventHelper event : eventList) {
            int eventDay = Integer.parseInt(event.getDay());
            int eventMonth = getMonthIndex(event.getMonth());
            int eventYear = Integer.parseInt(event.getYear());

            if (eventDay == selectedDate.getDay() && eventMonth == selectedDate.getMonth() && eventYear == selectedDate.getYear()) {
                filteredEventList.add(event);
            }
        }

        if (filteredEventList.isEmpty()) {
            Toast.makeText(getContext(), "No events on this date", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }

    private int getMonthIndex(String monthName) {
        switch (monthName) {
            case "Jan": return 0;
            case "Feb": return 1;
            case "Mar": return 2;
            case "Apr": return 3;
            case "May": return 4;
            case "Jun": return 5;
            case "Jul": return 6;
            case "Aug": return 7;
            case "Sep": return 8;
            case "Oct": return 9;
            case "Nov": return 10;
            case "Dec": return 11;
            default: return -1;
        }
    }

    private void fetchAndSetUsername() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("ProfileFragment", "Fetching data for userId: " + userId);

        db.collection("User").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("ProfileFragment", "Document found for userId: " + userId);

                String username = documentSnapshot.getString("username");
                String profileImageUri = documentSnapshot.getString("ImageUri");

                if (username != null && !username.isEmpty()) {
                    userUsername.setText("@" + username);
                    Log.d("ProfileFragment", "Username found: " + username);
                } else {
                    Log.w("ProfileFragment", "Username is null or empty, falling back to userId.");
                    userUsername.setText("@" + userId);
                }

                if (profileImageUri != null && !profileImageUri.isEmpty()) {
                    Log.d("ProfileFragment", "Profile image URI found: " + profileImageUri);
                    Uri imageUri = Uri.parse(profileImageUri);

                    try {
                        requireContext().getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Log.d("ProfileFragment", "Intent.");

                        Picasso.get()
                                .load(imageUri)
                                .placeholder(R.drawable.icprofileplaceholder)
                                .error(R.drawable.icprofileplaceholder)
                                .into(profile_image, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("ProfileFragment", "Image loaded successfully.");
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.e("ProfileFragment", "Error loading image: " + e.getMessage());
                                        profile_image.setImageResource(R.drawable.icprofileplaceholder);
                                    }
                                });
                    } catch (SecurityException e) {
                        Log.e("ProfileFragment", "Error requesting permissions: " + e.getMessage());
                        profile_image.setImageResource(R.drawable.icprofileplaceholder);
                    }
                } else {
                    Log.w("ProfileFragment", "Profile image URI is null or empty, setting default image.");
                    profile_image.setImageResource(R.drawable.icprofileplaceholder);
                }
            } else {
                Log.e("ProfileFragment", "No document found for userId: " + userId);
                userUsername.setText("@" + userId);
            }
        }).addOnFailureListener(e -> {
            Log.e("ProfileFragment", "Error fetching user data: " + e.getMessage());
            userUsername.setText("@" + userId);
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }
}
