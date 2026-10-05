package com.example.javapractice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

public class eventdetailactivity extends AppCompatActivity {

    String eventIdValue;
    String eventNameValue;

    ImageView eventImage;
    TextView eventName, eventDate, eventTime, eventPlace, eventAddress;
    TextView ticketPrice, tPrice, eventDescription;

    ImageButton btnBack;
    Button btnBookEvent;
    Button btnScanAttendance;

    ImageView registrationQR;
    TextView registrationStatus;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventdetailactivity);

        eventImage = findViewById(R.id.eventImage);
        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        eventTime = findViewById(R.id.eventTime);
        eventPlace = findViewById(R.id.eventPlace);
        eventAddress = findViewById(R.id.eventAddress);
        ticketPrice = findViewById(R.id.ticketPrice);
        tPrice = findViewById(R.id.tPrice);
        eventDescription = findViewById(R.id.eventDescription);

        btnBack = findViewById(R.id.btnBack);
        btnBookEvent = findViewById(R.id.btnBookEvent);
//        btnScanAttendance = findViewById(R.id.btnScanAttendance);
        registrationQR = findViewById(R.id.registrationQR);
        registrationStatus = findViewById(R.id.registrationStatus);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        eventIdValue = getIntent().getStringExtra("eventId");
        eventNameValue = getIntent().getStringExtra("eventName");
        checkExistingRegistration();

        String eventDateValue =
                getIntent().getStringExtra("eventDate");

        String eventTimeValue =
                getIntent().getStringExtra("eventTime");

        String eventPlaceValue =
                getIntent().getStringExtra("eventPlace");

        String eventAddressValue =
                getIntent().getStringExtra("eventAddress");

        String ticketPriceValue =
                getIntent().getStringExtra("ticketPrice");

        String eventDescriptionValue =
                getIntent().getStringExtra("eventDescription");

        int eventImageResource =
                getIntent().getIntExtra("eventImage", 0);


        if (eventNameValue != null) {
            eventName.setText(eventNameValue);
        }

        if (eventDateValue != null) {
            eventDate.setText("📅 " + eventDateValue);
        }

        if (eventTimeValue != null) {
            eventTime.setText("🕐 " + eventTimeValue);
        }

        if (eventPlaceValue != null) {
            eventPlace.setText("📍 " + eventPlaceValue);
        }

        if (eventAddressValue != null) {
            eventAddress.setText(eventAddressValue);
        }

        if (ticketPriceValue != null) {
            tPrice.setText("₹" + ticketPriceValue);
        }

        if (eventDescriptionValue != null) {
            eventDescription.setText(eventDescriptionValue);
        }

        if (eventImageResource != 0) {
            eventImage.setImageResource(eventImageResource);
        }


        btnBack.setOnClickListener(v -> finish());


        btnBookEvent.setOnClickListener(v -> {

            registerEvent(
                    eventIdValue,
                    eventNameValue,
                    eventDateValue,
                    eventTimeValue,
                    eventPlaceValue,
                    eventAddressValue,
                    ticketPriceValue
            );

        });


