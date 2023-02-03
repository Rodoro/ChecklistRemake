package com.example.checklist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {

    private DatabaseAdapter adapter;
    private long productId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        adapter = new DatabaseAdapter(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            productId = extras.getLong("id");
        }
    }

    public void bSaveProduct(View view) {
        EditText editName = findViewById(R.id.editProductName);
        EditText editQuantity = findViewById(R.id.editProductQuantity);
        String name = editName.getText().toString();
        String quantity = editQuantity.getText().toString();
        Product product = new Product(productId, name, quantity);

        if (editName.getText().toString().equals("")) {
            Toast toast = Toast.makeText(this, "Введите название товара", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            adapter.open();
            adapter.insert(product);
            adapter.close();
            goHome();
        }
    }

    public void bCansel(View view){
        goHome();
    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}