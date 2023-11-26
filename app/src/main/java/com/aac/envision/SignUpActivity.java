package com.aac.envision;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        usernameEditText = findViewById(R.id.usernameSignUp);
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
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = passwordConfirmEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, getString(R.string.error_fields_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show();
            return;
        }

        // Here, you can call Firebase Authentication method to perform sign up
        firebaseAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success
                        Toast.makeText(SignUpActivity.this, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
                        // Navigate to login or main app
                    } else {
                        // If sign up fails, display a message to the user.
                        Toast.makeText(SignUpActivity.this, getString(R.string.registration_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}