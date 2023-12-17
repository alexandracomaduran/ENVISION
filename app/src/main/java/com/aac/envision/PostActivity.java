package com.aac.envision;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.firestore.EventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PostActivity extends AppCompatActivity {

        private static final int PICK_IMAGE_REQUEST = 1;
        private static final int PICK_VIDEO_REQUEST = 2;

        private Button chooseFileButton;
        private Button submitButton;
        private TextView uploadTextView;
        private ProgressBar progressBar;

        private Uri selectedFileUri;
        private FirebaseAuth firebaseAuth;
        private FirebaseFirestore firestore;
        private FirebaseStorage storage;
        private PostAdapter postAdapter;
        BottomNavigationView bottomNavigationView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_post);

           //Initialize Firebase Auth, Firestore, and Storage
            firebaseAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
            storage = FirebaseStorage.getInstance();

            // Initialize UI components
            chooseFileButton = findViewById(R.id.chooseFileButton);
            submitButton = findViewById(R.id.submitButton);
            uploadTextView = findViewById(R.id.uploadTextView);
            progressBar = findViewById(R.id.postProgressBar);


            chooseFileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFileChooser();
                }
            });

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        submitPost();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });


            //Initialize UI component
            bottomNavigationView = findViewById(R.id.navigation);

            bottomNavigationView.setOnItemSelectedListener(item -> {

                if(item.getItemId() == R.id.home_navigation) {

                    Intent homeIntent = new Intent(PostActivity.this, HomeActivity.class);
                    startActivity(homeIntent);

                } else if (item.getItemId() == R.id.profile_navigation){
                    Intent profileIntent = new Intent(PostActivity.this, ProfilePageActivity.class);
                    startActivity(profileIntent);
                }
                else if (item.getItemId() == R.id.post_navigation){
                    Intent profileIntent = new Intent(PostActivity.this, PostActivity.class);
                    startActivity(profileIntent);
                }
                return true;
            });

        }

        // Open the file chooser for selecting media
        private void openFileChooser() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/* video/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }

        // Handle result from file chooser
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                selectedFileUri = data.getData();
                uploadTextView.setText(R.string.file_selected + selectedFileUri.getLastPathSegment());
            }
        }

        // Logic to submit the post
        private void submitPost() throws IOException {
            if (selectedFileUri == null) {
                Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check file type
            String fileType = getContentResolver().getType(selectedFileUri);
            if (!isValidFileType(fileType)) {
                Toast.makeText(this, "Invalid file type. Please select an image, gif, or video.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check video duration if it's a video
            if (fileType.startsWith("video")) {
                int videoDuration = getVideoDuration(selectedFileUri);
                if (videoDuration > 30) {
                    Toast.makeText(this, "Video duration should be 30 seconds or less.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Upload the file to Firebase Storage
            StorageReference storageRef = storage.getReference().child("uploads/" + System.currentTimeMillis());
            storageRef.putFile(selectedFileUri)
                    .addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            // Hide progress bar
                            progressBar.setVisibility(View.GONE);

                            if (task.isSuccessful()) {
                                // File uploaded successfully, get download URL
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUri) {
                                        // Add post data to Firestore
                                        getIndexAndAddPost(downloadUri.toString());
                                    }
                                });
                            } else {
                                Toast.makeText(PostActivity.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    private void getIndexAndAddPost(String uri) {
        firestore.collection("posts").get().addOnSuccessListener(queryDocumentSnapshots -> {
            int documentCount = queryDocumentSnapshots.size();
            int index = documentCount;

            addPostToFirestore(uri, index);
        }).addOnFailureListener(e -> {
            // Handle failure
            Log.e("Firestore", "Error getting document count", e);
        });
    }
        // Add post data to Firestore
        private void addPostToFirestore(String downloadUrl, int index) {
            String uid = firebaseAuth.getCurrentUser().getUid();

            Toast.makeText(PostActivity.this, ("The index is: " + index), Toast.LENGTH_SHORT).show();
            System.out.println("The index is: " + index);

            Map<String, Object> postDoc = new HashMap<>();
            postDoc.put("GlobalUserID", uid);
            postDoc.put("MediaURL", downloadUrl);
            postDoc.put("Index", index);

            firestore.collection("users").document(uid).collection("posts").add(postDoc).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Update postReferences in user's document
                            updatePostReferences(uid, documentReference.getId());
                            //Call EventChangeListener here to update user data and reflect new post
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(PostActivity.this, "Firestore error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


            firestore.collection("posts")
                    .add(postDoc)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(PostActivity.this, "Post submitted successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity after successful submission
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                //no errors
                                Log.d("Firestore", "Post added successfully");
                            } else {
                                //handle errors
                                Exception e = task.getException();
                                Log.e("Firestore", "Error adding post", e);
                                Toast.makeText(PostActivity.this, "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    //Update postReferences in user's document
        private void updatePostReferences(String uid, String postId) {
            firestore.collection("users").document(uid)
                    .update("postReferences", FieldValue.arrayUnion(postId))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(PostActivity.this, "Firestore error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        // Check if the file type is valid (image, gif, or video)
        private boolean isValidFileType(String fileType) {
            return fileType != null && (fileType.startsWith("image") || fileType.startsWith("video"));
        }

        // Get video duration in seconds
        private int getVideoDuration(Uri videoUri) throws IOException {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                retriever.setDataSource(this, videoUri);
                String durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long duration = Long.parseLong(durationString);
                return (int) (duration / 1000); // Convert duration to seconds
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            } finally {
                retriever.release(); // Release the MediaMetadataRetriever
            }
        }



}
