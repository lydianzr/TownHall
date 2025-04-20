package com.example.townhall;

import static android.app.Activity.RESULT_OK;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.io.IOException;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.google.android.gms.maps.model.LatLng;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import android.util.Log;
//import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TicketFragment extends Fragment {
    private Spinner typeSpinner, categorySpinner, receiverSpinner; //spinner
    private ArrayAdapter<String> typeAdapter, categoryAdapter, receiverAdapter; //spinner selection
    private EditText dateEditText, timeEditText, editTextDescription; // date and time
    private Calendar calendar;
    private EditText locationEditText; //location after choose
    private Button locationButton; //to choose the location
    private Button buttonSubmit; //to choose the location
    private ImageView imageUpload;



    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_PERMISSIONS = 100;
    private FirebaseFirestore database;
    private CollectionReference ticketsRef;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout first and then initialize the views
        try{
            View rootView = inflater.inflate(R.layout.fragment_ticket, container, false);

            imageUpload = rootView.findViewById(R.id.imageUpload);
            imageUpload.setOnClickListener(v -> showImageUploadDialog());

            database = FirebaseFirestore.getInstance();
            ticketsRef = database.collection("tickets");


            return rootView;  // Return the rootView after setting up the views
        }catch (Exception e) {
            Log.e("FragmentTicket", "Error in onCreateView", e);
            return null; // Return null in case of an error to avoid app crash
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        // Initialize all views
        // pastikan initialize dulu baru setup
        initializeViews(view);
        // FirebaseApp.initializeApp(getContext());

        locationEditText = view.findViewById(R.id.editTextLocation);
        locationButton = view.findViewById(R.id.buttonLocation);
        editTextDescription = view.findViewById(R.id.editTextDescription);

        setupLocationPicker(); //problem disini
        setupSpinners();
        setupDateTimePickers();
        database = FirebaseFirestore.getInstance();
        ticketsRef = database.collection("tickets");
                if (!FirebaseApp.getApps(getContext()).isEmpty()) {
            database = FirebaseFirestore.getInstance();
            ticketsRef = database.collection("tickets");
        } else {
            Log.e("TicketFragment", "Firebase not initialized");
        }


        ///////////////////////////////////////////////////////////////////////////
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        CollectionReference ticketsRef = database.collection("tickets");
//
//        if (FirebaseApp.getApps(getContext()).isEmpty()) {
//            Log.e("TicketFragment", "Firebase not initialized");
//        } else {
//            // Firestore is initialized and ready to use
//            Log.d("TicketFragment", "Firestore initialized successfully");
//        }
        ///////////////////////////////////////////////////////////////////////////

        // Set up button click listener for submission
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(v -> submitTicket());

    }


    private void initializeViews(View view) {
        typeSpinner = view.findViewById(R.id.spinnerType);
        categorySpinner = view.findViewById(R.id.spinnerCategory);
        receiverSpinner = view.findViewById(R.id.spinnerReceiver);
        dateEditText = view.findViewById(R.id.editTextDate);
        timeEditText = view.findViewById(R.id.editTextTime);
        calendar = Calendar.getInstance();
    }

    //spinner for Type & Receiver
    private void setupSpinners() {
        //Type Spinner
        int spinnerStyle = R.layout.custom_spinner_item; // Create this layout
        String[] types = {"Please select", "Complaint", "Suggestion", "Report", "Feedback", "Inquiry"};
        typeAdapter = new ArrayAdapter<>(getContext(), spinnerStyle, types);
        typeAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        // Receiver Spinner
        String[] receivers = {"Please select", "Management", "Security", "Maintenance", "Community Affairs"};
        receiverAdapter = new ArrayAdapter<>(getContext(), spinnerStyle, receivers);
        receiverAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        receiverSpinner.setAdapter(receiverAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCategorySpinner(types[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // update Category based on Type
    private void updateCategorySpinner(String selectedType) {
        String[] categories;

        switch (selectedType) {
            case "Complaint":
                categories = new String[]{"Please select", "General", "Cleanliness", "Other"};
                break;
            case "Suggestion":
                categories = new String[]{"Please select", "Event Proposal", "Improvement Idea", "Other"};
                break;
            case "Report":
                categories = new String[]{"Please select", "Safety Concern", "Property Damage", "Incident", "Lost Item", "Other"};
                break;
            case "Feedback":
                categories = new String[]{"Please select", "Service Quality", "Other"};
                break;
            case "Inquiry":
                categories = new String[]{"Please select", "General Information", "Facility Booking", "Other"};
                break;
            default:
                categories = new String[]{"Please Select"};
                break;
        }

        categoryAdapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void setupDateTimePickers() {
        dateEditText.setOnClickListener(v -> showDatePicker());
        timeEditText.setOnClickListener(v -> showTimePicker());
    }
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                R.style.DatePickerTheme,
                (view1, year, month, day) -> {
                    String selectedDate = String.format(Locale.getDefault(),
                            "%02d/%02d/%d", day, month + 1, year);
                    dateEditText.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                R.style.TimePickerTheme,
                (view12, hourOfDay, minute) -> {
                    String selectedTime = String.format(Locale.getDefault(),
                            "%02d:%02d", hourOfDay, minute);
                    timeEditText.setText(selectedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }



    // Declare class-level variables for gallery and camera selection
    private Uri cameraImageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private boolean isGallerySelected = false;
    private boolean isCameraSelected = false;
    private ActivityResultLauncher<String[]> permissionsLauncher;  // Correct class-level declaration

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        Log.d("PermissionResult", entry.getKey() + ": " + entry.getValue());
                    }
                    boolean allGranted = true;
                    for (Boolean isGranted : result.values()) {
                        allGranted = allGranted && isGranted;
                    }
                    if (allGranted) {
                        // Permissions granted, proceed with logic
                        if (isGallerySelected) {
                            selectImageFromGallery();
                        } else if (isCameraSelected) {
                            openCamera();
                        }
                    } else {
                        Log.d("PermissionResult", "adoi");
                        // Some permissions were denied
                    }
                });

// Correctly launch the permissions request
        permissionsLauncher.launch(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        });

        // Initialize gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            imageUpload.setImageURI(imageUri);
                            imageUpload.setTag(imageUri); // Set the URI as a tag
                            Log.d("TicketFragment", "Gallery image URI set: " + imageUri);
                        }
                    }
                });
        // Initialize camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && cameraImageUri != null) {
                        imageUpload.setImageURI(cameraImageUri);
                        imageUpload.setTag(cameraImageUri); // Set the URI as a tag
                        Log.d("TicketFragment", "Camera image URI set: " + cameraImageUri);
                    }
                });
    }

    private void showImageUploadDialog() {
        String[] options = {"Select from Gallery", "Open Camera"};
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Upload Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        isGallerySelected = true;
                        Log.d("ImageUpload", "Opening gallery...");
                        if (checkPermissions()) {
                            selectImageFromGallery();
                        }
                    } else {
                        isCameraSelected = true;
                        Log.d("ImageUpload", "Opening camera...");
                        if (checkPermissions()) {
                            openCamera();
                        }
                    }
                })
                .show();
    }

    private boolean checkPermissions() {
        // Only request CAMERA permission for taking pictures
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsLauncher.launch(new String[]{Manifest.permission.CAMERA});
            return false;
        }
        return true;
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryLauncher.launch(intent);  // Use the gallery launcher to open the file picker
    }

    private void openCamera() {
        if (checkPermissions()) {
            // Only proceed to open the camera if permissions are granted
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "camera_image.jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            cameraImageUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraImageUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                cameraLauncher.launch(intent);  // Use the camera launcher
            } else {
                Toast.makeText(getContext(), "Failed to create image file", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If permissions are not granted, show a toast or handle the error
            Toast.makeText(requireContext(), "Permissions are required to use the camera", Toast.LENGTH_SHORT).show();
        }
    }


    private LatLng lastSelectedLocation; // Add this as a class variable

    private void setupLocationPicker() {
        locationButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapPickerActivity.class);
            // Pass the last location if it exists
            if (lastSelectedLocation != null) {
                intent.putExtra("latitude", lastSelectedLocation.latitude);
                intent.putExtra("longitude", lastSelectedLocation.longitude);
            }
            locationPickerLauncher.launch(intent);
        });
    }
    private final ActivityResultLauncher<Intent> locationPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String selectedLocation = data.getStringExtra("location");
                        double latitude = data.getDoubleExtra("latitude", 0);
                        double longitude = data.getDoubleExtra("longitude", 0);
                        String address = getAddressFromCoordinates(latitude, longitude);

                        lastSelectedLocation = new LatLng(latitude, longitude);
                        locationEditText.setText(address);
                    }
                }
            });

    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Combine address lines or extract specific details
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if address cannot be found
    }

    private void submitTicket() {
        Log.d("TicketFragment", "submitTicket() called");
        try {
            // Gather input data
            String type = typeSpinner.getSelectedItem().toString();
            String category = categorySpinner.getSelectedItem().toString();
            String receiver = receiverSpinner.getSelectedItem().toString();
            String description = editTextDescription.getText().toString();
            String date = dateEditText.getText().toString();
            String time = timeEditText.getText().toString();
            String location = locationEditText.getText().toString();

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            // Get the URI from the ImageView and convert it to a string
            Uri imageUri = (Uri) imageUpload.getTag();
            String imageUriString = imageUri != null ? imageUri.toString() : "";
            String userId = currentUser.getUid();
            Log.d("TicketFragment", "Image URI retrieved: " + imageUriString);

            if (type.equals("Please Select") ||
            category.equals("Please Select") ||
            receiver.equals("Please Select")) {
                Toast.makeText(getContext(), "Please select all dropdown options", Toast.LENGTH_SHORT).show();
                return;
            }

            if (description.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri == null) {
                Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            Ticket ticket = new Ticket(type, category, receiver, description, date, time, location, imageUriString, userId);
            Log.d("TicketFragment", "Ticket object created");

            // Create a formatted log message with all ticket information
            String ticketInfo = String.format(
                    "Ticket Information:\n" +
                            "Type: %s\n" +
                            "Category: %s\n" +
                            "Receiver: %s\n" +
                            "Description: %s\n" +
                            "Date: %s\n" +
                            "Time: %s\n" +
                            "Location: %s\n" +
                            "Image URI: %s",
                    type, category, receiver, description, date, time, location, imageUriString
            );
            // Log the complete ticket information
            Log.d("TicketFragment", ticketInfo);
            Log.d("TicketFragment", "Ticket object created");
            Log.d("TicketFragment", "Attempting to get Firebase instance");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.d("TicketFragment", "Firebase instance obtained");
            Log.d("TicketFragment", "Getting database reference for 'tickets'");
            Log.d("TicketFragment", "Database reference obtained");
            Log.d("TicketFragment", "Attempting to push data to Firebase");

            Log.d("TicketFragment", "Attempting to add document to Firestore");
            db.collection("tickets").add(ticket)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("TicketFragment", "Ticket added with ID: " + documentReference.getId());
                        Toast.makeText(getContext(), "Ticket Submitted", Toast.LENGTH_SHORT).show();
                        resetForm();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TicketFragment", "Error adding ticket", e);
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            Log.e("TicketFragment", "Exception in submitTicket()", e);
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void resetForm() {
        editTextDescription.setText("");
        dateEditText.setText("");
        timeEditText.setText("");
        locationEditText.setText("");

        typeSpinner.setSelection(0);
        categorySpinner.setSelection(0);
        receiverSpinner.setSelection(0);

        imageUpload.setImageResource(R.drawable.ic_placeholder); // Reset image if needed
        imageUpload.setTag(null); // Clear the tag

    }



}
