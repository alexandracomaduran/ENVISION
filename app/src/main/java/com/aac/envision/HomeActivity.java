package com.aac.envision;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<User> userArrayList;
    private ArrayList<Post> postArrayList;
    private PostAdapter adapter;

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;


    public HomeActivity(){}
    public HomeActivity(FirebaseFirestore firestore1){
        this.firestore = firestore1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userArrayList = new ArrayList<>();
        postArrayList = new ArrayList<>();
        adapter = new PostAdapter(firestore, HomeActivity.this,  postArrayList);
        recyclerView.setAdapter(adapter);

        // Set up bottom navigation item selected listener
        //Initialize UI component
        bottomNavigationView = findViewById(R.id.navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home_navigation) {

                Intent homeIntent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(homeIntent);

            } else if (item.getItemId() == R.id.profile_navigation) {
                Intent profileIntent = new Intent(HomeActivity.this, ProfilePageActivity.class);
                startActivity(profileIntent);
            } else if (item.getItemId() == R.id.post_navigation) {
                Intent profileIntent = new Intent(HomeActivity.this, PostActivity.class);
                startActivity(profileIntent);
            }
            return true;
        });
        EventChangeListener();
    }


    private void EventChangeListener() {
        firestore.collection("users").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore error", error.getMessage());
                return;
            }

            userArrayList.clear(); // Clear the list before adding new data
            for (QueryDocumentSnapshot document : value) {
                userArrayList.add(document.toObject(User.class));
            }
            adapter.notifyDataSetChanged(); // Notify adapter of the data change
        });

        firestore.collection("posts").orderBy("Index", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore error", error.getMessage());
                return;
            }

            postArrayList.clear(); // Clear the list before adding new data
            for (QueryDocumentSnapshot document : value) {
                postArrayList.add(document.toObject(Post.class));
            }
            adapter.notifyDataSetChanged(); // Notify adapter of the data change
        });
    }
}