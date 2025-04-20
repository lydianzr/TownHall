package com.example.townhall;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private LinearLayout accountSettingsLayout;
    private LinearLayout notificationSettingsLayout;
    private LinearLayout privacySettingsLayout;
    private Button logoutButton; // Add a reference for the logout button

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize the LinearLayouts
        accountSettingsLayout = view.findViewById(R.id.account_settings);
//        notificationSettingsLayout = view.findViewById(R.id.notification_settings);
        privacySettingsLayout = view.findViewById(R.id.privacy_settings);
        logoutButton = view.findViewById(R.id.logout_button); // Initialize the logout button

        // Set click listeners for each row to navigate to the respective fragment
        accountSettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_settingsFragment_to_accountSettingsFragment);
            }
        });

//        notificationSettingsLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                NavController navController = Navigation.findNavController(v);
//                navController.navigate(R.id.action_settingsFragment_to_notificationSettingsFragment);
//            }
//        });

        privacySettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_settingsFragment_to_privacySettingsFragment);
            }
        });

        // Set click listener for the logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the logout method
                logOut();
            }
        });

        return view;
    }

    // Method to handle user logout
    private void logOut() {
        FirebaseAuth.getInstance().signOut(); // Sign out the user
        Toast.makeText(getContext(), "You have been logged out", Toast.LENGTH_SHORT).show();

        // Redirect the user to the login activity
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
        startActivity(intent);
    }
}
