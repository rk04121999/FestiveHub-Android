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
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String ADMIN_EMAIL = "admin@festivehub.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.main_login);

        TextView signupText = findViewById(R.id.signupText);

        signupText.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    signupactivity.class
            );

            startActivity(intent);
        });

        TextView forgotPasswordText =
                findViewById(R.id.forgotPasswordText);

        forgotPasswordText.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    forgot.class
            );

            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String email = loginEmail.getText()
                .toString()
                .trim();

        String password = loginPassword.getText()
                .toString();

        if (TextUtils.isEmpty(email)) {

            loginEmail.setError("Enter your email");
            loginEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {

            loginPassword.setError("Enter your password");
            loginPassword.requestFocus();
            return;
        }

        loginButton.setEnabled(false);

        if (email.equals(ADMIN_EMAIL)
                && password.equals(ADMIN_PASSWORD)) {

            mAuth.signInWithEmailAndPassword(
                    ADMIN_EMAIL,
                    ADMIN_PASSWORD
            ).addOnCompleteListener(this, task -> {

                if (task.isSuccessful()) {

                    Toast.makeText(
                            MainActivity.this,
                            "Admin Login Successful",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(
                            MainActivity.this,
                            AdminDashboardActivity.class
                    );

                    startActivity(intent);
                    finish();

                } else {

                    loginButton.setEnabled(true);

                    Toast.makeText(
                            MainActivity.this,
                            "Admin Firebase login failed",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });

            return;
        }

        mAuth.signInWithEmailAndPassword(
                email,
                password
        ).addOnCompleteListener(this, task -> {

            if (!task.isSuccessful()) {

                loginButton.setEnabled(true);

                String error;

                if (task.getException() != null) {
                    error = task.getException().getMessage();
                } else {
                    error = "Login failed";
                }

                Toast.makeText(
                        MainActivity.this,
                        error,
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            if (mAuth.getCurrentUser() == null) {

                loginButton.setEnabled(true);

                Toast.makeText(
                        MainActivity.this,
                        "User session not found",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            String uid = mAuth.getCurrentUser().getUid();

            db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        loginButton.setEnabled(true);

                        if (documentSnapshot.exists()) {

                            String role =
                                    documentSnapshot.getString("role");

                            if ("VOLUNTEER".equals(role)) {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Volunteer Login Successful",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent = new Intent(
                                        MainActivity.this,
                                        VolunteerDashboardActivity.class
                                );

                                startActivity(intent);
                                finish();

                            } else {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Login successful",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent = new Intent(
                                        MainActivity.this,
                                        homepage.class
                                );

                                startActivity(intent);
                                finish();
                            }

                        } else {

                            Toast.makeText(
                                    MainActivity.this,
                                    "Login successful",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    MainActivity.this,
                                    homepage.class
                            );

                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {

                        loginButton.setEnabled(true);

                        Toast.makeText(
                                MainActivity.this,
                                "Role check failed: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });
    }
}
