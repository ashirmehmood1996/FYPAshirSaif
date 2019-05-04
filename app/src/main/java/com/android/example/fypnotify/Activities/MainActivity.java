package com.android.example.fypnotify.Activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.example.fypnotify.Adapters.NotificationsHistoryAdapter;
import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.Models.NotificationModel;
import com.android.example.fypnotify.R;
import com.android.example.fypnotify.Utils.SharedPreferenceUtility;
import com.android.example.fypnotify.dataBase.DatabaseContract;
import com.android.example.fypnotify.dataBase.MembersDatabaseHelper;
import com.android.example.fypnotify.interfaces.NotificationItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;

import static com.android.example.fypnotify.dataBase.DatabaseContract.NotificationsEntry.COLOUMN_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NotificationItemClickListener {
    private RecyclerView recyclerView;
    private FloatingActionButton addNewConversationButton;
    private NotificationsHistoryAdapter notificationsHistoryAdapter;
    private ArrayList<NotificationModel> notificationArrayList;
    private final int USER_FORM_RESULT = 111;



    private RelativeLayout mainContainerRelativeLayout;
    private LinearLayout emptyViewLineaLayout;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("History");
        initializeFields();
    }

    private void createProgressBar() {
        mProgressBar = //findViewById(R.id.pb_settings);
                new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainContainerRelativeLayout.addView(mProgressBar, params);
        mProgressBar.setVisibility(View.GONE);
    }

    private void showProgressBar(boolean shouldShow) {
        if (shouldShow) {
            mProgressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void initializeFields() {

        emptyViewLineaLayout = findViewById(R.id.ll_main_empty_view);

        recyclerView = findViewById(R.id.rv_main_recycler_view);
        addNewConversationButton = findViewById(R.id.fab_main_add_Notification);
        mainContainerRelativeLayout = findViewById(R.id.rl_conversation_list_conatiner);
        createProgressBar();

        //adding listeners
        addNewConversationButton.setOnClickListener(this);
        addNewConversationButton.setOnTouchListener(new View.OnTouchListener() {
            //todo understand whats going on in this code the code is taken from stack over flow using the followinf link
            //https://stackoverflow.com/a/46373935/6039129

            private static final float CLICK_DRAG_TOLERANCE = 150;
            // private float dY;
            private float dX;
            // private float downRawY;
            private float downRawX;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {

                    downRawX = motionEvent.getRawX();
                    //       downRawY = motionEvent.getRawY();
                    dX = view.getX() - downRawX;
                    // dY = view.getY() - downRawY;


                    if (Math.abs(dX) < CLICK_DRAG_TOLERANCE/* && Math.abs(upDY) < CLICK_DRAG_TOLERANCE*/) { // A click
                        return false;//perform click
                    } else { // A drag
                        return true; // Consumed
                    }

                } else if (action == MotionEvent.ACTION_MOVE) {

                    int viewWidth = view.getWidth();
                    //int viewHeight = view.getHeight();

                    View viewParent = (View) view.getParent();
                    int parentWidth = viewParent.getWidth();
                    int parentHeight = viewParent.getHeight();

                    float newX = motionEvent.getRawX() + dX;
                    newX = Math.max(layoutParams.leftMargin, newX); // Don't allow the FAB past the left hand side of the parent
                    newX = Math.min(parentWidth - viewWidth - layoutParams.rightMargin, newX); // Don't allow the FAB past the right hand side of the parent

//                    float newY = motionEvent.getRawY() + dY;
//                    newY = Math.max(layoutParams.topMargin, newY); // Don't allow the FAB past the top of the parent
//                    newY = Math.min(parentHeight - viewHeight - layoutParams.bottomMargin, newY); // Don't allow the FAB past the bottom of the parent

                    view.animate()
                            .x(newX)
                            //      .y(newY)
                            .setDuration(0)
                            .start();

                    return true; // Consumed

                } else if (action == MotionEvent.ACTION_UP) {

                    float upRawX = motionEvent.getRawX();
                    //  float upRawY = motionEvent.getRawY();

                    float upDX = upRawX - downRawX;
                    //     float upDY = upRawY - downRawY;
                    float as = Math.abs(upDX);
                    if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE/* && Math.abs(upDY) < CLICK_DRAG_TOLERANCE*/) { // A click
                        return false;//perform click
                    } else { // A drag
                        return true; // Consumed
                    }

                } /*else {

                    return onTouchEvent(motionEvent);

                }*/
                return false;

            }
//            float dX;
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        dX = view.getX() - motionEvent.getRawX();
//
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        view.animate()
//                                .x(motionEvent.getRawX() + dX - (view.getWidth()))
//                                .setDuration(0)
//                                .start();
//                        break;
//                    default:
//                        return false;
//                }
//                play a little and use the stack over flow answer
//                return true;
//            }
        });

        notificationArrayList = new ArrayList<>();
        notificationsHistoryAdapter = new NotificationsHistoryAdapter(notificationArrayList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notificationsHistoryAdapter);
    }


    @Override
    protected void onStart() {
        loadNotifications();
        super.onStart();
    }


    private void loadNotifications() {
        String type = null, sortBy = null, time = null;
        if (SharedPreferenceUtility.hasValue(this, "sortBy")) {
            sortBy = SharedPreferenceUtility.getValue(this, "sortBy");
        }
        if (SharedPreferenceUtility.hasValue(this, "time")) {
            time = SharedPreferenceUtility.getValue(this, "time");
        }
        if (SharedPreferenceUtility.hasValue(this, "type")) {
            type = SharedPreferenceUtility.getValue(this, "type");
        }
        applyFilterAndReloadNotifications(sortBy, time, type);

//        /*notificationArrayList.clear();
//        MembersDatabaseHelper membersDatabaseHelper = new MembersDatabaseHelper(this);
//        membersDatabaseHelper.getWritableDatabase();
//        SQLiteDatabase database = membersDatabaseHelper.getReadableDatabase();
//        String[] projection = {DatabaseContract.NotificationsEntry.COLOUMN_ID,
//                DatabaseContract.NotificationsEntry.COLOUMN_TITLE,//array of coloumns that I want to retrieve from the tabe
//                DatabaseContract.NotificationsEntry.COLOUMN_MESSAGE,
//                DatabaseContract.NotificationsEntry.COLOUMN_TIME_STAMP,
//                DatabaseContract.NotificationsEntry.COLOUMN_URI_LIST,
//                DatabaseContract.NotificationsEntry.COLOUMN_TYPE,
//                DatabaseContract.NotificationsEntry.COLOUMN_RECIEVERS};
//
//        Cursor resultCursor = database.query(DatabaseContract.NotificationsEntry.TABLE_NAME,
//                projection, null, null, null, null, null);
//        int coloumnTitleIndex = resultCursor.getColumnIndex(DatabaseContract.NotificationsEntry.COLOUMN_TITLE);
//        int coloumnMessageIndex = resultCursor.getColumnIndex(DatabaseContract.NotificationsEntry.COLOUMN_MESSAGE);
//        int coloumnTimeStampIndex = resultCursor.getColumnIndex(DatabaseContract.NotificationsEntry.COLOUMN_TIME_STAMP);
//        int coloumnrecieversIndex = resultCursor.getColumnIndex(DatabaseContract.NotificationsEntry.COLOUMN_RECIEVERS);
//        int coloumnUrisIndex = resultCursor.getColumnIndex(DatabaseContract.NotificationsEntry.COLOUMN_URI_LIST);
//        int coloumnTypeIndex = resultCursor.getColumnIndex(DatabaseContract.NotificationsEntry.COLOUMN_TYPE);
//        // TODO: 12/18/2018 set the limit to the text entered because i think here is a default limit to wtite in asingle field
//        int coloumnIDIndex = resultCursor.getColumnIndex(DatabaseContract.NotificationsEntry.COLOUMN_ID);
//
//        while (resultCursor.moveToNext()) {
//            int memberID = resultCursor.getInt(coloumnIDIndex);
//            String name = resultCursor.getString(coloumnTitleIndex);
//            String message = resultCursor.getString(coloumnMessageIndex);
//            String timeStamp = resultCursor.getString(coloumnTimeStampIndex);
//            String recievers = resultCursor.getString(coloumnrecieversIndex);
//            String uriCVS = resultCursor.getString(coloumnUrisIndex);
//            String type = resultCursor.getString(coloumnTypeIndex);
//
//            notificationArrayList.add(0, new NotificationModel(memberID, name, message, timeStamp, recievers, type, uriCVS));
//        }
//        if (notificationArrayList.isEmpty())
//            emptyViewLineaLayout.setVisibility(View.VISIBLE);
//        else
//            emptyViewLineaLayout.setVisibility(View.GONE);
//        notificationsHistoryAdapter.notifyDataSetChanged();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == USER_FORM_RESULT) {
            if (resultCode == RESULT_OK) {//means activity was not interrupted
                MemberModel member = (MemberModel) data.getSerializableExtra("member");
                addUserToDataBase(member);
                Toast.makeText(this, "member added to database successfully", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "member was not added", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //get referencees of bottom sheet and supply date on  demnad and also i mrove the UI

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_main_add_Notification:
                showProgressBar(true);
                startActivity(new Intent(MainActivity.this, CreateNotificationActivity.class));
                break;
        }
    }

    //checking git online
    private synchronized void addUserToDataBase(MemberModel member) {
        MembersDatabaseHelper historyDataBaseHelper = new MembersDatabaseHelper(this);
        SQLiteDatabase dbWrite = historyDataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.MembersEntry.COLOUMN_NAME, member.getName());
        contentValues.put(DatabaseContract.MembersEntry.COLOUMN_PHONE_NUMBER, member.getPhoneNumber());
        contentValues.put(DatabaseContract.MembersEntry.COLOUMN_USER_TYPE, member.getMemberType());
        long id = dbWrite.insert(DatabaseContract.MembersEntry.TABLE_NAME,
                null,
                contentValues);
    }

    @Override
    protected void onStop() {
        showProgressBar(false);
        super.onStop();
    }

    //// TODO: 3/8/2019 optimize search functionality and avoid redundant lists formation when no query is in processing
