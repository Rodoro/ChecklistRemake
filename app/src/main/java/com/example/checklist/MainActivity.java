package com.example.checklist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Product> products = new ArrayList<Product>();
    private DatabaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new DatabaseAdapter(this);

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

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },100);
        }
    }

    public void bAddProduct(View view) {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

    public void bScanProduct(View view){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        String _path = Environment.getExternalStorageDirectory()
                + File.separator + "TakenFromCamera.jpg";
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {

            String _path = Environment.getExternalStorageDirectory() +                File.separator + "TakenFromCamera.jpg";
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra("img", _path);
            startActivity(intent);
        }
    }

    private void setInitialData(){
        adapter.open();
        for (Product i: adapter.getProducts()) {
            products.add(new Product (i.getId(), i.getName(), i.getQuantity()));
        }
        adapter.close();
    }
}