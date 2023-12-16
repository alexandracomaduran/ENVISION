package com.aac.envision;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// Import statements

public class ChangeProfilePictureActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    private Button chooseImageButton, uploadButton;
    private ProgressBar progressBar;
    private Uri imageUri;

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_picture);

        // Initialize views
        chooseImageButton = findViewById(R.id.chooseFileButton);
        uploadButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.ppProgressBar);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("profile_pictures");
        firestore = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // Set click listeners
        chooseImageButton.setOnClickListener(view -> chooseImage());
        uploadButton.setOnClickListener(view -> uploadImage());
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);

            StorageReference fileReference = storageReference.child(currentUser.getUid() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressBar.setVisibility(View.GONE);

                        // Get the uploaded image URL
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            // Update profile picture URL in Firestore
                            firestore.collection("users").document(currentUser.getUid())
                                    .update("profilePic", imageUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        // Update successful
                                        Toast.makeText(ChangeProfilePictureActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                                        finish(); // Finish the activity and go back to ProfilePageActivity
                                    })
                                    .addOnFailureListener(e -> {
                                        // Update failed
                                        Toast.makeText(ChangeProfilePictureActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ChangeProfilePictureActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}