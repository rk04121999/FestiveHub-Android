package com.example.javapractice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class VolunteerDashboardActivity extends AppCompatActivity {

    private Button scanAttendanceButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_dashboard);

        scanAttendanceButton = findViewById(R.id.scanAttendanceButton);
        logoutButton = findViewById(R.id.volunteerLogoutButton);

        scanAttendanceButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    VolunteerDashboardActivity.this,
                    QRScannerActivity.class
            );

            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(
                    VolunteerDashboardActivity.this,
                    MainActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }
}
