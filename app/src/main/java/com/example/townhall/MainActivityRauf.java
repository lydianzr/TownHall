package com.example.townhall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.townhall.GovernmentInitiatives;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivityRauf extends AppCompatActivity implements RecyclerViewInterface {

    ArrayList<GovernmentInitiatives> governmentiniatives = new ArrayList<>();
    ArrayList<GovernmentInitiatives> filteredList = new ArrayList<>();
    AA_recycleviewadapter adapter;
    RecyclerView recyclerView;
    TabLayout tabLayout;
    SearchView searchView;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.govinia);

        recyclerView = findViewById(R.id.recyclerview);
        tabLayout = findViewById(R.id.tabLayout);
        searchView = findViewById(R.id.search_view);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        db = FirebaseFirestore.getInstance();
        searchEditText.setTextColor(Color.WHITE);

        // Set hint text color to grey
        searchEditText.setHintTextColor(Color.DKGRAY);

        fetchGovernmentInitiatives();

        filteredList.addAll(governmentiniatives);

        adapter = new AA_recycleviewadapter((Context) this, filteredList, (RecyclerViewInterface) this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterData(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        setupSearchViewListener();
    }

    private void fetchGovernmentInitiatives() {
        db.collection("Gov_iniative")  // Use the correct collection name
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Clear the previous data
                        governmentiniatives.clear();

                        // Loop through the documents in the snapshot
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            // Log entire document data for debugging
                            Log.d("MainActivity", "Document data: " + document.getData());

                            // Fetch fields from the Firestore document
                            String category = document.getString("category");
                            String description = document.getString("description");
                            String startDate = document.getString("startDate");
                            String endDate = document.getString("endDate");
                            String name = document.getString("name");
                            String status = document.getString("status");
                            Boolean isOngoing = document.getBoolean("isOngoing");

                            // Set default values if any field is missing
                            if (description == null) {
                                Log.e("MainActivity", "Description field is null or missing.");
                                description = "Description not available."; // Set a default if description is missing
                            }
                            if (startDate == null || endDate == null) {
                                Log.e("MainActivity", "Start or End Date field is null.");
                                startDate = "Start Date not available";
                                endDate = "End Date not available";
                            }

                            // Fetch the image name from Firestore
                            String imageName = document.getString("image");  // Firestore stores the image name, e.g., "baucarbuku"

// Get the image resource ID from drawable folder using the image name
                            int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());

// Check if the image resource exists
                            if (imageResId == 0) {
                                Log.e("MainActivity", "Image not found: " + imageName);
                                imageResId = R.drawable.placeholder; // Use a default image if not found
                            }

                            // Create a new Governmentiniatives object
                            GovernmentInitiatives initiative = new GovernmentInitiatives(
                                    category,
                                    description,
                                    endDate,
                                    document.getId(),  // Using document ID as initiativeID
                                    name,
                                    startDate,
                                    status,
                                    imageResId, // Default image for now
                                    isOngoing != null && isOngoing // Default to false if null
                            );

                            // Add initiative to the list
                            governmentiniatives.add(initiative);
                        }

                        // Update the filtered list and notify adapter
                        filteredList.clear();
                        filteredList.addAll(governmentiniatives);
                        adapter.notifyDataSetChanged();

                        // Log the size of the fetched data
                        Log.d("MainActivity", "Fetched " + governmentiniatives.size() + " initiatives");
                    } else {
                        Log.e("MainActivity", "No initiatives found in Firestore.");
                        Toast.makeText(MainActivityRauf.this, "No initiatives found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("MainActivity", "Error fetching documents: " + e.getMessage()));
    }


    @Override
    public void onItemClick(int position) {
        if (filteredList != null && !filteredList.isEmpty() && position >= 0 && position < filteredList.size()) {
            Intent intent = new Intent(MainActivityRauf.this, MainActivity2.class);

            // Pass data to the next activity
            intent.putExtra("iniativeID", filteredList.get(position).getInitiativeID());
            intent.putExtra("Nama", filteredList.get(position).getName());
            intent.putExtra("description", filteredList.get(position).getDescription());
            intent.putExtra("Status", filteredList.get(position).getStatus());
            intent.putExtra("category", filteredList.get(position).getCategory());
            intent.putExtra("StartDate", filteredList.get(position).getStartDate());
            intent.putExtra("EndDate", filteredList.get(position).getEndDate());
            intent.putExtra("Image", filteredList.get(position).getImageResId());

            // Start the new activity
            startActivity(intent);
        } else {
            // Handle the error (e.g., show a Toast, log the error)
            Toast.makeText(this, "Error: Invalid item position", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterData(String tabName) {
        filteredList.clear(); // Clear the current list

        if (tabName.equals("For You")) {
            filteredList.addAll(governmentiniatives); // Show all items
        } else if (tabName.equals("Incoming")) {
            for (GovernmentInitiatives initiative : governmentiniatives) {
                if (initiative.isOngoing()) {
                    filteredList.add(initiative);
                }
            }
        } else if (tabName.equals("Completed")) {
            for (GovernmentInitiatives initiative : governmentiniatives) {
                if (!initiative.isOngoing()) {
                    filteredList.add(initiative);
                }
            }
        }

        adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    // Set up search view listener
    private void setupSearchViewListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Optional: Handle search submit action if needed
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInitiatives(newText);  // Call searchInitiatives when the query changes
                return true;
            }
        });
    }

    private void searchInitiatives(String query) {
        filteredList.clear(); // Clear the current list

        // If the query is empty, show all items (or you can change this to show none or a message)
        if (query.isEmpty()) {
            filterData(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString()); // Use the current tab to filter the list
        } else {
            for (GovernmentInitiatives initiative : governmentiniatives) {
                // Check if the initiative matches the active tab and the search query
                if (tabLayout.getSelectedTabPosition() == 0 || // For "For You"
                        (tabLayout.getSelectedTabPosition() == 1 && initiative.isOngoing()) || // For "Incoming"
                        (tabLayout.getSelectedTabPosition() == 2 && !initiative.isOngoing())) { // For "Completed"

                    if (initiative.getName().toLowerCase().contains(query.toLowerCase()) ||
                            initiative.getDescription().toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(initiative);
                    }
                }
            }
        }

        adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
    }
}