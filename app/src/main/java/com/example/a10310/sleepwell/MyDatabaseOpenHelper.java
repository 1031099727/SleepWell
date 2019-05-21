package com.example.a10310.sleepwell;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseOpenHelper extends SQLiteOpenHelper {

    private Context mContext;
    public static final String CREATE_WEIGHT = "create table sleep (gotobeddate integer primary key,getupdate integer,deep integer,tag integer)";

    public MyDatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WEIGHT);
        db.execSQL("insert into sleep (gotobeddate, getupdate, deep, tag) values(?, ?, ?, ?)",new String[] { "1545057000000", "1545093000000", "9100000", "2" }); //12-17
//        db.execSQL("insert into sleep (gotobeddate, getupdate, deep, tag) values(?, ?, ?, ?)",new String[] { "1545143400000", "1545179400000", "8600000", "2" });               //12-18
//        db.execSQL("insert into sleep (gotobeddate, getupdate, deep, tag) values(?, ?, ?, ?)",new String[] { "1543155800000", "1543191800000", "8800000", "2" });
//        db.execSQL("insert into sleep (gotobeddate, getupdate, deep, tag) values(?, ?, ?, ?)",new String[] { "1543242200000", "1543278200000", "8400000", "2" });
//        db.execSQL("insert into sleep (gotobeddate, getupdate, deep, tag) values(?, ?, ?, ?)",new String[] { "1543328600000", "1543364600000", "8600000", "2" });
//        db.execSQL("insert into sleep (gotobeddate, getupdate, deep, tag) values(?, ?, ?, ?)",new String[] { "1543415400000", "1543451400000", "8800000", "2" });
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
