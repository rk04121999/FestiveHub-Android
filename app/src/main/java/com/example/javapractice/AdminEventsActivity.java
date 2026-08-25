package com.example.javapractice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AdminEventsActivity extends AppCompatActivity {

    private Button addEventButton;
    private Button editEventButton;
    private Button deleteEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_events);

        addEventButton = findViewById(R.id.addEventButton);
        editEventButton = findViewById(R.id.editEventButton);
        deleteEventButton = findViewById(R.id.deleteEventButton);

        View.OnClickListener openEventManagement = v -> {

            Intent intent = new Intent(
                    AdminEventsActivity.this,
                    EventManagementActivity.class
            );

            intent.putExtra("isAdmin", true);

            startActivity(intent);
        };

        addEventButton.setOnClickListener(openEventManagement);
        editEventButton.setOnClickListener(openEventManagement);
        deleteEventButton.setOnClickListener(openEventManagement);
    }
}