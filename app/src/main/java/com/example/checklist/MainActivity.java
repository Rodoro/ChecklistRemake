package com.example.checklist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences pref;

    ArrayList<Product> products = new ArrayList<Product>();
    private DatabaseAdapter adapter;

    private static final int JSON_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new DatabaseAdapter(this);
        pref = getSharedPreferences("MyPref", MODE_PRIVATE);

        var editor = pref.edit();
        var nameProduct = pref.getString("productScanner", "0");
        System.out.println(1);
        System.out.println(nameProduct);
        if(nameProduct != "0"){
            editor.putString("productScanner", "0").apply();
            adapter.open();
            adapter.deleteByName(nameProduct);
            adapter.close();
        }else{
            Toast.makeText(this, "QR код не распознан", Toast.LENGTH_LONG).show();
        }

        setInitialData();
        RecyclerView recyclerView = findViewById(R.id.recycler);
        ProductAdapter.OnProductClickListener productClickListener = new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product, int position) {
                Intent intent = new Intent(getApplicationContext(), EditingActivity.class);
                intent.putExtra("id", product.getId());
                startActivity(intent);
            }
        };
        ProductAdapter adapter = new ProductAdapter(this, products, productClickListener);
        recyclerView.setAdapter(adapter);
        initPermission();
        getAmount();

        Button importButton = findViewById(R.id.button);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, JSON_REQUEST_CODE);
            }
        });
    }

    public void bAddProduct(View view) {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

    public void bScanProduct(View view){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        String _path = Environment.getExternalStorageDirectory()
                + File.separator + "ImgScan.jpg";
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, 100);
    }

    public void bImport(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, JSON_REQUEST_CODE);
    }

    private String readJsonFile(Uri uri) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            String _path = Environment.getExternalStorageDirectory() + File.separator + "ImgScan.jpg";
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra("img", _path);
            startActivity(intent);
        }

        //json
        if (requestCode == JSON_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String jsonString = readJsonFile(uri);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray productsArray = jsonObject.getJSONArray("products");

                adapter.open();
                adapter.deleteAllData();
                adapter.close();
                //удалить все из бд

                for (int i = 0; i < productsArray.length(); i++) {
                    JSONObject productObject = productsArray.getJSONObject(i);
                    int amount = productObject.getInt("amount");
                    String name = productObject.getString("name");

                    System.out.println(amount + name);
                    adapter.open();
                    adapter.insert(new Product(i,name, Integer.toString(amount)));
                    adapter.close();
                    //добавить значение в бд
                }

                //обновить меню
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setInitialData(){
        adapter.open();
        for (Product i: adapter.getProducts()) {
            products.add(new Product (i.getId(), i.getName(), i.getQuantity()));
        }
        adapter.close();
    }

    private void initPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },100);
        }
    }

    public void bClearAmount(View view){
        var editor = pref.edit();
        editor.putString("summaryAmount", "0").apply();
        getAmount();
    }

    private void getAmount(){
        var amount = pref.getString("summaryAmount", "0");
        if (amount != null) {
            TextView editText = findViewById(R.id.summaryAmount);
            editText.setText(amount);
        }
    }
}