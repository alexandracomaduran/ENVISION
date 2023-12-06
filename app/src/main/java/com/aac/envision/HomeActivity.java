package com.aac.envision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Initialize UI component
        bottomNavigationView = findViewById(R.id.navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home_navigation) {

                Intent homeIntent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(homeIntent);

            } else if (item.getItemId() == R.id.profile_navigation){
                Intent profileIntent = new Intent(HomeActivity.this, ProfilePageActivity.class);
                startActivity(profileIntent);
            }
            return true;
                });
    }

}