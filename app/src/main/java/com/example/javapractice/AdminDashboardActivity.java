package com.example.javapractice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button eventsButton;
    private Button studentsButton;
    private Button volunteersButton;
    private Button collegeButton;
    private Button notificationsButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        eventsButton = findViewById(R.id.adminEventsButton);
        studentsButton = findViewById(R.id.adminStudentsButton);
        volunteersButton = findViewById(R.id.adminVolunteersButton);
        collegeButton = findViewById(R.id.adminCollegeButton);
        notificationsButton = findViewById(R.id.adminNotificationsButton);
        logoutButton = findViewById(R.id.adminLogoutButton);

        eventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    AdminEventsActivity.class
            );
            startActivity(intent);
        });

//        studentsButton.setOnClickListener(v -> {
//            Intent intent = new Intent(
//                    AdminDashboardActivity.this,
//                    AdminStudentsActivity.class
//            );
//            startActivity(intent);
//        });

        volunteersButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    AdminVolunteersActivity.class
            );
            startActivity(intent);
        });

        collegeButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    AdminCollegeActivity.class
            );
            startActivity(intent);
        });

        notificationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    AdminNotificationsActivity.class
            );
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
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