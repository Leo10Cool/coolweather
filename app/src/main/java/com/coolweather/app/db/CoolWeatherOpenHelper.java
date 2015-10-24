package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by 飞 on 2015/10/23 0023.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    //    Province建表语句
    public static final String CREATE_PROVINCE = "create table Province ("
            + "id integer primary key autoincrement,"
            + "province_name text,"
            + "province_code text )";
    //    City建表语句
    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement,"
            + "city_name text,"
            + "city_code text,"
            + "province_id integer )";
    //    County建表语句
    public static final String CREATE_COUNTY = "create table County ("
            + "id integer primary key autoincrement,"
            + "county_name text,"
            + "county_code text,"
            + "city_id integer )";

    private Context mContext;

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
