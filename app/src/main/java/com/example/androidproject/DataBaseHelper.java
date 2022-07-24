package com.example.androidproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper
{
    private static DataBaseHelper sqLiteManager;

    private Activity activity;
    private Context context;
    private static final String DATABASE_NAME = "ItemsDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "Items";
    public static final String COLUMN_NAME_TITLE = "ItemName";
    public static final String COLUMN_QUANTITY = "quantity";

    public DataBaseHelper(Activity activity, Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.activity = activity;
    }

    public static DataBaseHelper instanceOfDatabase(Activity activity, Context context)
    {
        if(sqLiteManager == null)
            sqLiteManager = new DataBaseHelper(activity, context);
        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        StringBuilder sql;
        sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append("(")
                .append(COLUMN_NAME_TITLE)
                .append(" TEXT, ")
                .append(COLUMN_QUANTITY)
                .append(" TEXT)");
        sqLiteDatabase.execSQL(sql.toString());

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    public void addItemToDatabase(String name, String quantity) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_TITLE, name);
        contentValues.put(COLUMN_QUANTITY, quantity);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }


    public void updateItemInDB(String pevName, String name, String quantity) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_TITLE, name);
        values.put(COLUMN_QUANTITY, quantity);

        // Which row to update, based on the title
        String selection = COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = { pevName };

        int count = sqLiteDatabase.update(
                TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.e("Query", name);


    }

    public void deleteItemFromDB(String name){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        // Define 'where' part of query.
        String selection = COLUMN_NAME_TITLE + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { name };
        // Issue SQL statement.
        int deletedRows = sqLiteDatabase.delete(TABLE_NAME, selection, selectionArgs);
    }

    public boolean checkIfItemExist(String name){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String Query = "Select * from " + TABLE_NAME + " where " + COLUMN_NAME_TITLE + " = \"" + name + "\"";
        Log.e("Query",Query);

        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            Log.e("TEST","not exist ");
            return false;
        }
        cursor.close();
        Log.e("TEST","already exist ");

        return true;
    }



    public ArrayList<Item> getDataFromDB() {
        ArrayList arrayList = new ArrayList<Item>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null)) {
            if(result.getCount() != 0) {
                while (result.moveToNext()) {
                    String name = result.getString(0);
                    String quantity = result.getString(1);
                    arrayList.add(new Item(name, Integer.valueOf(quantity)));
                }
            }
        }
        return  arrayList;
    }
}

