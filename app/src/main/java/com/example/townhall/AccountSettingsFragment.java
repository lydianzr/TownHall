package com.example.townhall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class AccountSettingsFragment extends Fragment {

    private TextView nameTextView, usernameTextView, emailTextView;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_settings, container, false);

        // Initialize UI elements
        nameTextView = view.findViewById(R.id.name);
        usernameTextView = view.findViewById(R.id.username);
        emailTextView = view.findViewById(R.id.email);

        ImageButton nameButton = view.findViewById(R.id.editNameButton);
        ImageButton usernameButton = view.findViewById(R.id.editUsernameButton);
        ImageButton emailButton = view.findViewById(R.id.editEmailButton);

        loadUserInfo();

        nameButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_accountSettingsFragment_to_editNameFragment);
        });

        usernameButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_accountSettingsFragment_to_editUsernameFragment);
        });

        emailButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_accountSettingsFragment_to_editEmailFragment);
        });

        return view;
    }

    private void loadUserInfo() {
        // Get the current logged-in user's email
        String currentUserEmail = auth.getCurrentUser().getEmail();
        String currentUsername = auth.getCurrentUser().getUid();

        if (currentUserEmail != null) {
            // Reference to the user's Firestore document
            DocumentReference userDocRef = db.collection("User").document(currentUsername);

            // Fetch the document
            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Update TextViews with data
                    String name = documentSnapshot.getString("name");
                    String username = documentSnapshot.getString("username"); // Assuming the document ID is the username
//                    String email = documentSnapshot.getString("email");

                    nameTextView.setText(name);
                    usernameTextView.setText("@" + username);
                    emailTextView.setText(currentUserEmail);
                }
            }).addOnFailureListener(e -> {
                // Handle errors (e.g., log or show a message)
                e.printStackTrace();
            });
        }
    }
}
