package com.example.javapractice;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class EventsActivity extends AppCompatActivity {

    private LinearLayout navHome;
    private LinearLayout navMySchedule;
    private LinearLayout navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_events);

        navHome = findViewById(R.id.navHome);

        navMySchedule = findViewById(R.id.navMySchedule);

        navProfile = findViewById(R.id.navProfile);

        navHome.setOnClickListener(v -> {

            finish();
        });

        navMySchedule.setOnClickListener(v -> {

        });

        navProfile.setOnClickListener(v -> {

            startActivity(
                    new android.content.Intent(
                            EventsActivity.this,
                            ProfileActivity.class
                    )
            );

            finish();
        });
    }
}