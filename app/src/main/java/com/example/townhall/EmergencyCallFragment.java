package com.example.townhall;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.modulehajar.CustomAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmergencyCallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmergencyCallFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EmergencyCallFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmergencyCallFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmergencyCallFragment newInstance(String param1, String param2) {
        EmergencyCallFragment fragment = new EmergencyCallFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private List<String> emergencyContacts= new ArrayList<>();
    private List<String> hotlines= new ArrayList<>();
    CustomAdapter ECAdapter;
    CustomAdapter HAdapter;
/////////////////////////////////////////////////////
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    String userID;
////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ///////////////////////////////////////////////
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
//        fAuth.createUserWithEmailAndPassword("abu@gmail.com", "abubakar");
        ///////////////////////////////////////////////
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency_call, container, false);

        // Initialize dataset and adapter
        emergencyContacts = new ArrayList<>();
        hotlines = new ArrayList<>();
        ECAdapter = new CustomAdapter(requireContext(), emergencyContacts);
        HAdapter = new CustomAdapter(requireContext(), hotlines);
        ListView ECLV = view.findViewById(R.id.emergencyContactListView);
        ListView HLV = view.findViewById(R.id.hotlineListView);
        ECLV.setAdapter(ECAdapter);
        HLV.setAdapter(HAdapter);

        // Handle item click to change category
