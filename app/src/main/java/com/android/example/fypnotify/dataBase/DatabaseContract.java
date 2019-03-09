package com.android.example.fypnotify.dataBase;

import android.provider.BaseColumns;

public class DatabaseContract {
    private DatabaseContract() {
    }

    public static abstract class MembersEntry implements BaseColumns {

        public static final String TABLE_NAME = "UsersTable";

        public static final String COLOUMN_ID = BaseColumns._ID;
        public static final String COLOUMN_NAME = "name";
        public static final String COLOUMN_PHONE_NUMBER = "phone_number";
        public static final String COLOUMN_USER_TYPE = "user_type";
    }

    public static abstract class NotificationsEntry implements BaseColumns {

        public static final String TABLE_NAME = "NotificationsTable";

        public static final String COLOUMN_ID = BaseColumns._ID;
        public static final String COLOUMN_TITLE = "title";
        public static final String COLOUMN_MESSAGE = "message";
        public static final String COLOUMN_TIME_STAMP = "time_stamp";
        public static final String COLOUMN_RECIEVERS = "sent_to";
        public static final String COLOUMN_URI_LIST = "uri_list";
    }
}
