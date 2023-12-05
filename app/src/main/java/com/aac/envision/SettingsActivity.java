package com.aac.envision;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;

public class SettingsActivity extends AppCompatActivity {

    private Button usersManagementButton;
    private Button postsManagementButton;
    private Button changeProfilePicButton;
    private Button changeProfileDescriptionButton;
    private Button removeAccountButton;
    private Button logoutButton;

    private boolean isAdmin = false; // Set this based on the user's role.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize buttons
        usersManagementButton = findViewById(R.id.usersManagement);
        postsManagementButton = findViewById(R.id.postsManagement);
        changeProfilePicButton = findViewById(R.id.changeProfilePic);
        changeProfileDescriptionButton = findViewById(R.id.changeProfileDescription);
        removeAccountButton = findViewById(R.id.removeAccountButton);
        logoutButton = findViewById(R.id.logoutButton);

        if(currentUser != null) {
            String userEmail = currentUser.getEmail();

            //Query
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersCollection = db.collection("users");

            usersCollection.document(currentUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()) {
                        //Document exists, retrieve the role field
                        String userRole = documentSnapshot.getString("role");

                        //Visbility of Buttons
                        if ("Admin".equals(userRole)) {
                            usersManagementButton.setVisibility(View.VISIBLE);
                            postsManagementButton.setVisibility(View.VISIBLE);
                            changeProfilePicButton.setVisibility(View.GONE);
                            changeProfileDescriptionButton.setVisibility(View.GONE);
                        } else {
                            usersManagementButton.setVisibility(View.GONE);
                            postsManagementButton.setVisibility(View.GONE);
                            changeProfilePicButton.setVisibility(View.VISIBLE);
                            changeProfileDescriptionButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        //Document doesn't exist
                        Toast.makeText(SettingsActivity.this, "Document doesn't exist. ", Toast.LENGTH_SHORT).show();
                    }
                }


            })
                    .addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SettingsActivity.this, "Failed to fetch user role", Toast.LENGTH_SHORT).show();

                        }
                    });

        }




        // Click Listeners for Buttons
        usersManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Users Management button click for admins
            }
        });

        postsManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Posts Management button click for admins
            }
        });

        changeProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Change Profile Picture button click for regular users
            }
        });

        changeProfileDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Change Profile Description button click for regular users
            }
        });

        removeAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Remove Account button click (common for both)
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Logout button click (common for both)
            }
        });
    }
}
