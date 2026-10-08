package com.example.javapractice;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class MyRegistrationActivity extends AppCompatActivity {

    private ImageView registrationQR;
    private TextView tvEventName;
    private TextView tvEventDate;
    private TextView tvEventTime;
    private TextView tvEventPlace;
    private TextView tvRegistrationId;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_registration);

        registrationQR = findViewById(R.id.registrationQR);
        tvEventName = findViewById(R.id.tvEventName);
        tvEventDate = findViewById(R.id.tvEventDate);
        tvEventTime = findViewById(R.id.tvEventTime);
        tvEventPlace = findViewById(R.id.tvEventPlace);
        tvRegistrationId = findViewById(R.id.tvRegistrationId);
        btnBack = findViewById(R.id.btnBack);

        String registrationId =
                getIntent().getStringExtra("registrationId");

        String userId =
                getIntent().getStringExtra("userId");

        String email =
                getIntent().getStringExtra("userEmail");

        String eventId =
                getIntent().getStringExtra("eventId");

        String eventName =
                getIntent().getStringExtra("eventName");

        String eventDate =
                getIntent().getStringExtra("eventDate");

        String eventTime =
                getIntent().getStringExtra("eventTime");

        String eventPlace =
                getIntent().getStringExtra("eventPlace");

        tvEventName.setText(
                eventName != null ? eventName : "Event"
        );

        tvEventDate.setText(
                "📅 " + (eventDate != null ? eventDate : "-")
        );

        tvEventTime.setText(
                "🕐 " + (eventTime != null ? eventTime : "-")
        );

        tvEventPlace.setText(
                "📍 " + (eventPlace != null ? eventPlace : "-")
        );

        tvRegistrationId.setText(
                "Registration ID: " +
                        (registrationId != null
                                ? registrationId
                                : "-")
        );

        String qrData =
                createQRData(
                        registrationId,
                        eventId,
                        eventName,
                        eventDate,
                        eventTime,
                        userId,
                        email
                );

        generateQR(qrData);

        btnBack.setOnClickListener(v -> finish());
    }

    private String createQRData(
            String registrationId,
            String eventId,
            String eventName,
            String eventDate,
            String eventTime,
            String userId,
            String email
    ) {

        return "FESTIVE_HUB\n" +
                "Registration ID: " + registrationId + "\n" +
                "User ID: " + userId + "\n" +
                "Email: " + email + "\n" +
                "Event ID: " + eventId + "\n" +
                "Event: " + eventName + "\n" +
                "Date: " + eventDate + "\n" +
                "Time: " + eventTime;
    }

    private void generateQR(String data) {

        QRCodeWriter writer =
                new QRCodeWriter();

        try {

            BitMatrix matrix =
                    writer.encode(
                            data,
                            BarcodeFormat.QR_CODE,
                            600,
                            600
                    );

            int width = matrix.getWidth();
            int height = matrix.getHeight();

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