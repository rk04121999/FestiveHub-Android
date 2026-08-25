package com.example.javapractice;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminDeleteEventActivity extends AppCompatActivity {

    private ListView eventListView;

    private FirebaseFirestore db;

    private ArrayList<String> eventNames;
    private ArrayList<String> eventIds;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_event);

        eventListView = findViewById(R.id.adminDeleteEventList);

        db = FirebaseFirestore.getInstance();

        eventNames = new ArrayList<>();
        eventIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                eventNames
        );

        eventListView.setAdapter(adapter);

        loadEvents();

        eventListView.setOnItemClickListener(
                (parent, view, position, id) -> {

                    String eventName = eventNames.get(position);
                    String eventId = eventIds.get(position);

                    showDeleteConfirmation(eventName, eventId);
                }
        );
    }

    private void loadEvents() {

        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    eventNames.clear();
                    eventIds.clear();

                    for (QueryDocumentSnapshot document :
                            queryDocumentSnapshots) {

                        String eventName =
                                document.getString("eventName");

                        if (eventName == null ||
                                eventName.isEmpty()) {

                            eventName = "Unnamed Event";
                        }

                        eventNames.add(eventName);
                        eventIds.add(document.getId());
                    }

                    adapter.notifyDataSetChanged();

                    if (eventNames.isEmpty()) {

                        Toast.makeText(
                                AdminDeleteEventActivity.this,
                                "No events found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AdminDeleteEventActivity.this,
                            "Error loading events: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void showDeleteConfirmation(
            String eventName,
            String eventId
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage(
                        "Are you sure you want to delete \""
                                + eventName
                                + "\"?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> deleteEvent(eventId)
                )
                .show();
    }

    private void deleteEvent(String eventId) {

        db.collection("events")
                .document(eventId)
                .delete()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            AdminDeleteEventActivity.this,
                            "Event deleted successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadEvents();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AdminDeleteEventActivity.this,
                            "Delete failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}