package com.example.javapractice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private TextView tvProfilePhone;

    private LinearLayout registeredEventsContainer;

    private LinearLayout navHome;
    private LinearLayout navEvents;
    private LinearLayout navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);

        registeredEventsContainer =
                findViewById(R.id.registeredEventsContainer);

        navHome = findViewById(R.id.navHome);
        navEvents = findViewById(R.id.navEvents);
        navProfile = findViewById(R.id.navProfile);

        Button btnEditProfile =
                findViewById(R.id.btnEditProfile);

        Button btnLogout =
                findViewById(R.id.btnLogout);

        loadUserDetails();
        loadRegisteredEvents();

        btnEditProfile.setOnClickListener(v ->
                showEditProfileDialog()
        );

        navHome.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    homepage.class
            );

            startActivity(intent);
            finish();
        });

        navEvents.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    EventManagementActivity.class
            );

            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
        });

        btnLogout.setOnClickListener(v -> {

            mAuth.signOut();

            SharedPreferences preferences =
                    getSharedPreferences(
                            "UserPrefs",
                            MODE_PRIVATE
                    );

            preferences.edit().clear().apply();

            Intent intent =
                    new Intent(
                            ProfileActivity.this,
                            MainActivity.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }

    private void loadUserDetails() {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "User not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String email =
                currentUser.getEmail();

        if (email != null) {
            tvProfileEmail.setText(email);
        }

        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        tvProfileName.setText("Not added");
                        tvProfilePhone.setText("Not added");

                        return;
                    }

                    String name =
                            documentSnapshot.getString("name");

                    String phone =
                            documentSnapshot.getString("phone");

                    tvProfileName.setText(
                            name != null && !name.isEmpty()
                                    ? name
                                    : "Not added"
                    );

                    tvProfilePhone.setText(
                            phone != null && !phone.isEmpty()
                                    ? phone
                                    : "Not added"
                    );
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to load profile",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void showEditProfileDialog() {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        int padding = 50;

        layout.setPadding(
                padding,
                10,
                padding,
                10
        );

        EditText nameInput =
                new EditText(this);

        nameInput.setHint("Name");
        nameInput.setSingleLine(true);

        EditText phoneInput =
                new EditText(this);

        phoneInput.setHint("Phone");
        phoneInput.setSingleLine(true);
        phoneInput.setInputType(
                InputType.TYPE_CLASS_PHONE
        );

        String currentName =
                tvProfileName.getText().toString();

        String currentPhone =
                tvProfilePhone.getText().toString();

        if (!currentName.equals("Not added")) {
            nameInput.setText(currentName);
        }

        if (!currentPhone.equals("Not added")) {
            phoneInput.setText(currentPhone);
        }

        layout.addView(nameInput);
        layout.addView(phoneInput);

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Edit Profile")
                        .setView(layout)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Save",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                dialogInterface -> {

                    Button saveButton =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    saveButton.setOnClickListener(v -> {

                        String name =
                                nameInput.getText()
                                        .toString()
                                        .trim();

                        String phone =
                                phoneInput.getText()
                                        .toString()
                                        .trim();

                        if (name.isEmpty()) {

                            nameInput.setError(
                                    "Enter your name"
                            );

                            return;
                        }

                        if (phone.isEmpty()) {

                            phoneInput.setError(
                                    "Enter your phone number"
                            );

                            return;
                        }

                        saveProfile(
                                currentUser.getUid(),
                                name,
                                phone,
                                dialog
                        );
                    });
                }
        );

        dialog.show();
    }

    private void saveProfile(
            String userId,
            String name,
            String phone,
            AlertDialog dialog
    ) {

        java.util.Map<String, Object> profileData =
                new java.util.HashMap<>();

        profileData.put("name", name);
        profileData.put("phone", phone);

        db.collection("users")
                .document(userId)
                .set(profileData, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(unused -> {

                    tvProfileName.setText(name);
                    tvProfilePhone.setText(phone);

                    dialog.dismiss();

                    Toast.makeText(
                            this,
                            "Profile updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Update failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void loadRegisteredEvents() {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        String userId =
                currentUser.getUid();

        db.collection("registrations")
                .whereEqualTo(
                        "userId",
                        userId
                )
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            registeredEventsContainer
                                    .removeAllViews();

                            if (queryDocumentSnapshots
                                    .isEmpty()) {

                                TextView noEvents =
                                        new TextView(this);

                                noEvents.setText(
                                        "You have not registered for any events yet."
                                );

                                noEvents.setTextSize(16);

                                noEvents.setTextColor(
                                        getResources().getColor(
                                                android.R.color.darker_gray
                                        )
                                );

                                noEvents.setPadding(
                                        0,
                                        20,
                                        0,
                                        20
                                );

                                registeredEventsContainer
                                        .addView(noEvents);

                                return;
                            }

                            for (
                                    com.google.firebase.firestore.DocumentSnapshot document
                                    : queryDocumentSnapshots
                                    .getDocuments()
                            ) {

                                addRegisteredEventCard(
                                        document
                                );
                            }
                        }
                )
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Error loading registered events: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void addRegisteredEventCard(
            com.google.firebase.firestore.DocumentSnapshot document
    ) {

        View eventView =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.registered_event_item,
                                registeredEventsContainer,
                                false
                        );
        CardView registeredEventCard =
                eventView.findViewById(R.id.registeredEventCard);

        TextView tvEventName =
                eventView.findViewById(
                        R.id.tvRegisteredEventName
                );

        TextView tvEventDate =
                eventView.findViewById(
                        R.id.tvRegisteredEventDate
                );

        TextView tvEventTime =
                eventView.findViewById(
                        R.id.tvRegisteredEventTime
                );

        TextView tvEventPlace =
                eventView.findViewById(
                        R.id.tvRegisteredEventPlace
                );

        TextView tvTicketPrice =
                eventView.findViewById(
                        R.id.tvRegisteredTicketPrice
                );

        String eventName =
                document.getString("eventName");

        String eventDate =
                document.getString("eventDate");

        String eventTime =
                document.getString("eventTime");

        String eventPlace =
                document.getString("eventPlace");

        String ticketPrice =
                document.getString("ticketPrice");

        tvEventName.setText(
                eventName != null
                        ? eventName
                        : "Event"
        );

        tvEventDate.setText(
                           "📅 " +
                        (
                                eventDate != null
                                        ? eventDate
                                        : "-"
                        )
        );

        tvEventTime.setText(
                "🕐 " +
                        (
                                eventTime != null
                                        ? eventTime
                                        : "-"
                        )
        );

        tvEventPlace.setText(
                "📍 " +
                        (
                                eventPlace != null
                                        ? eventPlace
                                        : "-"
                        )
        );

        tvTicketPrice.setText(
                "₹" +
                        (
                                ticketPrice != null
                                        ? ticketPrice
                                        : "0"
                        )
        );

        registeredEventsContainer.addView(eventView);

        registeredEventCard.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ProfileActivity.this,
                            MyRegistrationActivity.class
                    );

            intent.putExtra(
                    "registrationId",
                    document.getString("registrationId")
            );

            intent.putExtra(
                    "userId",
                    document.getString("userId")
            );

            intent.putExtra(
                    "userEmail",
                    document.getString("userEmail")
            );

            intent.putExtra(
                    "eventId",
                    document.getString("eventId")
            );

            intent.putExtra(
                    "eventName",
                    document.getString("eventName")
            );

            intent.putExtra(
                    "eventDate",
                    document.getString("eventDate")
            );

            intent.putExtra(
                    "eventTime",
                    document.getString("eventTime")
            );

            intent.putExtra(
                    "eventPlace",
                    document.getString("eventPlace")
            );

            startActivity(intent);
        });




    }

    @Override
    protected void onResume() {

        super.onResume();

        if (mAuth != null &&
                registeredEventsContainer != null) {

            loadUserDetails();
            loadRegisteredEvents();
        }
    }
}

