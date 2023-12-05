package com.aac.envision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private Button adminButton;
    private Button logoutButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        logoutButton = findViewById(R.id.logoutButton);

        if (currentUser != null) {
            usernameTextView.setText(currentUser.getEmail());
            // Check if the user is an admin
            // This might involve checking a field in Firebase or Shared Preferences
            // For simplicity, let's assume we check from Shared Preferences
            boolean isAdmin = checkIfUserIsAdmin(currentUser);
            adminButton.setVisibility(isAdmin ? View.VISIBLE : View.INVISIBLE);
        }

        logoutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();
            navigateToLogin();
        });

        adminButton.setOnClickListener(v -> {
            // Navigate to admin area
            navigateToAdminArea();
        });
    }

    private boolean checkIfUserIsAdmin(FirebaseUser user) {
        // Implement your logic to determine if the user is an admin
        // For example, checking Shared Preferences or a field in Firebase
        return false; // Placeholder return
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToAdminArea() {
        // Navigate to Admin Area
    }
}