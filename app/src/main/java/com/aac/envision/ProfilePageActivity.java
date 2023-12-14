package com.aac.envision;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class ProfilePageActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        bottomNavigationView = findViewById(R.id.navigation);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        // Set up bottom navigation item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home_navigation) {
                Intent homeIntent = new Intent(ProfilePageActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            } else if (item.getItemId() == R.id.profile_navigation){
                Intent profileIntent = new Intent(ProfilePageActivity.this, ProfilePageActivity.class);
                startActivity(profileIntent);
            } else if (item.getItemId() == R.id.post_navigation) {
                Intent postIntent = new Intent(ProfilePageActivity.this, PostActivity.class);
                startActivity(postIntent);
            }
            return true;
        });

        // Load user-specific posts from Firestore ordered by timestamp
        loadUserPosts();
    }

    private void loadUserPosts() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            firestore.collection("users").document(uid).collection("posts")
                    .orderBy("Timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            postList.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                postList.add(post);
                            }
                            postAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("ProfilePageActivity", "Error getting user posts", task.getException());
                            Toast.makeText(ProfilePageActivity.this, "Error getting user posts", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}