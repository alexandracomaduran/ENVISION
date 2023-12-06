package com.aac.envision;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class ProfilePageActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        bottomNavigationView = findViewById(R.id.navigation);



        // Set up bottom navigation item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home_navigation) {

                Intent homeIntent = new Intent(ProfilePageActivity.this, HomeActivity.class);
                startActivity(homeIntent);

            } else if (item.getItemId() == R.id.profile_navigation){
                Intent profileIntent = new Intent(ProfilePageActivity.this, ProfilePageActivity.class);
                startActivity(profileIntent);
            }
            return true;
        });
        //Access settings button by its ID
        View settingsButton = findViewById(R.id.settingsButton);

        //Set a click listener for the settings button
        settingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Navigate to the SettingsActivity
                Intent settingsIntent = new Intent(ProfilePageActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

    }
}