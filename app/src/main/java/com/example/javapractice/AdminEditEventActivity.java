package com.example.javapractice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminEditEventActivity extends AppCompatActivity {

    private ListView eventListView;

    private FirebaseFirestore db;

    private ArrayList<String> eventNames;
    private ArrayList<String> eventIds;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_event);

        eventListView = findViewById(R.id.adminEditEventList);

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

        eventListView.setOnItemClickListener((parent, view, position, id) -> {

            String selectedEventId = eventIds.get(position);

            Intent intent = new Intent(
                    AdminEditEventActivity.this,
                    EditEventActivity.class
            );

            intent.putExtra("eventId", selectedEventId);

            startActivity(intent);
        });
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

                        if (eventName == null) {
                            eventName = "Unnamed Event";
                        }

                        eventNames.add(eventName);
                        eventIds.add(document.getId());
                    }

                    adapter.notifyDataSetChanged();

                    if (eventNames.isEmpty()) {

                        Toast.makeText(
                                AdminEditEventActivity.this,
                                "No events found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AdminEditEventActivity.this,
                            "Error loading events: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db != null && adapter != null) {
            loadEvents();
        }
    }
}