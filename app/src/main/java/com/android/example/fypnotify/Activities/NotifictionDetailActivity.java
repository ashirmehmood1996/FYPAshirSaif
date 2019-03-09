package com.android.example.fypnotify.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.fypnotify.Models.NotificationModel;
import com.android.example.fypnotify.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotifictionDetailActivity extends AppCompatActivity {
    private TextView recieversTextView, timeStampTextView, messageTextView, titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiction_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notification");
        //initialize fields
        recieversTextView = findViewById(R.id.tv_notification_detail_recievers);
        timeStampTextView = findViewById(R.id.tv_notification_detail_time_stamp);
        titleTextView = findViewById(R.id.tv_notification_detail_title);
        messageTextView = findViewById(R.id.tv_notification_detail_message);

        //get the message from oher activity
        Intent intent = getIntent();
        NotificationModel notificationModel = (NotificationModel) intent.getSerializableExtra("notification");
        String title = notificationModel.getTitle();
        String message = notificationModel.getMessage();
        String timeStamp = notificationModel.getTimeStamp();
        String recievers = notificationModel.getRecievers();
        String uris= notificationModel.getUriCSV();

        long time = Long.parseLong(timeStamp);
        timeStamp = getApproprteDateTime(time);

        recieversTextView.setText(recievers);
        timeStampTextView.setText(timeStamp);
        titleTextView.setText(title);
        messageTextView.setText(message +"\n\n"+uris);


    }


    //took help from here
    //https://stackoverflow.com/questions/12818711/how-to-find-time-is-today-or-yesterday-in-android
    private String getApproprteDateTime(long timeStamp) {
        if (DateUtils.isToday(timeStamp)) {
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm aa");
            return formatter.format(new Date(timeStamp));
        } else if (isYesterday(timeStamp)) {
            SimpleDateFormat formatter = new SimpleDateFormat(" h:mm aa");
            return "yesterday," + formatter.format(new Date(timeStamp));
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd yyyy, h:mm aa");
            return formatter.format(new Date(timeStamp));
        }
    }

    private boolean isYesterday(long timeStamp) {
        return DateUtils.isToday(timeStamp + DateUtils.DAY_IN_MILLIS); //yesterday time and plus one day will give us today so will return true
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.information,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.information:
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.information_blueprint);
                dialog.setTitle("Information");
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
