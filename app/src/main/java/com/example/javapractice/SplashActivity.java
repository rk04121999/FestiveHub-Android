package com.example.javapractice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private TextView tvLogo;

    private final String text = "Festive Hub";
    private int index = 0;

    private final Handler handler = new Handler();

    private final Runnable typingRunnable = new Runnable() {
        @Override
        public void run() {

            if (index < text.length()) {

                tvLogo.setText(text.substring(0, index + 1));

                index++;

                handler.postDelayed(this, 300);

            } else {

                handler.postDelayed(() -> {

                    Intent intent = new Intent(
                            SplashActivity.this,
                            MainActivity.class
                    );

                    startActivity(intent);
                    finish();

                }, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        tvLogo = findViewById(R.id.tvLogo);

        handler.postDelayed(typingRunnable, 300);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(typingRunnable);
    }
}