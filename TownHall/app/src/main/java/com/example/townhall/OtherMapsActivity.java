package com.example.townhall;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class OtherMapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps_other, container, false);

        // Load the map
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
//        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set a normal map type for better detail
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Retrieve the passed location data
        Bundle args = getArguments();
        if (args != null) {
            double latitude = args.getDouble("latitude", 0.0);
            double longitude = args.getDouble("longitude", 0.0);
            String personName = args.getString("personName", "Unknown");

            if (latitude != 0.0 && longitude != 0.0) {
                LatLng personLocation = new LatLng(latitude, longitude);

                // Smooth camera movement and higher zoom level
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(personLocation, 18));

                // Add a marker at the location
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(personLocation)
                        .title(personName + "'s Location");
                mMap.addMarker(markerOptions);

                //////////////////////////////////////////////////////////////////////////
                mMap.setOnMapLoadedCallback(() -> {
                    Toast.makeText(requireContext(), "Map loaded successfully", Toast.LENGTH_SHORT).show();
                });
                //////////////////////////////////////////////////////////////////////////
            } else {
                Toast.makeText(requireContext(), "Invalid location data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "No location data provided", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 99) { // Check the request code
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
