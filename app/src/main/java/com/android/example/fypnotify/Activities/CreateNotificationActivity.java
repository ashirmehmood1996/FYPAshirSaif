package com.android.example.fypnotify.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.Models.NotificationModel;
import com.android.example.fypnotify.R;
import com.android.example.fypnotify.dataBase.DatabaseContract;
import com.android.example.fypnotify.dataBase.MembersDatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateNotificationActivity extends AppCompatActivity {
    public static final int RC_SELECT_CONTACTS = 1000;

    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottomSheetLinearLayout;
    private View blurrView;
    private EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);
        bottomSheetLinearLayout = findViewById(R.id.bottom_sheet_send_notificaton);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout);
        blurrView = findViewById(R.id.v_create_notification_blur);
        blurrView.setVisibility(View.GONE);

        setBottomSheetCallBacks();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        messageEditText = findViewById(R.id.et_cn_message_to_send);


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SELECT_CONTACTS) {
            if (resultCode == RESULT_OK) {

                blurrView.setVisibility(View.VISIBLE);

                final ArrayList<MemberModel> selectedContatcs = (ArrayList<MemberModel>) data.getSerializableExtra("members_contacts");

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetLinearLayout.findViewById(R.id.bt_bs_send_notification_by_mail)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendViaEmail(selectedContatcs);
                            }
                        });

                bottomSheetLinearLayout.findViewById(R.id.bt_bs_send_notification_by_sms)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendSmsToTheMemebers(selectedContatcs);
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            }
                        });

                bottomSheetLinearLayout.findViewById(R.id.bt_bs_send_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        selectedContatcs.clear();
                        blurrView.setVisibility(View.GONE);
                    }
                });


            } else if (resultCode == RESULT_CANCELED) {
                blurrView.setVisibility(View.GONE);

            }
        }
    }

    private void sendViaEmail(ArrayList<MemberModel> selectedContatcs) {

//// TODO: 2/13/2019 it is hard coded for now later assign proper emails dynamically
        String[] recipients = {"ashirmehmood1996@gmail.com", "qsaif4@gmail.com"};

        String subject = "Alert Notification";
        String message = messageEditText.getText().toString().trim();

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        //intent.setType("message/rfc822");
        if (intent.resolveActivity(getPackageManager()) != null) {
            //    startActivity(intent);
            startActivity(Intent.createChooser(intent, "Choose an email client"));
        }
        //member id is not used
        NotificationModel notification = new NotificationModel(0, "title Hrad coded", "" + messageEditText.getText().toString().trim(), "" + Calendar.getInstance().getTimeInMillis(),
                "" + recipients,null
        );
        writeSentNotificationToDatabase(notification);


    }

    private void sendSmsToTheMemebers(ArrayList<MemberModel> selectedContacts) {
        SmsManager smsManager = SmsManager.getDefault();
        String recievers = "";

        for (MemberModel currentMember : selectedContacts) {
            String number = currentMember.getPhoneNumber();
            smsManager.sendTextMessage(number, null, messageEditText.getText().toString().trim(), null, null);
            recievers = recievers + number + ", ";
        }
        //member id is not used
        NotificationModel notification = new NotificationModel(0, "title Hard coded", "" + messageEditText.getText().toString().trim(), "" + Calendar.getInstance().getTimeInMillis(),
                "" + recievers,null
        );
        writeSentNotificationToDatabase(notification);
        Toast.makeText(getApplicationContext(), "Sms sent to \n" + recievers, Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CreateNotificationActivity.super.onBackPressed();
                    }
                });
            }
        }, 300);

    }

    private synchronized void writeSentNotificationToDatabase(NotificationModel notification) {

        MembersDatabaseHelper historyDataBaseHelper = new MembersDatabaseHelper(this);
        SQLiteDatabase dbWrite = historyDataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.NotificationsEntry.COLOUMN_TITLE, notification.getTitle());
        contentValues.put(DatabaseContract.NotificationsEntry.COLOUMN_MESSAGE, notification.getMessage());
        contentValues.put(DatabaseContract.NotificationsEntry.COLOUMN_TIME_STAMP, notification.getTimeStamp());
        contentValues.put(DatabaseContract.NotificationsEntry.COLOUMN_RECIEVERS, notification.getRecievers());

        long id = dbWrite.insert(DatabaseContract.NotificationsEntry.TABLE_NAME,
                null,
                contentValues);
        System.out.println("id = " + id);


//implement the query to get the recipients and make them in groups on the basis of types

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_notification, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_create_notification_attach:
                Toast.makeText(this, "yet to implement ", Toast.LENGTH_SHORT).show();
                // TODO: 2/15/2019 the display only those contacts that are able to recieve via email or whats app and notify others using a text message
                break;
            case R.id.nav_create_notification_send:

                blurrView.setVisibility(View.VISIBLE);

                Intent intent = new Intent(CreateNotificationActivity.this, ContactsSelectionActivity.class);
                startActivityForResult(intent, RC_SELECT_CONTACTS);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}





