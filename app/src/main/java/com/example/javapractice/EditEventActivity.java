package com.example.javapractice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditEventActivity extends AppCompatActivity {

    private EditText etEventName;
    private EditText etEventDate;
    private EditText etEventTime;
    private EditText etEventPlace;
    private EditText etEventAddress;
    private EditText etTicketPrice;
    private EditText etEventImage;
    private EditText etEventDescription;

    private Button btnUpdateEvent;

    private FirebaseFirestore db;

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_event);


        eventId = getIntent().getStringExtra("eventId");

        if (eventId == null || eventId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Event ID not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // Find views
        etEventName =
                findViewById(R.id.etEditEventName);

        etEventDate =
                findViewById(R.id.etEditEventDate);

        etEventTime =
                findViewById(R.id.etEditEventTime);

        etEventPlace =
                findViewById(R.id.etEditEventPlace);

        etEventAddress =
                findViewById(R.id.etEditEventAddress);

        etTicketPrice =
                findViewById(R.id.etEditTicketPrice);

        etEventImage =
                findViewById(R.id.etEditEventImage);

        etEventDescription =
                findViewById(R.id.etEditEventDescription);

        btnUpdateEvent =
                findViewById(R.id.btnUpdateEvent);

        db = FirebaseFirestore.getInstance();


        loadEvent();

        // Update button
        btnUpdateEvent.setOnClickListener(
                v -> updateEvent()
        );
    }

    private void loadEvent() {

        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        Event event =
                                documentSnapshot.toObject(
                                        Event.class
                                );

                        if (event != null) {

                            etEventName.setText(
                                    event.getEventName()
                            );

                            etEventDate.setText(
                                    event.getEventDate()
                            );

                            etEventTime.setText(
                                    event.getEventTime()
                            );

                            etEventPlace.setText(
                                    event.getEventPlace()
                            );

                            etEventAddress.setText(
                                    event.getEventAddress()
                            );

                            etTicketPrice.setText(
                                    event.getTicketPrice()
                            );

                            etEventImage.setText(
                                    event.getEventImage()
                            );

                            etEventDescription.setText(
                                    event.getEventDescription()
                            );
                        }

                    } else {

                        Toast.makeText(
                                EditEventActivity.this,
                                "Event not found",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            EditEventActivity.this,
                            "Error loading event: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void updateEvent() {

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
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Create updated data
        Map<String, Object> event =
                new HashMap<>();

        event.put("eventName", eventName);
        event.put("eventDate", eventDate);
        event.put("eventTime", eventTime);
        event.put("eventPlace", eventPlace);
        event.put("eventAddress", eventAddress);
        event.put("ticketPrice", ticketPrice);
        event.put("eventImage", eventImage);
        event.put("eventDescription", eventDescription);

        // Update existing Firestore document
        db.collection("events")
                .document(eventId)
                .update(event)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            EditEventActivity.this,
                            "Event updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            EditEventActivity.this,
                            "Update failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}