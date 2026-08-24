package com.example.javapractice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    private EditText etEventName;
    private EditText etEventDate;
    private EditText etEventTime;
    private EditText etEventPlace;
    private EditText etEventAddress;
    private EditText etTicketPrice;
    private EditText etEventImage;
    private EditText etEventDescription;

    private Button btnAddEvent;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_event);

        etEventName = findViewById(R.id.etEventName);
        etEventDate = findViewById(R.id.etEventDate);
        etEventTime = findViewById(R.id.etEventTime);
        etEventPlace = findViewById(R.id.etEventPlace);
        etEventAddress = findViewById(R.id.etEventAddress);
        etTicketPrice = findViewById(R.id.etTicketPrice);
        etEventImage = findViewById(R.id.etEventImage);
        etEventDescription = findViewById(R.id.etEventDescription);

        btnAddEvent = findViewById(R.id.btnAddEvent);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        btnAddEvent.setOnClickListener(v -> addEvent());
    }

    private void addEvent() {


        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    AddEventActivity.this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String currentUserId =
                mAuth.getCurrentUser().getUid();


        String eventName =
                etEventName.getText()
                        .toString()
                        .trim();

        String eventDate =
                etEventDate.getText()
                        .toString()
                        .trim();

        String eventTime =
                etEventTime.getText()
                        .toString()
                        .trim();

        String eventPlace =
                etEventPlace.getText()
                        .toString()
                        .trim();

        String eventAddress =
                etEventAddress.getText()
                        .toString()
                        .trim();

        String ticketPrice =
                etTicketPrice.getText()
                        .toString()
                        .trim();

        String eventImage =
                etEventImage.getText()
                        .toString()
                        .trim();

        String eventDescription =
                etEventDescription.getText()
                        .toString()
                        .trim();


        if (eventName.isEmpty() ||
                eventDate.isEmpty() ||
                eventTime.isEmpty() ||
                eventPlace.isEmpty() ||
                eventAddress.isEmpty() ||
                ticketPrice.isEmpty() ||
                eventImage.isEmpty() ||
                eventDescription.isEmpty()) {

            Toast.makeText(
                    AddEventActivity.this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Map<String, Object> event =
                new HashMap<>();

        event.put(
                "eventName",
                eventName
        );

        event.put(
                "eventDate",
                eventDate
        );

        event.put(
                "eventTime",
                eventTime
        );

        event.put(
                "eventPlace",
                eventPlace
        );

        event.put(
                "eventAddress",
                eventAddress
        );

        event.put(
                "ticketPrice",
                ticketPrice
        );

        event.put(
                "eventImage",
                eventImage
        );

        event.put(
                "eventDescription",
                eventDescription
        );


        event.put(
                "createdBy",
                currentUserId
        );


        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            AddEventActivity.this,
                            "Event added successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    etEventName.setText("");
                    etEventDate.setText("");
                    etEventTime.setText("");
                    etEventPlace.setText("");
                    etEventAddress.setText("");
                    etTicketPrice.setText("");
                    etEventImage.setText("");
                    etEventDescription.setText("");
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AddEventActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}