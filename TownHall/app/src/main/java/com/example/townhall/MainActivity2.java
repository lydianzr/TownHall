package com.example.townhall;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description);

        // Retrieve data from the intent
        String initiativeID = getIntent().getStringExtra("iniativeID");
        String nama = getIntent().getStringExtra("Nama");
        String description = getIntent().getStringExtra("description");
        String status = getIntent().getStringExtra("Status");
        String category = getIntent().getStringExtra("category");
        String startDate = getIntent().getStringExtra("StartDate");
        String endDate = getIntent().getStringExtra("EndDate");
        int imageResId = getIntent().getIntExtra("Image", -1);

        // Set ImageView
        // Set ImageView
        ImageView imageView = findViewById(R.id.imageView);
        if(imageView != null){
            if (imageResId != -1) {
                imageView.setImageResource(imageResId);
            }
        }

        // Set TextViews

        TextView textView3 = findViewById(R.id.textView3); // Name
        if(textView3 != null){
            textView3.setText(nama);
        }

        TextView textView4 = findViewById(R.id.textView4); // Description
        if(textView4 != null){
            textView4.setText(description);
        }

        TextView textView5 = findViewById(R.id.textView5); // Status
        if(textView5 != null){
            textView5.setText(status);
        }

        TextView textView6 = findViewById(R.id.textView6); // Category
        if(textView6 != null){
            textView6.setText(category);
        }

        TextView textView7 = findViewById(R.id.textView7); // Start Date
        if(textView7 != null){
            textView7.setText("Start date : " + startDate);
        }

        TextView textView8 = findViewById(R.id.textView8); // End Date
        if(textView8 != null){
            textView8.setText("End date : " + endDate);
        }
    }
}