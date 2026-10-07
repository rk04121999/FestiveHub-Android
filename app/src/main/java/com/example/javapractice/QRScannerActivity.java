package com.example.javapractice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.FieldValue;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRScannerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private PreviewView previewView;
    private TextView tvScanMessage;
    private ImageButton btnCloseScanner;

    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private boolean qrProcessed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        previewView = findViewById(R.id.previewView);
        tvScanMessage = findViewById(R.id.tvScanMessage);
        btnCloseScanner = findViewById(R.id.btnCloseScanner);

        cameraExecutor = Executors.newSingleThreadExecutor();

        barcodeScanner = BarcodeScanning.getClient();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnCloseScanner.setOnClickListener(v -> finish());

        checkCameraPermission();
    }

    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {

            startCamera();

        } else {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST
            );
        }
    }

    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {

            try {

                ProcessCameraProvider cameraProvider =
                        cameraProviderFuture.get();

                Preview preview = new Preview.Builder()
                        .build();

                preview.setSurfaceProvider(
                        previewView.getSurfaceProvider()
                );

                CameraSelector cameraSelector =
                        CameraSelector.DEFAULT_BACK_CAMERA;

                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(
                                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                                )
                                .build();

                imageAnalysis.setAnalyzer(
                        cameraExecutor,
                        this::analyzeImage
                );

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );

            } catch (Exception e) {

                Toast.makeText(
                        this,
                        "Unable to start camera",
                        Toast.LENGTH_LONG
                ).show();
            }

        }, ContextCompat.getMainExecutor(this));
    }

    @androidx.camera.core.ExperimentalGetImage
    private void analyzeImage(ImageProxy imageProxy) {

        if (qrProcessed) {
            imageProxy.close();
            return;
        }

        android.media.Image mediaImage = imageProxy.getImage();

        if (mediaImage == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.getImageInfo().getRotationDegrees()
        );

        barcodeScanner.process(image)
                .addOnSuccessListener(barcodes -> {

                    for (Barcode barcode : barcodes) {

                        String qrValue = barcode.getRawValue();

                        if (qrValue != null && !qrValue.trim().isEmpty()) {

                            qrProcessed = true;
                            validateQRCode(qrValue);
                            break;
                        }
                    }

                })
                .addOnFailureListener(e -> {

                    qrProcessed = false;

                })
                .addOnCompleteListener(task -> {
                    imageProxy.close();
                });
    }

    private void validateQRCode(String qrValue) {

        try {

            String[] lines = qrValue.split("\\n");

            String registrationId = null;
            String userId = null;
            String email = null;
            String qrEventId = null;

            for (String line : lines) {

                line = line.trim();

                if (line.startsWith("Registration ID:")) {

                    registrationId =
                            line.substring("Registration ID:".length()).trim();

                } else if (line.startsWith("User ID:")) {

                    userId =
                            line.substring("User ID:".length()).trim();

                } else if (line.startsWith("Email:")) {

                    email =
                            line.substring("Email:".length()).trim();

                } else if (line.startsWith("Event ID:")) {

                    qrEventId =
                            line.substring("Event ID:".length()).trim();
                }
            }

            if (registrationId == null || registrationId.isEmpty()) {
                showInvalidQR();
                return;
            }

            if (userId == null || userId.isEmpty()) {
                showInvalidQR();
                return;
            }

            if (qrEventId == null || qrEventId.isEmpty()) {
                showInvalidQR();
                return;
            }

            markAttendance(
                    registrationId,
                    userId,
                    email,
                    qrEventId
            );

        } catch (Exception e) {

            showInvalidQR();
        }
    }

    private void markAttendance(
            String registrationId,
            String userId,
            String email,
            String qrEventId
    ) {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        if (currentUser == null) {

            qrProcessed = false;

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        tvScanMessage.setText(
                "Checking registration..."
        );

        db.collection("registrations")
                .document(registrationId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        qrProcessed = false;

                        tvScanMessage.setText(
                                "Registration not found"
                        );

                        Toast.makeText(
                                this,
                                "Invalid registration",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    String firestoreEventId =
                            documentSnapshot.getString("eventId");

                    String firestoreUserId =
                            documentSnapshot.getString("userId");

                    String firestoreEmail =
                            documentSnapshot.getString("userEmail");

                    if (firestoreEventId == null ||
                            !firestoreEventId.equals(qrEventId)) {

                        qrProcessed = false;

                        tvScanMessage.setText(
                                "Invalid event QR"
                        );

                        Toast.makeText(
                                this,
                                "QR does not match the registration",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    if (firestoreUserId == null ||
                            !firestoreUserId.equals(userId)) {

                        qrProcessed = false;

                        tvScanMessage.setText(
                                "Invalid attendee QR"
                        );

                        Toast.makeText(
                                this,
                                "Attendee does not match registration",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    if (email != null &&
                            firestoreEmail != null &&
                            !firestoreEmail.equalsIgnoreCase(email)) {

                        qrProcessed = false;

                        tvScanMessage.setText(
                                "Invalid attendee email"
                        );

                        Toast.makeText(
                                this,
                                "Email does not match registration",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    saveAttendance(
                            registrationId,
                            qrEventId,
                            userId,
                            firestoreEmail
                    );

                })
                .addOnFailureListener(e -> {

                    qrProcessed = false;

                    tvScanMessage.setText(
                            "Unable to verify registration"
                    );

                    Toast.makeText(
                            this,
                            "Unable to verify registration",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void saveAttendance(
            String registrationId,
            String eventId,
            String userId,
            String email
    ) {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        if (currentUser == null) {

            qrProcessed = false;
            return;
        }

        tvScanMessage.setText(
                "Checking attendance..."
        );

        db.collection("attendance")
                .document(registrationId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        qrProcessed = false;

                        tvScanMessage.setText(
                                "Attendance already marked"
                        );

                        Toast.makeText(
                                this,
                                "Attendance already marked",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    Map<String, Object> attendance =
                            new HashMap<>();

                    attendance.put(
                            "registrationId",
                            registrationId
                    );

                    attendance.put(
                            "eventId",
                            eventId
                    );

                    attendance.put(
                            "userId",
                            userId
                    );

                    attendance.put(
                            "userEmail",
                            email
                    );

                    attendance.put(
                            "status",
                            "Present"
                    );

                    attendance.put(
                            "scannedAt",
                            FieldValue.serverTimestamp()
                    );

                    attendance.put(
                            "markedBy",
                            currentUser.getUid()
                    );

                    db.collection("attendance")
                            .document(registrationId)
                            .set(attendance)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        this,
                                        "Attendance Marked Successfully",
                                        Toast.LENGTH_LONG
                                ).show();

                                Intent resultIntent =
                                        new Intent();

                                resultIntent.putExtra(
                                        "registrationId",
                                        registrationId
                                );

                                resultIntent.putExtra(
                                        "eventId",
                                        eventId
                                );

                                setResult(
                                        RESULT_OK,
                                        resultIntent
                                );

                                finish();

                            })
                            .addOnFailureListener(e -> {

                                qrProcessed = false;

                                tvScanMessage.setText(
                                        "Failed to mark attendance"
                                );

                                Toast.makeText(
                                        this,
                                        "Failed to mark attendance",
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e -> {

                    qrProcessed = false;

                    tvScanMessage.setText(
                            "Unable to check attendance"
                    );

                    Toast.makeText(
                            this,
                            "Unable to check attendance",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void showInvalidQR() {

        runOnUiThread(() -> {

            qrProcessed = false;

            tvScanMessage.setText(
                    "Invalid QR code"
            );

            Toast.makeText(
                    this,
                    "Invalid QR code",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == CAMERA_PERMISSION_REQUEST) {

            if (grantResults.length > 0 &&
                    grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED) {

                startCamera();

            } else {

                Toast.makeText(
                        this,
                        "Camera permission is required to scan QR codes",
                        Toast.LENGTH_LONG
                ).show();

                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }

        if (barcodeScanner != null) {
            barcodeScanner.close();
        }
    }
}

