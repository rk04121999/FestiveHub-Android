package com.example.javapractice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventManagementActivity extends AppCompatActivity {

    private Button btnOpenAddEvent;
    private LinearLayout managementEventContainer;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_management);

        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        btnOpenAddEvent =
                findViewById(R.id.btnOpenAddEvent);

        managementEventContainer =
                findViewById(R.id.managementEventContainer);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnOpenAddEvent.setOnClickListener(v -> {

            Intent intent = new Intent(
                    EventManagementActivity.this,
                    AddEventActivity.class
            );

            startActivity(intent);
        });

        loadEvents();
    }

    private void loadEvents() {

        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String currentUserId =
                mAuth.getCurrentUser().getUid();

        if (isAdmin) {

            db.collection("events")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        managementEventContainer.removeAllViews();

                        if (queryDocumentSnapshots.isEmpty()) {

                            Toast.makeText(
                                    EventManagementActivity.this,
                                    "No events found",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        for (DocumentSnapshot document :
                                queryDocumentSnapshots.getDocuments()) {

                            Event event =
                                    document.toObject(Event.class);

                            if (event != null) {

                                addManagementEvent(
                                        document.getId(),
                                        event
                                );
                            }
                        }
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                EventManagementActivity.this,
                                "Error loading events: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });

        } else {

            db.collection("events")
                    .whereEqualTo("createdBy", currentUserId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        managementEventContainer.removeAllViews();

                        if (queryDocumentSnapshots.isEmpty()) {

                            Toast.makeText(
                                    EventManagementActivity.this,
                                    "You have not added any events",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        for (DocumentSnapshot document :
                                queryDocumentSnapshots.getDocuments()) {

                            Event event =
                                    document.toObject(Event.class);

                            if (event != null) {

                                addManagementEvent(
                                        document.getId(),
                                        event
                                );
                            }
                        }
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                EventManagementActivity.this,
                                "Error loading your events: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        }
    }

    private void addManagementEvent(
            String documentId,
            Event event) {

        View eventView =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.event_management_item,
                                managementEventContainer,
                                false
                        );

        TextView tvEventName =
                eventView.findViewById(
                        R.id.tvManagementEventName
                );

        TextView tvEventPlace =
                eventView.findViewById(
                        R.id.tvManagementEventPlace
                );

        TextView tvEventDate =
                eventView.findViewById(
                        R.id.tvManagementEventDate
                );

        Button btnEdit =
                eventView.findViewById(
                        R.id.btnEditEvent
                );

        Button btnDelete =
                eventView.findViewById(
                        R.id.btnDeleteEvent
                );

        tvEventName.setText(
                event.getEventName()
        );

        tvEventPlace.setText(
                event.getEventPlace()
        );

        tvEventDate.setText(
                event.getEventDate()
        );

        btnEdit.setOnClickListener(v -> {

            Intent intent = new Intent(
                    EventManagementActivity.this,
                    EditEventActivity.class
            );

            intent.putExtra(
                    "eventId",
                    documentId
            );

            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {

            showDeleteConfirmation(
                    documentId,
                    event.getEventName()
            );
        });

        managementEventContainer.addView(eventView);
    }

    private void showDeleteConfirmation(
            String documentId,
            String eventName) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage(
                        "Are you sure you want to delete \""
                                + eventName
                                + "\"?"
                )
                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .setPositiveButton(
                        "DELETE",
                        (dialog, which) -> {

                            deleteEvent(documentId);
                        }
                )
                .show();
    }

    private void deleteEvent(String documentId) {

        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (isAdmin) {

            db.collection("events")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                EventManagementActivity.this,
                                "Event deleted successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadEvents();
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                EventManagementActivity.this,
                                "Delete failed: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });

            return;
        }

        String currentUserId =
                mAuth.getCurrentUser().getUid();

        db.collection("events")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                EventManagementActivity.this,
                                "Event not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    String eventOwner =
                            documentSnapshot.getString(
                                    "createdBy"
                            );

                    if (eventOwner == null ||
                            !eventOwner.equals(currentUserId)) {

                        Toast.makeText(
                                EventManagementActivity.this,
                                "You can only delete your own events",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    db.collection("events")
                            .document(documentId)
                            .delete()
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        EventManagementActivity.this,
                                        "Event deleted successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadEvents();
                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        EventManagementActivity.this,
                                        "Delete failed: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            EventManagementActivity.this,
                            "Error checking event: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db != null &&
                managementEventContainer != null) {

            loadEvents();
        }
    }
}