package com.aac.envision;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ProfilePageActivity extends AppCompatActivity {

    private TextView emailTextView;
    private TextView descriptionTextView;
    private ImageView profilePictureImageView;
    private RecyclerView recyclerView;
    private PostAdapterUser adapterUser;
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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        bottomNavigationView = findViewById(R.id.navigation);

        // Set up bottom navigation item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home_navigation) {
                Intent homeIntent = new Intent(ProfilePageActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            } else if (item.getItemId() == R.id.profile_navigation) {
                // Already in the profile page
            } else if (item.getItemId() == R.id.post_navigation) {
                Intent postIntent = new Intent(ProfilePageActivity.this, PostActivity.class);
                startActivity(postIntent);
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

           //Retrieve all posts and filter based on user ID

            firestore.collection("posts")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Post> allPosts = new ArrayList<>();

                        for(DocumentSnapshot document : queryDocumentSnapshots) {
                            Post post = document.toObject(Post.class);
                            if(post != null) {
                                allPosts.add(post);
                            }
                        }

                        //Filter posts based on GlobalUserID
                        List<Post> userPosts = filterPostsByUserID(allPosts, userId);

                        adapterUser = new PostAdapterUser(userPosts, this);
                        recyclerView.setAdapter(adapterUser);

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failure in retrieving posts", Toast.LENGTH_SHORT).show();
                    });
        }

    }

    // Helper method to filter posts based on user ID
    private List<Post> filterPostsByUserID(List<Post> allPosts, String userId) {
        List<Post> userPosts = new ArrayList<>();

        for (Post post : allPosts) {
            if (userId.equals(post.getGlobalUserID())) {
                userPosts.add(post);
            }
        }

        return userPosts;
    }


}