package com.aac.envision;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PostsManagementActivity extends AppCompatActivity {

    private ListView postsListView;
    private Button deletePostButton;
    private List<Post> postList;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posts_management_activity);

        // Initialize views
        postsListView = findViewById(R.id.postListView);
        deletePostButton = findViewById(R.id.deletePostButton);


        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();

        postList = new ArrayList<>();
        fetchPosts();

        // Set up click listener for item deletion
        deletePostButton.setOnClickListener(view -> deleteSelectedPosts());
    }

    private void fetchPosts() {
        firestore.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        if (post != null) {
                            postList.add(post);
                        }
                    }

                    // Populate the ListView with posts using custom adapter
                    PostsAdapter adapter = new PostsAdapter(postList);
                    postsListView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    // Failed to fetch posts
                    Toast.makeText(this, "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteSelectedPosts() {
        // Implement the logic to delete the selected posts
        // Iterate through postList and delete the selected posts
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).isSelected()) {
                int selectedIndex = postList.get(i).getIndex();

                int finalI = i;
                // Delete the post based on the index
                firestore.collection("posts")
                        .whereEqualTo("Index", selectedIndex)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                String postId = document.getId();
                                firestore.collection("posts").document(postId)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            // Post deleted successfully
                                            Toast.makeText(this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                                            postList.remove(finalI);
                                            ((PostsAdapter) postsListView.getAdapter()).notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Failed to delete post
                                            Toast.makeText(this, "Failed to delete post", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Failed to fetch post details
                            Toast.makeText(this, "Failed to fetch post details", Toast.LENGTH_SHORT).show();
                        });
            }
        }

        clearSelection();
    }

    private void clearSelection() {
        for (Post post : postList) {
            post.setSelected(false);
        }
        ((PostsAdapter) postsListView.getAdapter()).notifyDataSetChanged();
    }

    private class PostsAdapter extends ArrayAdapter<Post> {

        PostsAdapter(List<Post> posts) {
            super(PostsManagementActivity.this, R.layout.list_item_with_checkbox, posts);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_with_checkbox, parent, false);
            }

            Post currentPost = getItem(position);

            String globalUserID = currentPost.getGlobalUserID();
            fetchUserEmail(globalUserID, view);

            CheckBox checkBox = view.findViewById(R.id.checkbox);
            checkBox.setChecked(getItem(position).isSelected());

            ImageView postImageView = view.findViewById(R.id.postImageView);

            String mediaURL = currentPost.getMediaURL();
            if(mediaURL != null && !mediaURL.isEmpty()) {
                Glide.with(getContext()).load(mediaURL).into(postImageView);
            } else {
                //IN THE CASE THERE IS NO MEDIA THERE, PUT A DUMMY CASE.
                postImageView.setImageResource(R.drawable.default_profile_image);
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> getItem(position).setSelected(isChecked));

            return view;
        }
    }
    private void fetchUserEmail(String globalUserID, View view) {
        // Fetch user email based on GlobalUserID
        firestore.collection("users")
                .document(globalUserID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userEmail = documentSnapshot.getString("email");
                        // Set the user email in the TextView or wherever you want to display it
                        TextView emailTextView = view.findViewById(R.id.emailTextView);
                        emailTextView.setText(userEmail);
                    } else {
                        Toast.makeText(this, "User document does not exist",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to fetch user details
                });
    }

}