//        btnScanAttendance.setOnClickListener(v -> {
//
//            Toast.makeText(
//                    eventdetailactivity.this,
//                    "Opening Scanner",
//                    Toast.LENGTH_SHORT
//            ).show();
//
//            Intent intent = new Intent(
//                    eventdetailactivity.this,
//                    QRScannerActivity.class
//            );
//
//            intent.putExtra("eventId", eventIdValue);
//            intent.putExtra("eventName", eventNameValue);
//
//            startActivity(intent);
//        });
    }

    private void registerEvent(
            String eventId,
            String eventName,
            String eventDate,
            String eventTime,
            String eventPlace,
            String eventAddress,
            String ticketPrice
    ) {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (eventName == null || eventName.isEmpty()) {

            Toast.makeText(
                    this,
                    "Event information missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        btnBookEvent.setEnabled(false);

        String registrationId =
                "REG_" + System.currentTimeMillis();

        Map<String, Object> registration =
                new HashMap<>();

        registration.put(
                "registrationId",
                registrationId
        );

        registration.put(
                "eventId",
                eventId
        );

        registration.put(
                "userId",
                currentUser.getUid()
        );

        registration.put(
                "userEmail",
                currentUser.getEmail()
        );

        registration.put(
                "eventName",
                eventName
        );

        registration.put(
                "eventDate",
                eventDate
        );

        registration.put(
                "eventTime",
                eventTime
        );

        registration.put(
                "eventPlace",
                eventPlace
        );

        registration.put(
                "eventAddress",
                eventAddress
        );

        registration.put(
                "ticketPrice",
                ticketPrice
        );

        registration.put(
                "registeredAt",
                System.currentTimeMillis()
        );

        registration.put(
                "attended",
                false
        );

        db.collection("registrations")
                .document(registrationId)
                .set(registration)

                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Event registered successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    btnBookEvent.setText("Registered");
                    btnBookEvent.setEnabled(false);


                    generateRegistrationQR(
                            registrationId,
                            eventId,
                            eventName,
                            eventDate,
                            eventTime,
                            currentUser.getUid(),
                            currentUser.getEmail()
                    );

                })

                .addOnFailureListener(e -> {

                    btnBookEvent.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Registration failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }

    private void generateRegistrationQR(
            String registrationId,
            String eventId,
            String eventName,
            String eventDate,
            String eventTime,
            String userId,
            String email
    ) {

        String qrData =
                "FESTIVE_HUB\n" +
                        "Registration ID: " + registrationId + "\n" +
                        "User ID: " + userId + "\n" +
                        "Email: " + email + "\n" +
                        "Event ID: " + eventId + "\n" +
                        "Event: " + eventName + "\n" +
                        "Date: " + eventDate + "\n" +
                        "Time: " + eventTime;

        QRCodeWriter writer =
                new QRCodeWriter();

        try {

            BitMatrix bitMatrix =
                    writer.encode(
                            qrData,
                            BarcodeFormat.QR_CODE,
                            500,
                            500
                    );

            int width =
                    bitMatrix.getWidth();

            int height =
                    bitMatrix.getHeight();

            Bitmap bitmap =
                    Bitmap.createBitmap(
                            width,
                            height,
                            Bitmap.Config.RGB_565
                    );

            for (int x = 0; x < width; x++) {

                for (int y = 0; y < height; y++) {

                    bitmap.setPixel(
                            x,
                            y,
                            bitMatrix.get(x, y)
                                    ? android.graphics.Color.BLACK
                                    : android.graphics.Color.WHITE
                    );
                }
            }


            Intent intent =
                    new Intent(
                            eventdetailactivity.this,
                            RegistrationQRActivity.class
                    );

            intent.putExtra(
                    "registrationId",
                    registrationId
            );

            intent.putExtra(
                    "qrData",
                    qrData
            );

            startActivity(intent);


        } catch (WriterException e) {

            Toast.makeText(
                    this,
                    "QR generation failed",
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }



    }

    private void checkExistingRegistration() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null || eventIdValue == null) {
            return;
        }

        db.collection("registrations")
                .whereEqualTo("userId", currentUser.getUid())
                .whereEqualTo("eventId", eventIdValue)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        com.google.firebase.firestore.DocumentSnapshot document =
                                queryDocumentSnapshots.getDocuments().get(0);

                        String registrationId =
                                document.getString("registrationId");

                        String userId =
                                document.getString("userId");

                        String email =
                                document.getString("userEmail");

                        String eventId =
                                document.getString("eventId");

                        String eventName =
                                document.getString("eventName");

                        String eventDate =
                                document.getString("eventDate");

                        String eventTime =
                                document.getString("eventTime");

                        String qrData =
                                "FESTIVE_HUB\n" +
                                        "Registration ID: " + registrationId + "\n" +
                                        "User ID: " + userId + "\n" +
                                        "Email: " + email + "\n" +
                                        "Event ID: " + eventId + "\n" +
                                        "Event: " + eventName + "\n" +
                                        "Date: " + eventDate + "\n" +
                                        "Time: " + eventTime;

                        btnBookEvent.setText("Already Registered");
                        btnBookEvent.setEnabled(false);

                        registrationStatus.setText(
                                "Registered\n\nRegistration ID: " + registrationId
                        );

                        registrationStatus.setVisibility(View.VISIBLE);
                        registrationQR.setVisibility(View.VISIBLE);

                        generateQRInsideEventDetails(qrData);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            this,
                            "Unable to check registration",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void generateQRInsideEventDetails(String data) {

        QRCodeWriter writer = new QRCodeWriter();

        try {

            BitMatrix matrix = writer.encode(
                    data,
                    BarcodeFormat.QR_CODE,
                    600,
                    600
            );

            int width = matrix.getWidth();
            int height = matrix.getHeight();

            Bitmap bitmap = Bitmap.createBitmap(
                    width,
                    height,
                    Bitmap.Config.RGB_565
            );

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {

                    bitmap.setPixel(
                            x,
                            y,
                            matrix.get(x, y)
                                    ? android.graphics.Color.BLACK
                                    : android.graphics.Color.WHITE
                    );
                }
            }

            registrationQR.setImageBitmap(bitmap);

        } catch (WriterException e) {

            Toast.makeText(
                    this,
                    "QR generation failed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


}