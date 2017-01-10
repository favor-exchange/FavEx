package com.favex.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shayan on 09-Jan-17.
 */

public class chatDatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "users";
    public static final String COLUMN_NAME_ID = "ID";
    public static final String COLUMN_NAME_SENDER = "SENDER";
    public static final String COLUMN_NAME_FBID = "FACEBOOKID";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    COLUMN_NAME_SENDER + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_FBID + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String DATABASE_NAME = "FavEx.db";

    public chatDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public boolean insertData(String sender, String facebookId){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_SENDER, sender);
        cv.put(COLUMN_NAME_FBID, facebookId);

        if( db.insert(TABLE_NAME, null, cv) == -1){
            return false;
        }
        return true;
    }

    public Cursor getAllChats(){
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("select * from " + TABLE_NAME, null);
    }
}
