package com.example.townhall;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EventRegisterFragment extends Fragment {

    private EditText dateOfBirthEdt;
    private String eventDocId;
    private String eventTitle;

    FirebaseAuth auth;
    FirebaseFirestore db;

    public EventRegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the event document ID passed to the fragment
        if (getArguments() != null) {
            eventDocId = getArguments().getString("eventDocId");
        }

        // Log to make sure the eventDocId is being passed
        Log.d("EventRegisterFragment", "Event Doc ID: " + eventDocId);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get references to views
        dateOfBirthEdt = view.findViewById(R.id.date_of_birth);
        Spinner genderSpinner = view.findViewById(R.id.gender_spinner);
        Spinner stateSpinner = view.findViewById(R.id.state_spinner);

        // Set up Gender Spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.gender_options,
                android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Set up State Spinner
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.malaysian_states,
                android.R.layout.simple_spinner_item
        );
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        // Set up Date of Birth EditText with DatePickerDialog
        dateOfBirthEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current date
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create and show DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        R.style.CustomDatePickerTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                dateOfBirthEdt.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                            }
                        },
                        year, month, day
                );
                datePickerDialog.show();
            }
        });

        Button submitButton = view.findViewById(R.id.ButtonSubmitRegister);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Collect form data
                String fullName = ((EditText) view.findViewById(R.id.full_name)).getText().toString().trim();
                String icNumber = ((EditText) view.findViewById(R.id.ic_number)).getText().toString().trim();
                String dob = dateOfBirthEdt.getText().toString().trim();
                String selectedGender = genderSpinner.getSelectedItem().toString();
                String selectedState = stateSpinner.getSelectedItem().toString();
                String email = ((EditText) view.findViewById(R.id.email)).getText().toString().trim();
                String contactNumber = ((EditText) view.findViewById(R.id.contact_number)).getText().toString().trim();
                String emergencyContact = ((EditText) view.findViewById(R.id.emergency_contact_number)).getText().toString().trim();
                boolean isTermsChecked = ((CheckBox) view.findViewById(R.id.checkbox_terms)).isChecked();

                // Validate form fields
                if (fullName.isEmpty() || icNumber.isEmpty() || dob.isEmpty() || selectedGender.equals("Select Gender") ||
                        selectedState.equals("Select State") || email.isEmpty() || contactNumber.isEmpty() ||
                        emergencyContact.isEmpty() || !isTermsChecked) {
                    Toast.makeText(getContext(), "Please fill in all required fields and accept Terms & Conditions.", Toast.LENGTH_SHORT).show();
                } else {
                    // Create a map to store event details
                    Map<String, String> eventDetails = new HashMap<>();
                    eventDetails.put("eventDocId", eventDocId); // Store eventDocId
                    eventDetails.put("fullName", fullName);
                    eventDetails.put("icNumber", icNumber);
                    eventDetails.put("dob", dob);
                    eventDetails.put("state", selectedState);
                    eventDetails.put("contactNumber", contactNumber);
                    eventDetails.put("email", email);
                    eventDetails.put("emergencyContact", emergencyContact);
                    eventDetails.put("gender", selectedGender);
                    eventDetails.put("medicalCond", ""); // Add medical condition field if needed

                    // Reference to Firestore
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (currentUser != null) {
                        String userId = currentUser.getUid();

                        // Reference to the user document
                        DocumentReference userDocRef = firestore.collection("User").document(userId);

                        // Check if the user document exists
                        userDocRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    // Document exists
                                    Log.d("Firestore", "User document exists.");
                                    updateRegisteredEvents(userDocRef, eventDetails, v);
                                } else {
                                    // Document does not exist
                                    Log.d("Firestore", "User document does not exist. Creating a new one...");
                                    Map<String, Object> newUserData = new HashMap<>();
                                    newUserData.put("registeredEvent", new ArrayList<>());
                                    userDocRef.set(newUserData).addOnSuccessListener(aVoid -> {
                                        updateRegisteredEvents(userDocRef, eventDetails, v);
                                    }).addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error creating user document: " + e.getMessage());
                                        Toast.makeText(getContext(), "Error creating user document: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                                }
                            } else {
                                Log.e("Firestore", "Error checking user document: " + task.getException().getMessage());
                                Toast.makeText(getContext(), "Error checking user document: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void updateRegisteredEvents(DocumentReference userDocRef, Map<String, String> eventDetails, View v) {
        // Log event details before updating Firestore
        Log.d("EventDetails", "Event Details: " + eventDetails.toString());

        userDocRef.update("registeredEvent", FieldValue.arrayUnion(eventDetails))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event Registered Successfully!", Toast.LENGTH_SHORT).show();

                    // Navigate to ProfileFragment with the event title
                    Bundle bundle = new Bundle();
                    bundle.putString("event_title", eventTitle);
                    Navigation.findNavController(v).navigate(R.id.navigation_profile, bundle);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to register for the event: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

}
