package com.example.javapractice;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class RegistrationQRActivity extends AppCompatActivity {

    ImageView qrCode;
    TextView registrationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_qr);

        qrCode = findViewById(R.id.qrCode);
        registrationId = findViewById(R.id.registrationId);

        String registrationIdValue =
                getIntent().getStringExtra("registrationId");

        String qrData =
                getIntent().getStringExtra("qrData");

        if (registrationIdValue == null || qrData == null) {
            Toast.makeText(
                    this,
                    "Registration information not found",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        registrationId.setText(registrationIdValue);

        generateQR(qrData);
    }

    private void generateQR(String data) {

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

            qrCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            Toast.makeText(
                    this,
                    "Unable to generate QR code",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}