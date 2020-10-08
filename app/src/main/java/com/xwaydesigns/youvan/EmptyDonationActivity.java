package com.xwaydesigns.youvan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class EmptyDonationActivity extends AppCompatActivity {

    private String heading;
    private TextView heading_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_donation);

       heading =  getIntent().getStringExtra("heading");
       heading_text = findViewById(R.id.pending_title_text);
       heading_text.setText(heading);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EmptyDonationActivity.this,MainActivity.class);
        startActivity(intent);
    }
}