//        ECLV.setOnItemClickListener((parent, view1, position, id) -> showCategoryDialog(position, true));
//        HLV.setOnItemClickListener((parent, view1, position, id) -> showCategoryDialog(position, false));

        loadContacts();
        // Handle "More" button
        ImageButton moreButton = view.findViewById(R.id.moreButton);
        moreButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Manage Contacts")
                    .setItems(new String[]{"Add Contact", "Delete Contact"}, (dialog, which) -> {
                        if (which == 0) {
                            // Add Contact
                            checkPermissionAndPickContact();
                        } else if (which == 1) {
                            // Delete Contact
                            showDeleteContactDialog();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


        return view;
    }

    private static final int PICK_CONTACT_REQUEST = 1;

    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri contactUri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};

            Cursor cursor = requireActivity().getContentResolver().query(contactUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                cursor.close();

                // Show category dialog
                showCategorySelectionDialog(name, number);
            }
        }
    }

    private void showCategorySelectionDialog(String name, String number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Category")
                .setItems(new String[]{"Emergency Contact", "Hotline"}, (dialog, which) -> {
                    if (which == 0) {
                        // Add to Emergency Contacts
                        emergencyContacts.add(name + " - " + number);
                        ECAdapter.notifyDataSetChanged();
                        //////////////////////////////////////////////////////////////////////////////////////////////////////
                        userID = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("User").document(userID);

// Create a map for the contact details
                        Map<String, String> contactDetails = new HashMap<>();
                        contactDetails.put("name", name);
                        contactDetails.put("phoneNumber", number);
                        contactDetails.put("category", "Emergency Contact");

// Use Firestore transaction or update to ensure field is created
                        documentReference.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists() && documentSnapshot.contains("emergencyContact")) {
                                // If the field exists, update it by adding the contact
                                documentReference.update("emergencyContact", FieldValue.arrayUnion(contactDetails))
                                        .addOnSuccessListener(unused ->
                                                Toast.makeText(requireContext(), "Contact added to Emergency Contacts", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e ->
                                                Toast.makeText(requireContext(), "Failed to add contact: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            } else {
                                // If the field doesn't exist, initialize it with the new contact
                                Map<String, Object> data = new HashMap<>();
                                List<Map<String, String>> newEmergencyContactList = new ArrayList<>();
                                newEmergencyContactList.add(contactDetails);
                                data.put("emergencyContact", newEmergencyContactList);

                                documentReference.set(data, SetOptions.merge())
                                        .addOnSuccessListener(unused ->
                                                Toast.makeText(requireContext(), "Emergency Contact field created and contact added.", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e ->
                                                Toast.makeText(requireContext(), "Failed to create field: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        });

                        /////////////////////////////////////////////////////////////////////////////////////////////////////
//                        Toast.makeText(requireContext(), "Contact added to Emergency Contacts", Toast.LENGTH_SHORT).show();
                    } else {
                        // Add to Hotlines
                        hotlines.add(name + " - " + number);
                        HAdapter.notifyDataSetChanged();
                        //////////////////////////////////////////////////////////////////////////////////////////////////////
                        userID = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("User").document(userID);

// Create a map for the contact details
                        Map<String, String> contactDetails = new HashMap<>();
                        contactDetails.put("name", name);
                        contactDetails.put("phoneNumber", number);
                        contactDetails.put("category", "Hotline");

// Use Firestore transaction or update to ensure field is created
                        documentReference.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists() && documentSnapshot.contains("emergencyContact")) {
                                // If the field exists, update it by adding the contact
                                documentReference.update("emergencyContact", FieldValue.arrayUnion(contactDetails))
                                        .addOnSuccessListener(unused ->
                                                Toast.makeText(requireContext(), "Contact added to Hotlines", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e ->
                                                Toast.makeText(requireContext(), "Failed to add contact: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            } else {
                                // If the field doesn't exist, initialize it with the new contact
                                Map<String, Object> data = new HashMap<>();
                                List<Map<String, String>> newEmergencyContactList = new ArrayList<>();
                                newEmergencyContactList.add(contactDetails);
                                data.put("emergencyContact", newEmergencyContactList);

                                documentReference.set(data, SetOptions.merge())
                                        .addOnSuccessListener(unused ->
                                                Toast.makeText(requireContext(), "Hotline field created and contact added.", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e ->
                                                Toast.makeText(requireContext(), "Failed to create field: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        });
                        /////////////////////////////////////////////////////////////////////////////////////////////////////
//                        Toast.makeText(requireContext(), "Contact added to Hotlines", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


//    private void showCategoryDialog(int position, boolean isEmergencyContact) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        builder.setTitle("Change Category")
//                .setItems(new String[]{"Emergency Contact", "Hotline"}, (dialog, which) -> {
//                    if (isEmergencyContact) {
//                        // Moving contact from Emergency Contacts to Hotlines
//                        String contact = emergencyContacts.remove(position);
//                        hotlines.add(contact); // Add to Hotlines
//                        HAdapter.notifyDataSetChanged();
//                        ECAdapter.notifyDataSetChanged();
//                        Toast.makeText(requireContext(), "Contact moved to Hotlines", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // Moving contact from Hotlines to Emergency Contacts
//                        String contact = hotlines.remove(position);
//                        emergencyContacts.add(contact); // Add to Emergency Contacts
//                        ECAdapter.notifyDataSetChanged();
//                        HAdapter.notifyDataSetChanged();
//                        Toast.makeText(requireContext(), "Contact moved to Emergency Contacts", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setNegativeButton("Cancel", null)
//                .show();
//    }

    private void deleteSelectedContacts(List<String> contactsToDelete) {
        for (String contact : contactsToDelete) {
            if (emergencyContacts.contains(contact)) {
                emergencyContacts.remove(contact);
            } else if (hotlines.contains(contact)) {
                hotlines.remove(contact);
            }
        }

        // Notify adapters to refresh the ListViews
        ECAdapter.notifyDataSetChanged();
        HAdapter.notifyDataSetChanged();

        Toast.makeText(requireContext(), "Selected contact(s) deleted.", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteContactDialog() {
        // Combine emergency contacts and hotlines into a single list
        List<String> allContacts = new ArrayList<>();
        allContacts.addAll(emergencyContacts);
        allContacts.addAll(hotlines);

        if (allContacts.isEmpty()) {
            Toast.makeText(requireContext(), "No contacts available to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Track selected items
        boolean[] selectedContacts = new boolean[allContacts.size()];

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Contacts to Delete")
                .setMultiChoiceItems(allContacts.toArray(new String[0]), selectedContacts, (dialog, which, isChecked) -> {
                    // Track user selections (handled by the system)
                    selectedContacts[which] = isChecked;
                })
                .setPositiveButton("Delete", (dialog, which) -> {
                    confirmDeleteContacts(allContacts, selectedContacts);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void confirmDeleteContacts(List<String> allContacts, boolean[] selectedContacts) {
        List<String> contactsToDelete = new ArrayList<>();
        for (int i = 0; i < allContacts.size(); i++) {
            if (selectedContacts[i]) {
                contactsToDelete.add(allContacts.get(i));
            }
        }

        if (contactsToDelete.isEmpty()) {
            Toast.makeText(requireContext(), "No contacts selected for deletion.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format the contacts into a readable message for the user
        StringBuilder message = new StringBuilder("Are you sure you want to delete the following contact(s)?\n\n");
        for (String contact : contactsToDelete) {
            message.append("- ").append(contact).append("\n");
        }

        TextView messageView = new TextView(requireContext());
        messageView.setText(message.toString());
        messageView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black)); // Set a readable color
        messageView.setPadding(50, 50, 50, 50); // Add some padding for better UI
        messageView.setTextSize(16); // Adjust text size if needed

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Deletion")
                .setView(messageView)
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteSelectedContacts(contactsToDelete);
                    deleteSelectedContactsFromFirestore(contactsToDelete);
                })
                .setNegativeButton("No", null)
                .show();

    }

    private void deleteSelectedContactsFromFirestore(List<String> contactsToDelete) {
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("User").document(userID);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("emergencyContact")) {
                // Retrieve the existing emergencyContact array
                List<Map<String, String>> contactList = (List<Map<String, String>>) documentSnapshot.get("emergencyContact");

                if (contactList != null) {
                    // Remove the selected contacts
                    List<Map<String, String>> updatedContactList = new ArrayList<>(contactList);
                    for (String contactToDelete : contactsToDelete) {
                        // Parse contactToDelete into its name and phone number
                        String[] parts = contactToDelete.split(" - ");
                        if (parts.length == 2) {
                            String name = parts[0].trim();
                            String phoneNumber = parts[1].trim();

                            // Find and remove the matching contact
                            updatedContactList.removeIf(contact ->
                                    name.equals(contact.get("name")) && phoneNumber.equals(contact.get("phoneNumber"))
                            );
                        }
                    }

                    // Update the emergencyContact field in Firestore
                    documentReference.update("emergencyContact", updatedContactList)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(requireContext(), "Contacts deleted successfully from Firestore.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Failed to delete contacts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(requireContext(), "No contacts found to delete in Firestore.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to retrieve contacts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    private void checkPermissionAndPickContact() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS}, PICK_CONTACT_REQUEST);
        } else {
            pickContact();
        }
    }

    private void loadContacts() {
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("User").document(userID);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("emergencyContact")) {
                // Retrieve the emergencyContact array from Firestore
                List<Map<String, String>> contactList = (List<Map<String, String>>) documentSnapshot.get("emergencyContact");
                if (contactList != null) {
                    // Clear the existing lists to avoid duplicates
                    emergencyContacts.clear();
                    hotlines.clear();

                    // Iterate through the contact list
                    for (Map<String, String> contact : contactList) {
                        String name = contact.get("name");
                        String phoneNumber = contact.get("phoneNumber");
                        String category = contact.get("category");

                        String formattedContact = name + " - " + phoneNumber;

                        // Add to the appropriate list based on the category
                        if ("Emergency Contact".equals(category)) {
                            emergencyContacts.add(formattedContact);
                        } else if ("Hotline".equals(category)) {
                            hotlines.add(formattedContact);
                        }
                    }

                    // Notify adapters to refresh the ListViews
                    ECAdapter.notifyDataSetChanged();
                    HAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(requireContext(), "No contacts found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to load contacts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }



}