package com.example.javapractice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminAttendanceActivity extends AppCompatActivity {

    private LinearLayout attendanceContainer;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance);

        attendanceContainer = findViewById(
                R.id.attendanceContainer
        );

        db = FirebaseFirestore.getInstance();

        loadAttendance();
    }

    private void loadAttendance() {

        db.collection("attendance")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    attendanceContainer.removeAllViews();

                    if (queryDocumentSnapshots.isEmpty()) {

                        TextView emptyText = new TextView(this);

                        emptyText.setText(
                                "No attendance records found"
                        );

                        emptyText.setTextSize(18);
                        emptyText.setPadding(
                                20,
                                40,
                                20,
                                40
                        );

                        attendanceContainer.addView(
                                emptyText
                        );

                        return;
                    }

                    for (DocumentSnapshot document :
                            queryDocumentSnapshots.getDocuments()) {

                        addAttendanceCard(document);
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to load attendance",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void addAttendanceCard(
            DocumentSnapshot document
    ) {

        View view = LayoutInflater.from(this)
                .inflate(
                        R.layout.item_admin_attendance,
                        attendanceContainer,
                        false
                );

        TextView tvEventName =
                view.findViewById(R.id.tvAttendanceEvent);

        TextView tvUserEmail =
                view.findViewById(R.id.tvAttendanceEmail);

        TextView tvUserId =
                view.findViewById(R.id.tvAttendanceUserId);

        TextView tvRegistrationId =
                view.findViewById(R.id.tvAttendanceRegistrationId);

        TextView tvStatus =
                view.findViewById(R.id.tvAttendanceStatus);

        TextView tvScannedAt =
                view.findViewById(R.id.tvAttendanceTime);

        TextView tvMarkedBy =
                view.findViewById(R.id.tvAttendanceMarkedBy);

        String eventId =
                document.getString("eventId");

        String eventName =
                document.getString("eventName");

        String userEmail =
                document.getString("userEmail");

        String userId =
                document.getString("userId");

        String registrationId =
                document.getString("registrationId");

        String status =
                document.getString("status");

        String markedBy =
                document.getString("markedBy");

        tvEventName.setText(
                "Event ID: " +
                        (eventId != null ? eventId : "N/A")
        );

        tvUserEmail.setText(
                "Email: " +
                        (userEmail != null ? userEmail : "N/A")
        );

        tvUserId.setText(
                "User ID: " +
                        (userId != null ? userId : "N/A")
        );

        tvRegistrationId.setText(
                "Registration ID: " +
                        (registrationId != null
                                ? registrationId
                                : "N/A")
        );

        tvStatus.setText(
                "Status: " +
                        (status != null ? status : "N/A")
        );

        tvMarkedBy.setText(
                "Marked By: " +
                        (markedBy != null ? markedBy : "N/A")
        );

        Object scannedAt =
                document.get("scannedAt");

        if (scannedAt instanceof com.google.firebase.Timestamp) {

            com.google.firebase.Timestamp timestamp =
                    (com.google.firebase.Timestamp) scannedAt;

            Date date =
                    timestamp.toDate();

            SimpleDateFormat format =
                    new SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            Locale.getDefault()
                    );

            tvScannedAt.setText(
                    "Scanned At: " +
                            format.format(date)
            );

        } else {

            tvScannedAt.setText(
                    "Scanned At: N/A"
            );
        }

        attendanceContainer.addView(view);
    }
}

