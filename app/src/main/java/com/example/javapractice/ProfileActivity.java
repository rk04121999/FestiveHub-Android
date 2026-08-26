package com.example.javapractice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // User details
    private TextView tvProfileEmail;

    // Registered events
    private LinearLayout registeredEventsContainer;

    // Bottom navigation
    private LinearLayout navHome;
    private LinearLayout navEvents;
    private LinearLayout navProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();


        tvProfileEmail =
                findViewById(R.id.tvProfileEmail);


        registeredEventsContainer =
                findViewById(
                        R.id.registeredEventsContainer
                );


        navHome =
                findViewById(R.id.navHome);

        navEvents =
                findViewById(R.id.navEvents);

        navProfile =
                findViewById(R.id.navProfile);


        loadUserDetails();


        loadRegisteredEvents();


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

            // Already on Profile

        });


        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            SharedPreferences preferences =
                    getSharedPreferences("UserPrefs", MODE_PRIVATE);

            preferences.edit().clear().apply();

            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }


    private void loadUserDetails() {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();


        if (currentUser == null) {

            Toast.makeText(
                    ProfileActivity.this,
                    "User not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String email =
                currentUser.getEmail();


        if (email != null) {

            tvProfileEmail.setText(
                    email
            );
        }
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


                            // No registered events

                            if (queryDocumentSnapshots
                                    .isEmpty()) {

                                TextView noEvents =
                                        new TextView(
                                                ProfileActivity.this
                                        );

                                noEvents.setText(
                                        "You have not registered for any events yet."
                                );

                                noEvents.setTextSize(
                                        16
                                );

                                noEvents.setTextColor(
                                        getResources()
                                                .getColor(
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
                            ) {

                                addRegisteredEventCard(
                                        document
                                );
                            }

                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    ProfileActivity.this,
                                    "Error loading registered events: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                        }
                );
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
                document.getString(
                        "eventName"
                );


        String eventDate =
                document.getString(
                        "eventDate"
                );


        String eventTime =
                document.getString(
                        "eventTime"
                );


        String eventPlace =
                document.getString(
                        "eventPlace"
                );


        String ticketPrice =
                document.getString(
                        "ticketPrice"
                );



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




        registeredEventsContainer
                .addView(eventView);
    }


    @Override
    protected void onResume() {

        super.onResume();

        if (mAuth != null &&
                registeredEventsContainer != null) {

            loadRegisteredEvents();
        }
    }
}