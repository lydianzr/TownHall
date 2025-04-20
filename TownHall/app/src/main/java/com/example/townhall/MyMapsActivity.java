package com.example.townhall;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyMapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 99;
    ////////////////////////////////////////////////////
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    String userId;
    ///////////////////////////////////////////////////

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps_my, container, false);

/////////////////////////////////////////////////////////////////////////////////////////////////////
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
////////////////////////////////////////////////////////////////////////////////////////////////
//        auth.signInWithEmailAndPassword("abu@gmail.com", "abubakar")
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // User is signed in, you can fetch the current user now
//                        FirebaseUser currentUser = auth.getCurrentUser();
//                        if (currentUser != null) {
//                            Toast.makeText(requireContext(), "Signed in as: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
//                            getLastLocation(); // Fetch location only after the user is authenticated
//                        }
//                    } else {
//                        Toast.makeText(requireContext(), "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//        // KENA BUANG BILA DAH COMPILE !!!!!!!!!!!!!!!

/////////////////////////////////////////////////////////////////////////////////////////////////
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        getLastLocation();

        return view;
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getContext(), currentLocation.getLatitude()
                            + "," + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment =
                            (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.FragMap);
                    if (supportMapFragment != null) {
                        supportMapFragment.getMapAsync(MyMapsActivity.this);
                    }
                    //////////////////////////////////////////////////////////////////
                    saveLocationToFirestore(location);
                    ////////////////////////////////////////////////////////////////////
                }
            }
        });
    }

    private void saveLocationToFirestore(Location location) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        // Fetch emergency contacts of the current user
        db.collection("User").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("emergencyContact")) {
                        List<HashMap<String, Object>> emergencyContacts =
                                (List<HashMap<String, Object>>) documentSnapshot.get("emergencyContact");

                        // Filter emergency contacts to find matching app users
                        findAppUsersFromContacts(emergencyContacts, location);
                    } else {
                        Toast.makeText(requireContext(), "No emergency contacts found for this user.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error fetching emergency contacts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void findAppUsersFromContacts(List<HashMap<String, Object>> emergencyContacts, Location location) {
        List<String> contactPhoneNumbers = new ArrayList<>();
        for (HashMap<String, Object> contact : emergencyContacts) {
            contactPhoneNumbers.add((String) contact.get("phoneNumber"));
        }

        // Query Firestore to find users with matching phone numbers
        db.collection("User")
                .whereIn("phoneNumber", contactPhoneNumbers)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> matchingUserNames = new ArrayList<>();
                    List<String> matchingUserIds = new ArrayList<>();

                    for (var document : queryDocumentSnapshots.getDocuments()) {
                        String name = document.getString("name");
                        String id = document.getId();
                        matchingUserNames.add(name);
                        matchingUserIds.add(id);
                    }

                    // Show dialog to select users
                    showContactsDialog(matchingUserNames, matchingUserIds, location);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error fetching app users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showContactsDialog(List<String> userNames, List<String> userIds, Location location) {
        String[] userNameArray = userNames.toArray(new String[0]);
        boolean[] checkedItems = new boolean[userNames.size()];

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Contacts")
                .setMultiChoiceItems(userNameArray, checkedItems, (dialog, which, isChecked) -> {
                    checkedItems[which] = isChecked;
                })
                .setPositiveButton("Share", (dialog, which) -> {
                    // Collect selected user IDs and names
                    List<String> selectedUserIds = new ArrayList<>();
                    List<String> selectedUserNames = new ArrayList<>();
                    for (int i = 0; i < checkedItems.length; i++) {
                        if (checkedItems[i]) {
                            selectedUserIds.add(userIds.get(i));
                            selectedUserNames.add(userNames.get(i));
                        }
                    }

                    // Share location with selected users
                    shareLocationWithSelectedFollowers(selectedUserIds, selectedUserNames, location);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void shareLocationWithSelectedFollowers(List<String> selectedUserIds, List<String> selectedUserNames, Location location) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        HashMap<String, Object> sharedLocation = new HashMap<>();
        sharedLocation.put("userId", userId);
        sharedLocation.put("location", new com.google.firebase.firestore.GeoPoint(location.getLatitude(), location.getLongitude()));

        for (int i = 0; i < selectedUserIds.size(); i++) {
            String followerId = selectedUserIds.get(i);
            String followerName = selectedUserNames.get(i);

            db.collection("User").document(followerId)
                    .update("otherPeopleLocation", FieldValue.arrayUnion(sharedLocation))
                    .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Location shared with: " + followerName, Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error sharing location with " + followerName + ": " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker and move the camera
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                .title("My Current Location");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        mMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}