// TODO: 3/8/2019 use a standart way of doing this filtering of results
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.nav_main_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();// etrach the searchview from menu item // search view must be casted as anroid widget v7
        searchView.setQueryHint("search here");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                if (!searchView.isIconified()) {
//                    searchView.setIconified(true);
//                }
//                searchMenuItem.collapseActionView();// in case if these was a submit option it is to collasp the searchview


                return true; // // TODO: 3/8/2019 deal here if needed
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<NotificationModel> filteredArrayList = getFilteredNotificationsArrayList(notificationArrayList, newText);

                notificationsHistoryAdapter.setFilter(filteredArrayList, newText);
                if (filteredArrayList.isEmpty()) {
                    // TODO: 3/9/2019 deal with this later
                    //show no reslts view
                } else {
                    //hide the no results view
                }
                notificationsHistoryAdapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private ArrayList<NotificationModel> getFilteredNotificationsArrayList(ArrayList<NotificationModel> notificationArrayList, String queryText) {
        ArrayList<NotificationModel> filteredArrayList = new ArrayList<>();
        queryText = queryText.toLowerCase();
        for (NotificationModel currentNotification : notificationArrayList) {
            String title = currentNotification.getTitle().toLowerCase();
            if (title.contains(queryText)) {
                filteredArrayList.add(currentNotification);
            }
        }
        return filteredArrayList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav__contacts:
                startActivity(new Intent(this, FragmentsActivity.class));
                break;
            case R.id.nav_main_filter_notifications:
                showfilterDialogue();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onNotificationItemClick(int position, NotificationModel notificationModel) {
        Intent intent = new Intent(this, NotifictionDetailActivity.class);
        intent.putExtra("notification", notificationModel);
        startActivity(intent);

    }

    @Override
    public void onNotificationItemLongClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("delete..");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeHistoryItemFromDataBase(position);
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).create().show();
    }

    private void removeHistoryItemFromDataBase(int position) {
// TO DO: 10/3/2018 in new Update  add an undo option and for which do not delete the instance at one ratehr use timer looper or any thing necessary

        MembersDatabaseHelper membersDatabaseHelper = new MembersDatabaseHelper(this);
        SQLiteDatabase db = membersDatabaseHelper.getReadableDatabase();
        String whereClause = COLOUMN_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(notificationArrayList.get(position).getMemberID())};
        int deleteResult = db.delete(DatabaseContract.NotificationsEntry.TABLE_NAME, whereClause, whereArgs);
        //Toast.makeText(historyActivity, "result code is  = " + deleteResult, Toast.LENGTH_SHORT).show();
        notificationArrayList.remove(position);
        notificationsHistoryAdapter.notifyItemRemoved(position);

        notificationsHistoryAdapter.notifyItemRangeChanged(position, notificationArrayList.size());//NOTE to notify  the adapter that its time to update the item count
        if (notificationArrayList.isEmpty())
            emptyViewLineaLayout.setVisibility(View.VISIBLE);


    }



    //filter related all code it also requires refinemnet , cleaning and testing

    private void showfilterDialogue() {

        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.filter_history_dialogue_layout, null);//inflating a prebuilt xml file
        RadioGroup sortByRadioGroup = linearLayout.findViewById(R.id.rg_filter_dialogue_sort_by);
        RadioButton timeRadioButton = linearLayout.findViewById(R.id.rb_filter_dialogue_sort_by_time);
        RadioButton titleRadioButton = linearLayout.findViewById(R.id.rb_filter_dialogue_sort_by_title);
        // RadioButton typeRadioButton = linearLayout.findViewById(R.id.rb_filter_dialogue_sort_by_type);
        RadioGroup timeRadioGroup = linearLayout.findViewById(R.id.rg_filter_dialogue_time);
        RadioButton last30RadioButton = linearLayout.findViewById(R.id.rb_filter_dialogue_time_last_30);
        RadioButton thisMonthRadioButton = linearLayout.findViewById(R.id.rb_filter_dialogue_time_this_month);
        RadioButton thisYearRadioButton = linearLayout.findViewById(R.id.rb_filter_dialogue_time_this_year);
        RadioButton allTimeRadioButton = linearLayout.findViewById(R.id.rb_filter_dialogue_time_all);

        RadioGroup typeRadioGroup = linearLayout.findViewById(R.id.rg_filter_dialogue_type);
        RadioButton allTypeRadioButton = linearLayout.findViewById(R.id.rb_filter_dialogue_type_all);
        RadioButton multimediaOnlyRadioButton = linearLayout.findViewById(R.id.rb_filter_dialogue_type_mutimedia_only);
        RadioButton textOnlyRadioButton = linearLayout.findViewById(R.id.rb_filter_dialogue_type_text_only);

        if (SharedPreferenceUtility.hasValue(this, "sortBy")) {
            String sortBy = SharedPreferenceUtility.getValue(this, "sortBy");
            switch (sortBy) {
                case DatabaseContract.NotificationsEntry.COLOUMN_TITLE:
                    titleRadioButton.setChecked(true);
                    break;
                case "time":
                    timeRadioButton.setChecked(true);
                    break; //dont have to use this case as it is a default case lates test to be further sure of any conflicts
//                case "type":
//                    break;
                default://it is asumed that the code execution will never come to this block unless a wrong value is set to the key
                    timeRadioButton.setChecked(true);
                    sortBy = DatabaseContract.NotificationsEntry.COLOUMN_TIME_STAMP;
                    break;
            }
        } else {

            timeRadioButton.setChecked(true);
        }
        if (SharedPreferenceUtility.hasValue(this, "time")) {
            String time = SharedPreferenceUtility.getValue(this, "time");
            switch (time) {
                case "last30Time": //dont have to use this case as it is a default case lates test to be further sure of any conflicts
                    last30RadioButton.setChecked(true);
                    break;
                case "thisMonthTime":
                    thisMonthRadioButton.setChecked(true);
                    break;
                case "thisYearTime":
                    thisYearRadioButton.setChecked(true);
                    break;
                case "allTime":
                    allTimeRadioButton.setChecked(true);
                    break;
                default:
                    last30RadioButton.setChecked(true);//it is asumed that the code execution will never come to this block unless a wrong value is set to the key
                    break;
            }
        } else {
            last30RadioButton.setChecked(true);
        }
        if (SharedPreferenceUtility.hasValue(this, "type")) {
            String type = SharedPreferenceUtility.getValue(this, "type");

            switch (type) {
                case "all": //dont have to use this case as it is a default case lates test to be further sure of any conflicts
                    allTypeRadioButton.setChecked(true);
                    break;
                case "multimedia":
                    multimediaOnlyRadioButton.setChecked(true);
                    break;
                case "text":
                    textOnlyRadioButton.setChecked(true);
                    break;
                default://it is asumed that the code execution will never come to this block unless a wrong value is set to the key
                    allTypeRadioButton.setChecked(true);
                    break;
            }
        } else {
            allTypeRadioButton.setChecked(true);
        }

        // TODO: 4/27/2019 set one field in the group by default which will be fetched from sharedpreference
        // TODO: 4/27/2019  add this linear layout in a scroll view to support smaller screens

        new AlertDialog.Builder(this)
                .setView(linearLayout) //view is set here
                .setPositiveButton("apply Filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sortBy;
                        String time;
                        String type;


                        //sortby related
                        int sortId = sortByRadioGroup.getCheckedRadioButtonId();
                        switch (sortId) {
                            case R.id.rb_filter_dialogue_sort_by_time:
                                sortBy = DatabaseContract.NotificationsEntry.COLOUMN_TIME_STAMP;
                                break;
                            case R.id.rb_filter_dialogue_sort_by_title:
                                sortBy = DatabaseContract.NotificationsEntry.COLOUMN_TITLE;
                                break;
                            default:
                                sortBy = DatabaseContract.NotificationsEntry.COLOUMN_TIME_STAMP;
                                break;

                        }
                        SharedPreferenceUtility.storeValue(MainActivity.this, "sortBy", sortBy);


                        int timeId = timeRadioGroup.getCheckedRadioButtonId();
                        switch (timeId) {
                            case R.id.rb_filter_dialogue_time_last_30:
                                time = "last30Time";
                                break;
                            case R.id.rb_filter_dialogue_time_this_month:
                                time = "thisMonthTime";
                                break;
                            case R.id.rb_filter_dialogue_time_this_year:
                                time = "thisYearTime";
                                break;
                            case R.id.rb_filter_dialogue_time_all:
                                time = "allTime";
                                break;
                            default:
                                time = "last30Time";
                                break;

                        }
                        SharedPreferenceUtility.storeValue(MainActivity.this, "time", time);

                        int typeId = typeRadioGroup.getCheckedRadioButtonId();
                        switch (typeId) {
                            case R.id.rb_filter_dialogue_type_all:
                                type = "all";
                                break;
                            case R.id.rb_filter_dialogue_type_text_only:
                                type = "text";
                                break;
                            case R.id.rb_filter_dialogue_type_mutimedia_only:
                                type = "multimedia";
                                break;
                            default:
                                type = "all";
                                break;

                        }
                        SharedPreferenceUtility.storeValue(MainActivity.this, "type", type);
                        applyFilterAndReloadNotifications(sortBy, time, type);

                    }
                }).setNegativeButton("go back", null).show();
    }

    /**
     * @param sortBy the sring should be exact coloum name
     * @param time   string definations
     * @param type   string definations
     */
    private void applyFilterAndReloadNotifications(String sortBy, String time, String type) {
        //order by related
        String orderBy = null; //// TODO: 4/27/2019  later we wont be in need of this if else condition if we donot add the result at index 0 in arraylist
        if (sortBy != null) {
            if (sortBy.equals(DatabaseContract.NotificationsEntry.COLOUMN_TIME_STAMP)) {
                orderBy = sortBy + " ASC";
            } else if (sortBy.equals(DatabaseContract.NotificationsEntry.COLOUMN_TITLE)) {
                orderBy = sortBy + " DESC";
            }
        }
        //time base record related
        String selecion = DatabaseContract.NotificationsEntry.COLOUMN_TIME_STAMP + " >=?";

        String[] selectionArgs = null;
        if (time != null) {
            selectionArgs = new String[1];
            long timeforCondition = getRequiredTimeInMilliSeconds(time);
            if (timeforCondition != 0) {
                selectionArgs[0] = String.valueOf(timeforCondition);
            } else {
                selecion = null;
                selectionArgs = null;
            }
        }
        //type relted
        if (type != null && (type.equals("text") || type.equals("multimedia"))) {
            if (selecion != null && selectionArgs != null) {
                selecion = selecion + " AND " + DatabaseContract.NotificationsEntry.COLOUMN_TYPE + " =?";
                String tempValOne = selectionArgs[0];
                selectionArgs = new String[2];
                selectionArgs[0] = tempValOne;
                selectionArgs[1] = type;
            } else {
                selectionArgs = new String[1];
                selectionArgs[0] = type;
            }
        }


        notificationArrayList.clear();
        MembersDatabaseHelper membersDatabaseHelper = new MembersDatabaseHelper(this);
        membersDatabaseHelper.getWritableDatabase();
        SQLiteDatabase database = membersDatabaseHelper.getReadableDatabase();
        String[] projection = {DatabaseContract.NotificationsEntry.COLOUMN_ID,
                DatabaseContract.NotificationsEntry.COLOUMN_TITLE,//array of coloumns that I want to retrieve from the tabe
                DatabaseContract.NotificationsEntry.COLOUMN_MESSAGE,
                DatabaseContract.NotificationsEntry.COLOUMN_TIME_STAMP,
                DatabaseContract.NotificationsEntry.COLOUMN_URI_LIST,
                DatabaseContract.NotificationsEntry.COLOUMN_RECIEVERS};

        Cursor resultCursor = database.query(DatabaseContract.NotificationsEntry.TABLE_NAME,
                projection, selecion, selectionArgs, null, null, orderBy);
        int coloumnTitleIndex = resultCursor.getColumnIndex(DatabaseContract.NotificationsEntry.COLOUMN_TITLE);
        int coloumnMessageIndex = resultCursor.getColumnIndex(DatabaseContract.NotificationsEntry.COLOUMN_MESSAGE);
        int coloumnTimeStampIndex = resultCursor.getColumnIndex(DatabaseContract.NotificationsEntry.COLOUMN_TIME_STAMP);
        int coloumnrecieversIndex = resultCursor.getColumnIndex(DatabaseContract.NotificationsEntry.COLOUMN_RECIEVERS);
        int coloumnUrisIndex = resultCursor.getColumnIndex(DatabaseContract.NotificationsEntry.COLOUMN_URI_LIST);
        // TODO: 12/18/2018 set the limit to the text entered because i think here is a default limit to wtite in asingle field
        int coloumnIDIndex = resultCursor.getColumnIndex(DatabaseContract.NotificationsEntry.COLOUMN_ID);

        while (resultCursor.moveToNext()) {
            int memberID = resultCursor.getInt(coloumnIDIndex);
            String name = resultCursor.getString(coloumnTitleIndex);
            String message = resultCursor.getString(coloumnMessageIndex);
            String timeStamp = resultCursor.getString(coloumnTimeStampIndex);
            String recievers = resultCursor.getString(coloumnrecieversIndex);
            String uriCVS = resultCursor.getString(coloumnUrisIndex);

            notificationArrayList.add(0, new NotificationModel(memberID, name, message, timeStamp, recievers, type, uriCVS));
        }
        if (notificationArrayList.isEmpty())
            emptyViewLineaLayout.setVisibility(View.VISIBLE);
        else
            emptyViewLineaLayout.setVisibility(View.GONE);
        notificationsHistoryAdapter.notifyDataSetChanged();

    }

    /**
     * // TODO: 4/27/2019 perform the following test
     * this method is not tested for real time data and cannot be tested at least in a year of use
     * so we can use dummy data of varying random time to populate the database and then the tests will be performed on this bases
     * helper link
     * https://stackoverflow.com/questions/2937086/how-to-get-the-first-day-of-the-current-week-and-month
     */
    private long getRequiredTimeInMilliSeconds(String time) {
        Calendar calender = Calendar.getInstance();
        switch (time) {
            case "last30Time":
                calender.add(Calendar.DAY_OF_YEAR, -30);
                calender = clearTimes(calender);
                return calender.getTimeInMillis();
            case "thisMonthTime":
                calender.set(Calendar.DAY_OF_MONTH, 1);//this will set the day to 1st of month

                calender = clearTimes(calender);
                return calender.getTimeInMillis();
            case "thisYearTime":
                calender.set(Calendar.MONTH, 1);//set the month to 1st of the year
                calender = clearTimes(calender);
                return calender.getTimeInMillis();
            case "allTime":
                return 0;
            default:
                return 0;
        }

    }


    //code borrowed rom stack over flow link
    //https://stackoverflow.com/questions/12346198/convert-date-in-millisecond-to-today-yesterday-last-7-days-last-30-days-in-ja
    private static Calendar clearTimes(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }


//    public static String convertSimpleDayFormat(long val) {
//        Calendar today = Calendar.getInstance();
//        today = clearTimes(today);
//
//        Calendar yesterday = Calendar.getInstance();
//        yesterday.add(Calendar.DAY_OF_YEAR, -1);
//        yesterday = clearTimes(yesterday);
//
//        Calendar last7days = Calendar.getInstance();
//        last7days.add(Calendar.DAY_OF_YEAR, -7);
//        last7days = clearTimes(last7days);
//
//        Calendar last30days = Calendar.getInstance();
//        last30days.add(Calendar.DAY_OF_YEAR, -30);
//        last30days = clearTimes(last30days);
//
//
//        if (val > today.getTimeInMillis()) {
//            return "today";
//        } else if (val > yesterday.getTimeInMillis()) {
//            return "yesterday";
//        } else if (val > last7days.getTimeInMillis()) {
//            return "last7days";
//        } else if (val > last30days.getTimeInMillis()) {
//            return "last30days";
//        } else {
//            return "morethan30days";
//        }
//    }

}