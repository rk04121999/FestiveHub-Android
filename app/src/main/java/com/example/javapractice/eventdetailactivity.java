package com.example.javapractice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class eventdetailactivity extends AppCompatActivity {

    ImageView eventImage;

    TextView eventName;
    TextView eventDate;
    TextView eventTime;
    TextView eventPlace;
    TextView eventAddress;
    TextView ticketPrice;
    TextView tPrice;
    TextView eventDescription;

    ImageButton btnBack;
    Button btnBookEvent;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_eventdetailactivity);



        eventImage = findViewById(R.id.eventImage);

        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        eventTime = findViewById(R.id.eventTime);
        eventPlace = findViewById(R.id.eventPlace);
        eventAddress = findViewById(R.id.eventAddress);

        ticketPrice = findViewById(R.id.ticketPrice);
        tPrice = findViewById(R.id.tPrice);

        eventDescription =
                findViewById(R.id.eventDescription);

       btnBack = findViewById(R.id.btnBack);
        btnBookEvent = findViewById(R.id.btnBookEvent);


        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();



        String eventNameValue =
                getIntent().getStringExtra("eventName");

        String eventDateValue =
                getIntent().getStringExtra("eventDate");

        String eventTimeValue =
                getIntent().getStringExtra("eventTime");

        String eventPlaceValue =
                getIntent().getStringExtra("eventPlace");

        String eventAddressValue =
                getIntent().getStringExtra("eventAddress");

        String ticketPriceValue =
                getIntent().getStringExtra("ticketPrice");

        String eventDescriptionValue =
                getIntent().getStringExtra(
                        "eventDescription"
                );

        int eventImageResource =
                getIntent().getIntExtra(
                        "eventImage",
                        0
                );



        if (eventNameValue != null) {

            eventName.setText(
                    eventNameValue
            );
        }


        if (eventDateValue != null) {

            eventDate.setText(
                    "📅 " + eventDateValue
            );
        }


        if (eventTimeValue != null) {

            eventTime.setText(
                    "🕐 " + eventTimeValue
            );
        }


        if (eventPlaceValue != null) {

            eventPlace.setText(
                    "📍 " + eventPlaceValue
            );
        }


        if (eventAddressValue != null) {

            eventAddress.setText(
                    eventAddressValue
            );
        }


        if (ticketPriceValue != null) {

            tPrice.setText(
                    "₹" + ticketPriceValue
            );
        }


        if (eventDescriptionValue != null) {

            eventDescription.setText(
                    eventDescriptionValue
            );
        }


        if (eventImageResource != 0) {

            eventImage.setImageResource(
                    eventImageResource
            );
        }



        btnBack.setOnClickListener(v -> {

            finish();

        });




        btnBookEvent.setOnClickListener(v -> {

            registerEvent(
                    eventNameValue,
                    eventDateValue,
                    eventTimeValue,
                    eventPlaceValue,
                    eventAddressValue,
                    ticketPriceValue
            );

        });

    }



    private void registerEvent(
            String eventName,
            String eventDate,
            String eventTime,
            String eventPlace,
            String eventAddress,
            String ticketPrice
    ) {



        FirebaseUser currentUser =
                mAuth.getCurrentUser();



        if (currentUser == null) {

            Toast.makeText(
                    eventdetailactivity.this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String userId =
                currentUser.getUid();

        String userEmail =
                currentUser.getEmail();



        if (eventName == null ||
                eventName.isEmpty()) {

            Toast.makeText(
                    eventdetailactivity.this,
                    "Event information missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

//        String userId = currentUser.getUid();
//        String userEmail = currentUser.getEmail();



        btnBookEvent.setEnabled(false);




        Map<String, Object> registration =
                new HashMap<>();


        registration.put(
                "userId",
                userId
        );


        registration.put(
                "userEmail",
                userEmail
        );


        registration.put(
                "eventName",
                eventName
        );


        registration.put(
                "eventDate",
                eventDate
        );


        registration.put(
                "eventTime",
                eventTime
        );


        registration.put(
                "eventPlace",
                eventPlace
        );


        registration.put(
                "eventAddress",
                eventAddress
        );


        registration.put(
                "ticketPrice",
                ticketPrice
        );


        registration.put(
                "registeredAt",
                System.currentTimeMillis()
        );


        db.collection("registrations")
                .add(registration)
                .addOnSuccessListener(
                        documentReference -> {

                            Toast.makeText(
                                    eventdetailactivity.this,
                                    "Event registered successfully",
                                    Toast.LENGTH_SHORT
                            ).show();




                            btnBookEvent.setText(
                                    "Registered"
                            );


                            btnBookEvent.setEnabled(
                                    false
                            );

                        }
                )
                .addOnFailureListener(
                        e -> {

                            btnBookEvent.setEnabled(
                                    true
                            );


                            Toast.makeText(
                                    eventdetailactivity.this,
                                    "Registration failed: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                        }
                );
    }
}