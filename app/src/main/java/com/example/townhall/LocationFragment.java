package com.example.townhall;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationFragment extends Fragment {

    private ListView peopleListView;
    private ArrayList<HashMap<String, Object>> people;
    private SimpleAdapter adapter;
    private Button shareLocButton;

    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    private String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        // Initialize UI elements
        peopleListView = view.findViewById(R.id.peopleListView);
        shareLocButton = view.findViewById(R.id.shareLocButton);

        // Initialize the list and adapter
        people = new ArrayList<>();
        adapter = new SimpleAdapter(
                requireContext(),
                people,
                com.example.modulehajar.R.layout.people_item_view,  // Layout for each item
                new String[]{"name", "image"},  // Map keys to bind
                new int[]{com.example.modulehajar.R.id.personName, com.example.modulehajar.R.id.profileImage}  // View IDs to bind to
        );
        peopleListView.setAdapter(adapter);

        // Fetch data and populate list
        fetchOtherPeopleLocations();

        // Handle item clicks to navigate to another fragment
        peopleListView.setOnItemClickListener((AdapterView<?> parent, View view1, int position, long id) -> {
            HashMap<String, Object> selectedPerson = people.get(position);
            String name = (String) selectedPerson.get("name");
            double latitude = (double) selectedPerson.get("latitude");
            double longitude = (double) selectedPerson.get("longitude");

            ////////////////////////////////////////////////////////////////////////////////////////
            Toast.makeText(requireContext(), "latitude: " + latitude, Toast.LENGTH_SHORT).show();
            Toast.makeText(requireContext(), "longitude: " + longitude, Toast.LENGTH_SHORT).show();
            ///////////////////////////////////////////////////////////////////////////////////////
            Bundle bundle = new Bundle();
            bundle.putString("personName", name);
            bundle.putDouble("latitude", latitude);
            bundle.putDouble("longitude", longitude);

            Navigation.findNavController(view1).navigate(R.id.action_locationFragment_to_otherMapFragment, bundle);
        });

        // Share location button navigation
        shareLocButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_locationFragment_to_myMapFragment)
        );

        return view;
    }

    private void fetchOtherPeopleLocations() {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            userID = currentUser.getUid();

            // Fetch the current user's document
            db.collection("User").document(userID).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && documentSnapshot.contains("otherPeopleLocation")) {
                            List<HashMap<String, Object>> otherPeopleLocations =
                                    (List<HashMap<String, Object>>) documentSnapshot.get("otherPeopleLocation");

                            if (otherPeopleLocations != null) {
                                for (HashMap<String, Object> locationMap : otherPeopleLocations) {
                                    GeoPoint location = (GeoPoint) locationMap.get("location");
                                    String otherUserId = (String) locationMap.get("userID");

                                    if (location != null && otherUserId != null) {
                                        // Fetch the name of the user using the userID
                                        db.collection("User").document(otherUserId).get()
                                                .addOnSuccessListener(otherUserDoc -> {
                                                    if (otherUserDoc.exists() && otherUserDoc.contains("name")) {
                                                        String name = otherUserDoc.getString("name");

                                                        // Add the user data to the people list
                                                        HashMap<String, Object> person = new HashMap<>();
                                                        person.put("name", name != null ? name : otherUserId); // Fallback to userID if name is null
                                                        person.put("latitude", location.getLatitude());
                                                        person.put("longitude", location.getLongitude());
                                                        person.put("image", R.drawable.ic_profile); // Static image for now
                                                        people.add(person);

                                                        // Notify the adapter after adding the person
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("LocationFragment", "Error fetching user details for " + otherUserId + ": " + e.getMessage());
                                                });
                                    }
                                }
                            } else {
                                Toast.makeText(requireContext(), "Failed to load locations.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("LocationFragment", "No otherPeopleLocation field found.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("LocationFragment", "Error fetching user document: " + e.getMessage());
                        Toast.makeText(requireContext(), "Failed to load locations.", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}
