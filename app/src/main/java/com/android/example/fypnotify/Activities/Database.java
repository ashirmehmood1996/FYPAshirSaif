package com.android.example.fypnotify.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Groups.db";
    public static final String TABLE_NAME = "GroupsTable";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(" CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT ,Group_Title Text, MEMBER_ID TEXT,TYPE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void deleteData(String title,String memberId,String type){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME,"Group_Title = ? AND MEMBER_ID = ? AND TYPE = ? ",new String[]{title,memberId,type});

    }

    public void deleteAllData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + TABLE_NAME);


    }

    public void deleteGroup(String title,String type){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        if (!type.equals("null"))
            sqLiteDatabase.delete(TABLE_NAME,"Group_Title = ?  AND TYPE = ? ",new String[]{title,type});
        else
            sqLiteDatabase.delete(TABLE_NAME,"Group_Title = ? ",new String[]{title});
    }


    public Boolean insertData(String title,String memberId,String type) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("Group_Title", title);
        contentValues.put("MEMBER_ID", memberId);
        contentValues.put("TYPE",type);


        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData(String query){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        return cursor;
    }
}















