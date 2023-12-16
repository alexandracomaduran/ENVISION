package com.aac.envision;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;  // Import Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ProfilePageActivity extends AppCompatActivity {

    private TextView emailTextView;
    private TextView descriptionTextView;
    private ImageView profilePictureImageView;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

        // Initialize UI components after setting content view
        emailTextView = findViewById(R.id.profilepage_email);
        descriptionTextView = findViewById(R.id.profilepage_description);
        profilePictureImageView = findViewById(R.id.profilepage_profilepicture);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        bottomNavigationView = findViewById(R.id.navigation);
        // Set up bottom navigation item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home_navigation) {
                Intent homeIntent = new Intent(ProfilePageActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            } else if (item.getItemId() == R.id.profile_navigation) {
                Intent profileIntent = new Intent(ProfilePageActivity.this, ProfilePageActivity.class);
                startActivity(profileIntent);
            } else if (item.getItemId() == R.id.post_navigation) {
                Intent profileIntent = new Intent(ProfilePageActivity.this, PostActivity.class);
                startActivity(profileIntent);
            }
            return true;
        });

        MaterialButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(view -> {
            Intent settingsIntent = new Intent(ProfilePageActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            firestore.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userEmail = documentSnapshot.getString("email");
                            String userDescription = documentSnapshot.getString("pageDescription");


                            emailTextView.setText(userEmail);
                            descriptionTextView.setText(userDescription);

                            String profilePicUrl = documentSnapshot.getString("profilePic");

                            if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(profilePicUrl)
                                        .into(profilePictureImageView);
                            } else {
                                Glide.with(this)
                                        .load(R.drawable.default_profile_image)
                                        .into(profilePictureImageView);
                            }
                        }
                    });

            Query query = firestore.collection("posts").whereEqualTo("GlobalUserId", userId).orderBy("Index", Query.Direction.DESCENDING);
            adapter = new PostAdapter(createOptionsForAdapter(query));

            recyclerView.setAdapter(adapter);
        }
    }

    private FirestoreRecyclerOptions<Post> createOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();
    }
}