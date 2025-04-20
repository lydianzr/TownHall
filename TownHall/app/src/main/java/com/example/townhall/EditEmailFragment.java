package com.example.townhall;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class EditEmailFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_email, container, false);

        // UI references
        EditText editEmail = view.findViewById(R.id.editEmail);
        EditText password = view.findViewById(R.id.password);
        Button doneButton = view.findViewById(R.id.doneButton3);
        ImageView successTick = view.findViewById(R.id.successTick);
        ImageView failTick1 = view.findViewById(R.id.failTick1);
        ImageView failTick2 = view.findViewById(R.id.failTick2);

        // Firebase references
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        doneButton.setOnClickListener(v -> {
            String newEmail = editEmail.getText().toString().trim();
            String currentPassword = password.getText().toString().trim();

            if (newEmail.isEmpty()) {
                Toast.makeText(getContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = auth.getCurrentUser();

//      TENGOK AFTER DAH COMPILE DGN RAUF  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//            db.collection("User")
//                    .whereEqualTo("email", newEmail)
//                    .get()
//                    .addOnSuccessListener(queryDocumentSnapshots -> {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            // Email already exists
//                            Toast.makeText(getContext(), "Email already exists. Please enter another email.", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Email does not exist, proceed with update
//                            updateEmail(newEmail);
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(getContext(), "Failed to check email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });


            if (user != null) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                user.reauthenticate(credential).addOnSuccessListener(unused -> {
                    failTick2.setVisibility(View.GONE);
                    successTick.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Reauthentication successful!", Toast.LENGTH_SHORT).show();

                    user.verifyBeforeUpdateEmail(newEmail)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("UpdateEmailError", "Failed to send verification email: " + task.getException().getMessage());
                                    Toast.makeText(getContext(), "Failed to send verification email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }).addOnFailureListener(e -> {
                    successTick.setVisibility(View.GONE);
                    failTick2.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Reauthentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(getContext(), "No authenticated user found", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}
