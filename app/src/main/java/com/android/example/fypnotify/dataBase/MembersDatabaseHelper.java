package com.android.example.fypnotify.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MembersDatabaseHelper extends SQLiteOpenHelper {
    //public static final int DATABASE_VERSION = 1;//older versionm
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "UsersDatabase.db";

    public MembersDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //for history table
        String CREATE_MEMBERS_TABLE = "CREATE TABLE " + DatabaseContract.MembersEntry.TABLE_NAME + "("
                + DatabaseContract.MembersEntry.COLOUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.MembersEntry.COLOUMN_NAME + " TEXT NOT NULL DEFAULT 'untitled', "
                + DatabaseContract.MembersEntry.COLOUMN_PHONE_NUMBER + " TEXT NOT NULL,"
                + DatabaseContract.MembersEntry.COLOUMN_USER_TYPE + " TEXT NOT NULL DEFAULT 'untitled');";


        String CREATE_NOTIFICATION_TABLE = "CREATE TABLE " + DatabaseContract.NotificationsEntry.TABLE_NAME + "("
                + DatabaseContract.NotificationsEntry.COLOUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.NotificationsEntry.COLOUMN_TITLE + " TEXT NOT NULL DEFAULT 'untitled', "
                + DatabaseContract.NotificationsEntry.COLOUMN_MESSAGE + " TEXT NOT NULL,"
                + DatabaseContract.NotificationsEntry.COLOUMN_TIME_STAMP + " TEXT NOT NULL DEFAULT 'untitled',"
                + DatabaseContract.NotificationsEntry.COLOUMN_RECIEVERS + " TEXT NOT NULL );";
        sqLiteDatabase.execSQL(CREATE_MEMBERS_TABLE);
        sqLiteDatabase.execSQL(CREATE_NOTIFICATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String ALTER_NOTIFICATION_TABLE = "ALTER TABLE " + DatabaseContract.NotificationsEntry.TABLE_NAME
                + " ADD COLUMN " + DatabaseContract.NotificationsEntry.COLOUMN_URI_LIST + " TEXT DEFAULT NULL;";

        sqLiteDatabase.execSQL(ALTER_NOTIFICATION_TABLE);
        //when we add new table or add new coloum to an existing table or modify schema in any form then we need to use on upgrade mehod for further deatails follow th elink below
        //https://stackoverflow.com/questions/24634116/caused-by-android-database-sqlite-sqliteexception-no-such-table-code-1-andr
    }
}
