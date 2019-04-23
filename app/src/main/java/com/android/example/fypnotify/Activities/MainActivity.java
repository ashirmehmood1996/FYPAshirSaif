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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.example.fypnotify.Adapters.NotificationsHistoryAdapter;
import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.Models.NotificationModel;
import com.android.example.fypnotify.R;
import com.android.example.fypnotify.dataBase.DatabaseContract;
import com.android.example.fypnotify.dataBase.MembersDatabaseHelper;
import com.android.example.fypnotify.interfaces.NotificationItemClickListener;

import java.util.ArrayList;

import static com.android.example.fypnotify.dataBase.DatabaseContract.NotificationsEntry.COLOUMN_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NotificationItemClickListener {
    private RecyclerView recyclerView;
    private FloatingActionButton addNewConversationButton;
    private NotificationsHistoryAdapter notificationsHistoryAdapter;
    private ArrayList<NotificationModel> notificationArrayList;
    private final int USER_FORM_RESULT = 111;

    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottomSheetLinearLayout;

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
        bottomSheetLinearLayout = findViewById(R.id.ll_notification_detail_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

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
                projection, null, null, null, null, null);
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

            notificationArrayList.add(0, new NotificationModel(memberID, name, message, timeStamp, recievers, uriCVS));
        }
        if (notificationArrayList.isEmpty())
            emptyViewLineaLayout.setVisibility(View.VISIBLE);
        else
            emptyViewLineaLayout.setVisibility(View.GONE);
        notificationsHistoryAdapter.notifyDataSetChanged();
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
                startActivity(new Intent(MainActivity.this, CreateNotification.class));
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
                startActivity(new Intent(this, FracgmentsActivity.class));
                //startActivityForResult(new Intent(this, MemberFormActivity.class), USER_FORM_RESULT);
                /*break;
            case R.id.nav_main_show_all_contacts:
                startActivity(new Intent(this, ContactsActivity.class));
                break;*/
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

    private void setBottomSheetCallBacks() {

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:

                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:

                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:

                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:

                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }
}
