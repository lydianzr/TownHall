package com.example.townhall;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrivacySettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrivacySettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button saveButton;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String userID;
    FirebaseUser user;

    public PrivacySettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrivacySettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PrivacySettingsFragment newInstance(String param1, String param2) {
        PrivacySettingsFragment fragment = new PrivacySettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userID = auth.getCurrentUser().getUid();
        user = auth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privacy_settings, container, false);

        EditText confirmNewPassword = view.findViewById(R.id.confirmNewPassword);
        EditText newPassword = view.findViewById(R.id.newPassword);
        EditText currentPassword = view.findViewById(R.id.currentPassword);
        Button saveButton = view.findViewById(R.id.saveButton);
        ImageView successTick1 = view.findViewById(R.id.successTick1);
        ImageView successTick2 = view.findViewById(R.id.successTick2);
        ImageView successTick3 = view.findViewById(R.id.successTick3);
        ImageView failTick1 = view.findViewById(R.id.failTick1);

        // Add TextWatcher to handle backspace events
        addTextWatcher(currentPassword, successTick1);
        addTextWatcher(newPassword, successTick2);
        addTextWatcher(confirmNewPassword, successTick3);

        saveButton.setOnClickListener(v -> {
            String current = currentPassword.getText().toString().trim();
            String newP = newPassword.getText().toString().trim();
            String confirm = confirmNewPassword.getText().toString().trim();

            if (current.isEmpty() || newP.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newP.equals(confirm)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the current password is the same as the new password
            if (current.equals(newP)) {
                Toast.makeText(getContext(), "New password cannot be the same as the current password", Toast.LENGTH_SHORT).show();
                return;
            }

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (!isValidPassword(newP)) {
                Toast.makeText(requireContext(), "Password must meet criteria", Toast.LENGTH_LONG).show();
                return;
            }

            // Reauthenticate the user
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), current);
            user.reauthenticate(credential).addOnSuccessListener(unused -> {
                successTick1.setVisibility(View.VISIBLE);
                // Update the password after successful reauthentication
                user.updatePassword(newP).addOnSuccessListener(unused1 -> {
                    Toast.makeText(getContext(), "Password reset successfully", Toast.LENGTH_SHORT).show();
                    successTick2.setVisibility(View.VISIBLE);
                    successTick3.setVisibility(View.VISIBLE);
                    failTick1.setVisibility(View.GONE);
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Password reset failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Reauthentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                failTick1.setVisibility(View.VISIBLE);
            });
        });
        return view;
    }

    /**
     * Add a TextWatcher to handle changes in the text field and hide the success tick when empty.
     */
    private void addTextWatcher(EditText editText, ImageView successTick) {
        editText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    successTick.setVisibility(View.GONE); // Hide the success tick
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // No action needed after text changes
            }
        });
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_])[A-Za-z\\d\\W_]{8,12}$";
        return password.matches(passwordPattern);
    }

}