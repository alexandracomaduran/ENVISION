package com.aac.envision;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        emailEditText = findViewById(R.id.emailSignUp);
        passwordEditText = findViewById(R.id.passwordSignUp);
        passwordConfirmEditText = findViewById(R.id.passwordConfirmSignUp);
        Button signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = passwordConfirmEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, getString(R.string.error_fields_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the user in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User registration success
                            Toast.makeText(SignUpActivity.this, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();

                            // After successful registration, add user data to Firestore
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            String initialRole = "User"; // Initial role for new users

                            Map<String, Object> userDoc = new HashMap<>();
                            userDoc.put("email", email);
                            userDoc.put("role", initialRole);

                            DocumentReference userRef = firestore.collection("users").document(uid);
                            userRef.set(userDoc)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Firestore document creation success
                                            } else {
                                                // Firestore document creation failed
                                            }
                                            // Navigate to login or main app
                                        }
                                    });
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, getString(R.string.registration_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}