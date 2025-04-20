package com.example.townhall;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText usernameEditText, nameEditText, phoneNumberEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    Button Btn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ImageView imageView2;

    private boolean isGallerySelected = false;
    private boolean isCameraSelected = false;
    private String imageUrl = "";

    private ActivityResultLauncher<String[]> permissionsLauncher;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    private Uri cameraImageUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usernameEditText = findViewById(R.id.username);
        nameEditText = findViewById(R.id.name);
        phoneNumberEditText = findViewById(R.id.phoneNumber);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.passwordconfirmation);
        Btn = findViewById(R.id.signupButton);
        imageView2 = findViewById(R.id.imageView2);


        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        imageView2.setOnClickListener(v -> showImageUploadDialog());


        // Permissions logic for camera and gallery
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

        // Request permissions if needed


        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            imageView2.setImageURI(imageUri);
                            imageView2.setTag(imageUri); // Set the URI as a tag
                            Log.d("TicketFragment", "Gallery image URI set: " + imageUri);
                        }
                    }
                });
        // Initialize camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && cameraImageUri != null) {
                        imageView2.setImageURI(cameraImageUri);
                        imageView2.setTag(cameraImageUri); // Set the URI as a tag
                        Log.d("TicketFragment", "Camera image URI set: " + cameraImageUri);
                    }
                });



    }







    private void showImageUploadDialog() {
        String[] options = {"Select from Gallery", "Open Camera"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsLauncher.launch(new String[]{android.Manifest.permission.CAMERA});
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
            cameraImageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraImageUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                cameraLauncher.launch(intent);  // Use the camera launcher
            } else {
                Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If permissions are not granted, show a toast or handle the error
            Toast.makeText(this, "Permissions are required to use the camera", Toast.LENGTH_SHORT).show();
        }
    }























    private void registerNewUser() {
        String username = usernameEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        Uri imageUri = (Uri) imageView2.getTag();
        String imageUriString = imageUri != null ? imageUri.toString() : "";

        if (username.isEmpty() || name.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(SignUp.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(SignUp.this, "Password must meet criteria", Toast.LENGTH_LONG).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Create a user object to store in Firestore
                            String userId = mAuth.getCurrentUser().getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("username", username);
                            user.put("name", name);
                            user.put("phoneNumber", phoneNumber);
                            user.put("email", email);
                            user.put("ImageUri", imageUriString);

                            // Save user data to Firestore
                            db.collection("User").document(userId)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignUp.this, "Registration successful!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(SignUp.this, Login.class);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(SignUp.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            Toast.makeText(SignUp.this, "Registration failed! Please try again later.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_])[A-Za-z\\d\\W_]{8,12}$";
        return password.matches(passwordPattern);
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}