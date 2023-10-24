package com.example.checklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public  DatabaseAdapter open() {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private Cursor getAllEntries(){
        String[] columns = new String[] {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_QUANTITY};
        return  database.query(DatabaseHelper.TABLE, columns, null, null, null, null, null);
    }

    public List<Product> getProducts(){
        ArrayList<Product> products = new ArrayList<>();
        Cursor cursor = getAllEntries();
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            String quantity = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY));
            products.add(new Product(id, name, quantity));
        }
        cursor.close();
        return  products;
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE);
    }

    public Product getProduct(long id){
        Product product = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?",DatabaseHelper.TABLE, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});
        if(cursor.moveToFirst()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            String quantity = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY));
            product = new Product(id, name, quantity);
        }
        cursor.close();
        return  product;
    }

    public long insert(Product product){

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, product.getName());
        cv.put(DatabaseHelper.COLUMN_QUANTITY, product.getQuantity());

        return  database.insert(DatabaseHelper.TABLE, null, cv);
    }

    public long delete(long productId){

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(productId)};
        return database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
    }

    public void deleteAllData() {
        String query = "DELETE FROM " + DatabaseHelper.TABLE;
        database.execSQL(query);
    }

    public long deleteByName(String productName){

        String whereClause = "name = ?";
        String[] whereArgs = new String[]{String.valueOf(productName)};
        return database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
    }

    public long update(Product product){

        String whereClause = DatabaseHelper.COLUMN_ID + "=" + product.getId();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, product.getName());
        cv.put(DatabaseHelper.COLUMN_QUANTITY, product.getQuantity());
        return database.update(DatabaseHelper.TABLE, cv, whereClause, null);
    }
}
