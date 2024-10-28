package com.example.project2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.project2.R;

public class ThankYouActivity extends AppCompatActivity {
    Button backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(ThankYouActivity.this, Home.class));
        });
    }
}