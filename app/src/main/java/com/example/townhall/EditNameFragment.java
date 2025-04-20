package com.example.townhall;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditNameFragment extends Fragment {

    private EditText editTextName;
    private Button doneButton;
    private FirebaseFirestore firestore;
    private String currentUserId;

    public EditNameFragment() {
        // Required empty public constructor
    }

    public static EditNameFragment newInstance(String param1, String param2) {
        EditNameFragment fragment = new EditNameFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current user's ID
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_name, container, false);

        editTextName = view.findViewById(R.id.editName);
        doneButton = view.findViewById(R.id.doneButton);
        ImageView successTick = view.findViewById(R.id.successTick);

        // Add TextWatcher to dynamically handle the visibility of the successTick
        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    successTick.setVisibility(View.GONE); // Hide the success tick when the field is cleared
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text changes
            }
        });

        doneButton.setOnClickListener(v -> {
            String newName = editTextName.getText().toString().trim();
            if (!newName.isEmpty()) {
                firestore.collection("User").document(currentUserId)
                        .update("name", newName)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Name updated successfully", Toast.LENGTH_SHORT).show();
                            successTick.setVisibility(View.VISIBLE); // Show the green tick
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to update name", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
