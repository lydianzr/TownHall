package com.example.townhall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditUsernameFragment extends Fragment {

    private String currentUsername; // Variable to store the current username

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_username, container, false);

        // Get references to UI elements
        EditText editUsername = view.findViewById(R.id.editUsername);
        Button doneButton = view.findViewById(R.id.doneButton4);
        ImageView successTick = view.findViewById(R.id.successTick);
        ImageView failTick = view.findViewById(R.id.failTick);

        // Firestore and Authentication references
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid(); // Get current user's UID

        // Retrieve current username from Firestore
        db.collection("User").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentUsername = documentSnapshot.getString("username");
                        editUsername.setText(currentUsername); // Set the current username in the input field
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to retrieve username", Toast.LENGTH_SHORT).show());

        // Add TextWatcher to handle backspace events and dynamic tick visibility
        addTextWatcher(editUsername, successTick, failTick);

        doneButton.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();

            if (newUsername.isEmpty()) {
                Toast.makeText(getContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newUsername.equals(currentUsername)) {
                Toast.makeText(getContext(), "Please enter a different username", Toast.LENGTH_SHORT).show();
                failTick.setVisibility(View.VISIBLE);
                return;
            }

            // Query Firestore to check if the username already exists
            db.collection("User")
                    .whereEqualTo("username", newUsername)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Username already exists
                            Toast.makeText(getContext(), "Username already taken, please choose another", Toast.LENGTH_SHORT).show();
                            failTick.setVisibility(View.VISIBLE);
                        } else {
                            // Username does not exist, proceed to update
                            db.collection("User").document(userId)
                                    .update("username", newUsername)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                                        successTick.setVisibility(View.VISIBLE); // Show green tick
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Failed to update username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to check username availability: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        return view;
    }

    /**
     * Add a TextWatcher to handle changes in the text field and dynamically update tick visibility.
     */
    private void addTextWatcher(EditText editText, ImageView successTick, ImageView failTick) {
        editText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    successTick.setVisibility(View.GONE); // Hide success tick if field is empty
                    failTick.setVisibility(View.GONE); // Hide fail tick if field is empty
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // No action needed after text changes
            }
        });
    }
}
