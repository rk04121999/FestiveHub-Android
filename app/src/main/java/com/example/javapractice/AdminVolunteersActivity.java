package com.example.javapractice;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminVolunteersActivity extends AppCompatActivity {

    private EditText volunteerName;
    private EditText volunteerEmail;
    private EditText volunteerPassword;
    private Button addVolunteerButton;
    private LinearLayout volunteerListContainer;

    private FirebaseAuth adminAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_volunteers);

        adminAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        volunteerName = findViewById(R.id.volunteerName);
        volunteerEmail = findViewById(R.id.volunteerEmail);
        volunteerPassword = findViewById(R.id.volunteerPassword);
        addVolunteerButton = findViewById(R.id.addVolunteerButton);
        volunteerListContainer = findViewById(R.id.volunteerListContainer);

        addVolunteerButton.setOnClickListener(v -> addVolunteer());

        loadVolunteers();
    }

    private void addVolunteer() {

        String name = volunteerName.getText().toString().trim();
        String email = volunteerEmail.getText().toString().trim();
        String password = volunteerPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            volunteerName.setError("Enter volunteer name");
            volunteerName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            volunteerEmail.setError("Enter valid email");
            volunteerEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            volunteerPassword.setError("Password must be at least 6 characters");
            volunteerPassword.requestFocus();
            return;
        }

        addVolunteerButton.setEnabled(false);

        FirebaseOptions options = FirebaseApp.getInstance().getOptions();

        FirebaseApp secondaryApp;

        try {
            secondaryApp = FirebaseApp.getInstance("VolunteerApp");
        } catch (IllegalStateException e) {
            secondaryApp = FirebaseApp.initializeApp(
                    getApplicationContext(),
                    options,
                    "VolunteerApp"
            );
        }

        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp);

        secondaryAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser volunteerUser =
                                secondaryAuth.getCurrentUser();

                        if (volunteerUser == null) {
                            addVolunteerButton.setEnabled(true);
                            return;
                        }

                        String uid = volunteerUser.getUid();

                        Map<String, Object> volunteer = new HashMap<>();
                        volunteer.put("name", name);
                        volunteer.put("email", email);
                        volunteer.put("role", "VOLUNTEER");
                        volunteer.put("active", true);

                        db.collection("users")
                                .document(uid)
                                .set(volunteer)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(
                                            AdminVolunteersActivity.this,
                                            "Volunteer added successfully",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    volunteerName.setText("");
                                    volunteerEmail.setText("");
                                    volunteerPassword.setText("");

                                    loadVolunteers();

                                    secondaryAuth.signOut();

                                    addVolunteerButton.setEnabled(true);
                                })
                                .addOnFailureListener(e -> {

                                    addVolunteerButton.setEnabled(true);

                                    Toast.makeText(
                                            AdminVolunteersActivity.this,
                                            e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                });

                    } else {

                        addVolunteerButton.setEnabled(true);

                        String error = "Failed to create volunteer";

                        if (task.getException() != null) {
                            error = task.getException().getMessage();
                        }

                        Toast.makeText(
                                AdminVolunteersActivity.this,
                                error,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void loadVolunteers() {

        volunteerListContainer.removeAllViews();

        db.collection("users")
                .whereEqualTo("role", "VOLUNTEER")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (com.google.firebase.firestore.QueryDocumentSnapshot document
                            : queryDocumentSnapshots) {

                        String name = document.getString("name");
                        String email = document.getString("email");
                        Boolean active = document.getBoolean("active");

                        TextView volunteerText = new TextView(
                                AdminVolunteersActivity.this
                        );

                        volunteerText.setText(
                                name + "\n" +
                                        email + "\n" +
                                        "Status: " +
                                        (Boolean.TRUE.equals(active)
                                                ? "Active"
                                                : "Disabled")
                        );

                        volunteerText.setTextSize(16);
                        volunteerText.setTextColor(
                                android.graphics.Color.BLACK
                        );
                        volunteerText.setPadding(
                                16,
                                16,
                                16,
                                16
                        );

                        volunteerListContainer.addView(volunteerText);
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AdminVolunteersActivity.this,
                            "Failed to load volunteers",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
}

