package com.aac.envision;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private FirebaseAuth firebaseAuth;
    private BottomNavigationView bottomNavigationView;

    private boolean isAdmin = false; // Set this based on the user's role.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize buttons
        usersManagementButton = findViewById(R.id.usersManagement);
        postsManagementButton = findViewById(R.id.postsManagement);
        changeProfilePicButton = findViewById(R.id.changeProfilePic);
        changeProfileDescriptionButton = findViewById(R.id.changeProfileDescription);
        logoutButton = findViewById(R.id.logoutButton);
        bottomNavigationView = findViewById(R.id.navigation);

        if(currentUser != null) {
            String userUID = currentUser.getUid();

            //Query
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersCollection = db.collection("users");

            usersCollection.document(userUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()) {
                        //Document exists, retrieve the role field
                        String userRole = documentSnapshot.getString("role");

                        //Visibility of Buttons
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

        } else {
            Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }


// Set up bottom navigation item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home_navigation) {
                Intent homeIntent = new Intent(SettingsActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            } else if (item.getItemId() == R.id.profile_navigation){
                Intent profileIntent = new Intent(SettingsActivity.this, ProfilePageActivity.class);
                startActivity(profileIntent);
            } else if (item.getItemId() == R.id.post_navigation) {
                Intent postIntent = new Intent(SettingsActivity.this, PostActivity.class);
                startActivity(postIntent);
            }
            return true;
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out the user
                FirebaseAuth.getInstance().signOut();

                // Navigate to the LoginActivity or your desired destination after logout
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the SettingsActivity
            }
        });



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
                Intent profilePicIntent = new Intent(SettingsActivity.this, ChangeProfilePictureActivity.class);
                startActivity(profilePicIntent);
            }
        });

        changeProfileDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent descriptionIntent = new Intent(SettingsActivity.this, ChangeProfileDescriptionActivity.class);
                startActivity(descriptionIntent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out the user
                FirebaseAuth.getInstance().signOut();

                // Navigate to the LoginActivity or your desired destination after logout
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the SettingsActivity
            }
        });
    }
}
