package com.example.townhall;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.Locale; // Import the Locale class

public class MapPickerActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Get passed location or use default
        LatLng location;
        Intent intent = getIntent();
        if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
            double lat = intent.getDoubleExtra("latitude", 0);
            double lng = intent.getDoubleExtra("longitude", 0);
            location = new LatLng(lat, lng);
            // Add marker at last location
            googleMap.addMarker(new MarkerOptions().position(location).title("Last Selected Location"));
        } else {
            location = new LatLng(3.152, 101.703); // Default location
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16)); //zoom

        googleMap.setOnMapClickListener(latLng -> {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            returnLocation(latLng);
        });
    }

    private void returnLocation(LatLng latLng) {
        String location = String.format(Locale.getDefault(), "Lat: %.6f, Lng: %.6f",
                latLng.latitude, latLng.longitude);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("location", location);
        resultIntent.putExtra("latitude", latLng.latitude);
        resultIntent.putExtra("longitude", latLng.longitude);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
