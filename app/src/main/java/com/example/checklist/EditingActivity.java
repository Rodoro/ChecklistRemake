package com.example.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditingActivity extends AppCompatActivity {

    private EditText nameBox, quantityBox;
    private long productId=0;
    private DatabaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing);

        nameBox = findViewById(R.id.editProductName);
        quantityBox = findViewById(R.id.editProductQuantity);
        adapter = new DatabaseAdapter(this);

        Bundle arguments = getIntent().getExtras();
        productId = arguments.getLong("id");

        adapter.open();
        Product product = adapter.getProduct(productId);
        nameBox.setText(product.getName());
        quantityBox.setText(product.getQuantity());
        adapter.close();
    }

    public void bSaveProduct(View view) {
        EditText editName = findViewById(R.id.editProductName);
        EditText editQuantity = findViewById(R.id.editProductQuantity);
        String name = editName.getText().toString();
        String quantity = editQuantity.getText().toString();
        Product product = new Product(productId, name, quantity);

        if (editName.getText().toString().equals("")) {
            Toast toast = Toast.makeText(this, "Вы не ввели название товара", Toast.LENGTH_LONG);
            toast.show();
        } else {
            adapter.open();
            adapter.update(product);
            adapter.close();
            goHome();
        }
    }

    public void bRemoveProduct(View view){
        adapter.open();
        adapter.delete(productId);
        adapter.close();
        goHome();
    }

    public void bCancel(View view){
        goHome();
    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
