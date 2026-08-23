package com.example.javapractice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        // Login fields
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.main_login);

        // Sign Up
        TextView signupText = findViewById(R.id.signupText);

        signupText.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    signupactivity.class
            );

            startActivity(intent);
        });

        // Forgot Password
        TextView forgotPasswordText =
                findViewById(R.id.forgotPasswordText);

        forgotPasswordText.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    forgot.class
            );

            startActivity(intent);
        });

        // Login button
        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String email = loginEmail.getText()
                .toString()
                .trim();

        String password = loginPassword.getText()
                .toString();

        // Check email
        if (TextUtils.isEmpty(email)) {
            loginEmail.setError("Enter your email");
            loginEmail.requestFocus();
            return;
        }

        // Check password
        if (TextUtils.isEmpty(password)) {
            loginPassword.setError("Enter your password");
            loginPassword.requestFocus();
            return;
        }

        loginButton.setEnabled(false);

        // Firebase Login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                MainActivity.this,
                                "Login successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        // Open Home
                        Intent intent = new Intent(
                                MainActivity.this,
                                homepage.class
                        );

                        startActivity(intent);
                        finish();

                    } else {

                        loginButton.setEnabled(true);

                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Login failed";

                        Toast.makeText(
                                MainActivity.this,
                                error,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}