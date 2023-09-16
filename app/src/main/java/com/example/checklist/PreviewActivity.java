package com.example.checklist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class PreviewActivity extends AppCompatActivity {
    private SharedPreferences pref;
    ImageView imageView;
    Matrix matrix = new Matrix();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        pref = getSharedPreferences("MyPref", MODE_PRIVATE);

        imageView = findViewById(R.id.ivPhoto);
        Bundle arguments = getIntent().getExtras();
        matrix.postRotate(90);
        Bitmap img = BitmapFactory.decodeFile(arguments.getString("img"));
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(img, img.getWidth(), img.getHeight(), true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        imageView.setImageBitmap(rotatedBitmap);
    }

    public void bCancel(View view) {
        goHome();
    }

    public void bScan(View view) {
        post();
        //goHome();
    }

    private void post() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Result result = null;
                String strRes = null;
                try {
                    String _path = Environment.getExternalStorageDirectory() + File.separator + "ImgScan.jpg";
                    // Чтение изображения QR-кода из файла
                    FileInputStream inputStream = new FileInputStream(_path);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Bitmap resize = Bitmap.createScaledBitmap(bitmap, 200,200,false);
                    int[] intArray = new int[resize.getWidth() * resize.getHeight()];
                    resize.getPixels(intArray, 0, resize.getWidth(), 0, 0, resize.getWidth(), resize.getHeight());

                    // Создание объекта BinaryBitmap
                    int width = resize.getWidth();
                    int height = resize.getHeight();
                    RGBLuminanceSource source = new RGBLuminanceSource(width, height, intArray);
                    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
                    System.out.println(binaryBitmap);

                    // Создание объекта QRCodeReader и сканирование QR-кода
                    QRCodeReader reader = new QRCodeReader();
                    result = reader.decode(binaryBitmap);
                    System.out.println(result);

                } catch (IOException | NotFoundException e) {
                    e.printStackTrace();
                } catch (ChecksumException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    e.printStackTrace();
                }
                int amountRes = 0;
                String nameRes = null;
                if (result != null){
                    strRes = result.getText();
                    amountRes = Integer.parseInt(strRes.split("amount=\"")[1].split("\"")[0]);
                    nameRes = strRes.split("name=\"")[1].split("\"")[0];
                }
                var editor = pref.edit();
                var amount = pref.getString("summaryAmount", "0");
                if (amount != null) {
                    editor.putString("summaryAmount", Integer.toString(amountRes + Integer.parseInt(amount))).apply();
                }
                editor.putString("productScanner", nameRes).apply();
                System.out.println(amountRes);
                System.out.println(nameRes);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        goHome();
    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
