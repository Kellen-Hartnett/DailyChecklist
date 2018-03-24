package com.my.kellen.dailychecklist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DbHelper extends SQLiteOpenHelper{

    DbHelper(Context context){
        super(context, "checklist.db", null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + Contract.Entry.TABLE_NAME + " (" +
                Contract.Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.Entry.COLUMN_NAME + " TEXT NOT NULL, " +
                Contract.Entry.COLUMN_DATE + " TEXT NOT NULL, " +
                Contract.Entry.COLUMN_COMPLETE + " INTEGER NOT NULL, " +
                Contract.Entry.COLUMN_REPEAT + " INTEGER NOT NULL " + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.Entry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
