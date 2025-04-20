package com.example.townhall;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class SubmittedTicketFragment extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_submitted_ticket, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.ticketRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Load tickets
        loadTicketsFromFirestore();
    }

    private void loadTicketsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();
        db.collection("tickets")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("SubmittedTicket", "Error loading tickets", error);
                        return;
                    }

                    if (value != null) {
                        List<Ticket> tickets = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            try {
                                Ticket ticket = null;
                                if(doc.getString("userId").equals(currentUserId))
                                    ticket = doc.toObject(Ticket.class);
                                if (ticket != null) {
                                    // Get the Firestore-generated ID
                                    String firestoreId = doc.getId();
                                    // Format the ID to match the UI (e.g., "240320")
                                    String displayId = firestoreId.substring(0, 6);
                                    ticket.setId(displayId);

                                    // Get other fields directly if needed
                                    ticket.setType(doc.getString("type"));
                                    ticket.setCategory(doc.getString("category"));
                                    ticket.setStatus("Pending Investigation");
                                    ticket.setDescription(doc.getString("description"));
                                    ticket.setDate(doc.getString("date"));
                                    ticket.setTime(doc.getString("time"));
                                    ticket.setReceiver(doc.getString("receiver"));
                                    ticket.setUserId(doc.getString("userId"));


                                    String uriString = doc.getString("uri"); // replace "uri" with your field name in Firestore
                                    if (uriString != null) {
                                        Uri uri = Uri.parse(uriString);
                                        ticket.setUri(uri);  // Set the Uri in Ticket object
                                    }

                                    tickets.add(ticket);
                                    Log.d("SubmittedTicket", "Loaded ticket with ID: " + displayId);
                                }
                            } catch (Exception e) {
                                Log.e("SubmittedTicket", "Error parsing ticket document", e);
                            }
                        }
                        // Create and set adapter
                        TicketAdapter adapter = new TicketAdapter(tickets);
                        recyclerView.setAdapter(adapter);


                    }
                });
    }


}