package com.example.javapractice;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class RegisteredUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerRegisteredUsers;
    private TextView tvEventName;
    private ImageButton btnBack;

    private FirebaseFirestore db;

    private final List<RegisteredUser> registeredUserList = new ArrayList<>();
    private RegisteredUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registered_users);

        recyclerRegisteredUsers =
                findViewById(R.id.recyclerRegisteredUsers);

        tvEventName =
                findViewById(R.id.tvEventName);

        btnBack =
                findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();

        String eventId =
                getIntent().getStringExtra("eventId");

        String eventName =
                getIntent().getStringExtra("eventName");

        if (eventName != null) {
            tvEventName.setText(eventName);
        }

        recyclerRegisteredUsers.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new RegisteredUserAdapter(
                this,
                registeredUserList
        );

        recyclerRegisteredUsers.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        if (eventId == null || eventId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Event ID missing",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        loadRegisteredUsers(eventId);
    }

    private void loadRegisteredUsers(String eventId) {

        db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .orderBy(
                        "registeredAt",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    registeredUserList.clear();

                    for (var document :
                            queryDocumentSnapshots.getDocuments()) {

                        RegisteredUser user =
                                document.toObject(
                                        RegisteredUser.class
                                );

                        if (user != null) {
                            registeredUserList.add(user);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (registeredUserList.isEmpty()) {

                        Toast.makeText(
                                this,
                                "No users registered for this event",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Error loading registered users: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}