package com.aac.envision;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
            if (item.getItemId() == R.id.home_navigation) {
                Intent homeIntent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            } else if (item.getItemId() == R.id.profile_navigation) {
                Intent profileIntent = new Intent(HomeActivity.this, ProfilePageActivity.class);
                startActivity(profileIntent);
            } else if (item.getItemId() == R.id.post_navigation) {
                Intent postIntent = new Intent(HomeActivity.this, PostActivity.class);
                startActivity(postIntent);
            }
            return true;
        });

        // Load posts from Firestore ordered by timestamp
        loadPosts();
    }

    private void loadPosts() {
        firestore.collection("posts")
                .orderBy("Timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = document.toObject(Post.class);
                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("HomeActivity", "Error getting posts", task.getException());
                        Toast.makeText(HomeActivity.this, "Error getting posts", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}