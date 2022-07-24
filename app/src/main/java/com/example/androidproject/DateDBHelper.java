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

public class DateDBHelper extends SQLiteOpenHelper
{
    private static DateDBHelper sqLiteManager;

    private Context context;
    private static final String DATABASE_NAME = "DateDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "DateTable";
    public static final String COLUMN_DATE = "Date";
    public static final String COLUMN_TIME = "Time";

    public DateDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static DateDBHelper instanceOfDatabase(Context context)
    {
        if(sqLiteManager == null)
            sqLiteManager = new DateDBHelper(context);
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
                .append(COLUMN_DATE)
                .append(" TEXT, ")
                .append(COLUMN_TIME)
                .append(" TEXT)");
        sqLiteDatabase.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    public void addItemToDatabase(String date, String time) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_TIME, time);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }


    public void updateItemInDB(String pevName, String date, String time) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);

        // Which row to update, based on the title
        String selection = COLUMN_DATE + " LIKE ?";
        String[] selectionArgs = { pevName };

        int count = sqLiteDatabase.update(
                TABLE_NAME,
                values,
                selection,
                selectionArgs);


    }

    public String getDateFromDB() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String date = "";
        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null)) {
            if(result.getCount() != 0) {
                while (result.moveToNext()) {
                    date = result.getString(0);
                }
            }
        }
        return date;
    }

    public String getTimeFromDB() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String time = "";
        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null)) {
            if(result.getCount() != 0) {
                while (result.moveToNext()) {
                    time = result.getString(1);
                }
            }
        }
        return time;
    }
}

