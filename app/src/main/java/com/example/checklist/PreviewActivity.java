package com.example.checklist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class PreviewActivity extends AppCompatActivity {
    ImageView imageView;

    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        imageView = findViewById(R.id.ivPhoto);
        Bundle arguments = getIntent().getExtras();

        imageView.setImageBitmap(BitmapFactory.decodeFile(arguments.getString("img")));
    }
}
