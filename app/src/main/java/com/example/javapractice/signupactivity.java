package com.example.javapractice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class signupactivity extends AppCompatActivity{

    private EditText usernameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private Button createButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(
                savedInstanceState
        );

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signupactivity);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                        Insets systemBars = insets.getInsets(
                                WindowInsetsCompat.Type.systemBars()
                        );

                        v.setPadding(
                                systemBars.left,
                                systemBars.top,
                                systemBars.right,
                                systemBars.bottom
                        );
                        return insets;
                }
        );

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        usernameEditText = findViewById(R.id.usernameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        createButton = findViewById(R.id.createButton);

        TextView loginText = findViewById(R.id.loginText);

        loginText.setOnClickListener(v -> {

            Intent intent = new Intent(
                    signupactivity.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();

        });


        createButton.setOnClickListener(v -> createAccount());


    }
    private void createAccount(){
        String username = usernameEditText.getText()
                .toString()
                .trim();
        String phone = phoneEditText.getText()
                .toString()
                .trim();
        String email = emailEditText.getText()
                .toString()
                .trim();
        String password = passwordEditText.getText()
                .toString();
        String confirmPassword = confirmPasswordEditText.getText()
                .toString();

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Enter username");
            usernameEditText.requestFocus();
            return;

        }
        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError("Enter phone number");
            phoneEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)){
            passwordEditText.setError("Enter password");
            passwordEditText.requestFocus();
            return;
        }
        if (password.length() < 6){
            passwordEditText.setError(
                    "Password must be at least 6 characters"
            );
            passwordEditText.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)){
            confirmPasswordEditText.setError(
                    "Passwords do not match"
            );
            confirmPasswordEditText.requestFocus();
            return;
        }

        createButton.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();

                        Map<String, Object> user = new HashMap<>();

                        user.put("username", username);
                        user.put("phone", phone);
                        user.put("email",email);

                        firestore.collection("users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(
                                            signupactivity.this,
                                            "Account created successfully",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    Intent intent = new Intent(
                                            signupactivity.this,
                                            MainActivity.class
                                    );

                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    createButton.setEnabled(true);

                                    Toast.makeText(
                                            signupactivity.this,
                                            "Firestore error : "
                                                                + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                });

                    }else {
                        createButton.setEnabled(true);

                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Account creation failed";
                        Toast.makeText(
                                signupactivity.this,
                                error,
                                Toast.LENGTH_LONG
                        ).show();
                    }

                });

    }



}