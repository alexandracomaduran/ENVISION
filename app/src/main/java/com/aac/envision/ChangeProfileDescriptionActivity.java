package com.aac.envision;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangeProfileDescriptionActivity extends AppCompatActivity {

    private EditText descriptionEditText;
    private TextView changeDtextview;
    private Button submitButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_description);

        // Initialize views

        changeDtextview = findViewById(R.id.changeDTextView);
        descriptionEditText = findViewById(R.id.descriptionTextInput);
        submitButton = findViewById(R.id.submitButton);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // Set click listener
        submitButton.setOnClickListener(view -> updateProfileDescription());
    }

    private void updateProfileDescription() {
        String newDescription = descriptionEditText.getText().toString().trim();

        // Update profile description in Firestore
        if (!newDescription.isEmpty()) {
            firestore.collection("users").document(currentUser.getUid())
                    .update("pageDescription", newDescription)
                    .addOnSuccessListener(aVoid -> {
                        // Update successful
                        Toast.makeText(ChangeProfileDescriptionActivity.this, "Profile description updated", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity and go back to ProfilePageActivity
                    })
                    .addOnFailureListener(e -> {
                        // Update failed
                        Toast.makeText(ChangeProfileDescriptionActivity.this, "Failed to update profile description", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(ChangeProfileDescriptionActivity.this, "Please enter a description", Toast.LENGTH_SHORT).show();
        }
    }
}