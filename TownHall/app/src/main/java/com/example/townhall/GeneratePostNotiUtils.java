package com.example.townhall;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to generate notifications for post owners
 * when there is an interaction (like upvoting, downvoting, or commenting)
 * by other users.
 */
public class GeneratePostNotiUtils {

    private static final String TAG = "GeneratePostNotiUtils"; // Tag for logging

    /**
     * This method generates a notification for the post owner when someone interacts with their post.
     *
     * @param postId       ID of the post being interacted with.
     * @param actionType   Type of interaction (e.g., "upvote", "downvote", "comment").
     * @param actionUserId ID of the user who interacted with the post.
     */
    public static void generatePostNoti(String postId, String actionType, String actionUserId) {
        // Log the interaction details
        Log.d(TAG, "generatePostNoti: postId = " + postId + ", actionType = " + actionType + ", actionUserId = " + actionUserId);

        FirebaseFirestore db = FirebaseFirestore.getInstance(); // Get Firestore instance

        // Get the reference to the post document
        DocumentReference postRef = db.collection("posts").document(postId);

        // Use a WriteBatch to perform multiple writes atomically
        WriteBatch batch = db.batch();

        // Define the notification message based on the action type
        String notificationMessage = generateNotificationMessage(actionType, actionUserId);

        // Get the user ID of the post owner (e.g., who created the post)
        postRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String postOwnerId = documentSnapshot.getString("userId"); // Get post owner's user ID
                if (postOwnerId != null) {
                    // Log the post owner's user ID
                    Log.d(TAG, "Post owner ID: " + postOwnerId);

                    // Create a new notification document for the post owner
                    Map<String, Object> notificationData = new HashMap<>();
                    notificationData.put("message", notificationMessage);  // Set the notification message
                    notificationData.put("timestamp", System.currentTimeMillis()); // Set the timestamp
                    notificationData.put("postId", postId); // Associate the notification with the post

                    // Add the notification to the batch write for the post owner
                    DocumentReference notificationRef = db.collection("users")
                            .document(postOwnerId)
                            .collection("postnotis") // Store notifications in this sub-collection
                            .document();
                    batch.set(notificationRef, notificationData); // Add the notification to the batch
                    Log.d(TAG, "Notification added for post owner: " + postOwnerId);
                } else {
                    Log.e(TAG, "Post owner ID not found.");
                }
            } else {
                Log.e(TAG, "Post not found.");
            }

            // Commit the batch write to Firestore
            batch.commit().addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Notification successfully written to Firestore.");
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error writing notification to Firestore", e);
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching post document", e);
        });
    }

    /**
     * Generates a notification message based on the action type.
     *
     * @param actionType   The type of action (e.g., "upvote", "downvote", "comment").
     * @param actionUserId The ID of the user performing the action.
     * @return A string containing the notification message.
     */
    private static String generateNotificationMessage(String actionType, String actionUserId) {
        // Log the action type and user ID
        Log.d(TAG, "Generating notification message for actionType: " + actionType + ", actionUserId: " + actionUserId);

        switch (actionType) {
            case "upvote":
                // Message for when a post gets an upvote
                return "User " + actionUserId + " upvoted your post!";
            case "downvote":
                // Message for when a post gets a downvote
                return "User " + actionUserId + " downvoted your post!";
            case "comment":
                // Message for when a post gets a comment
                return "User " + actionUserId + " commented on your post!";
            default:
                // Default case in case of unknown action types
                return "User " + actionUserId + " interacted with your post!";
        }
    }
}