package com.fineapp.jsonparse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String NAME = "jsonData";
    public static final String MOVIES = "movies";

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "url";
    public static final String KEY_RATING = "rating";
    public static final String KEY_YEAR = "releaseYear";
    public static final String KEY_GENRE = "genre";

    public SQLite(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + MOVIES + "(" + KEY_ID + " integer primary key," + KEY_TITLE + " text," + KEY_IMAGE + " blob," + KEY_RATING + " text," + KEY_YEAR + " integer," + KEY_GENRE + " text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVers, int newVers) {
        sqLiteDatabase.execSQL("drop table if exists" + MOVIES);
        onCreate(sqLiteDatabase);
    }
}
