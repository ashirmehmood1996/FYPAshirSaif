package com.android.example.fypnotify.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.fypnotify.Models.NotificationModel;
import com.android.example.fypnotify.R;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotifictionDetailActivity extends AppCompatActivity {
    private TextView recieversTextView, timeStampTextView, messageTextView, titleTextView;
    private Button pdfAttachmentButton; // TODO: 5/4/2019  it is decided for now that only pdfs will be snet and recieved so its assumend that we have only one uri
    private Uri pdfUri;

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
        pdfAttachmentButton = findViewById(R.id.bt_pdf);

        //get the message from oher activity
        Intent intent = getIntent();
        NotificationModel notificationModel = (NotificationModel) intent.getSerializableExtra("notification");
        String title = notificationModel.getTitle();
        String message = notificationModel.getMessage();
        String timeStamp = notificationModel.getTimeStamp();
        String recievers = notificationModel.getRecievers();
        String uris = notificationModel.getUriCSV();

        long time = Long.parseLong(timeStamp);
        timeStamp = getApproprteDateTime(time);

        recieversTextView.setText(recievers);
        timeStampTextView.setText(timeStamp);
        titleTextView.setText(title);
        if (uris != null) {
            pdfAttachmentButton.setVisibility(View.VISIBLE);
            pdfUri = Uri.parse(uris);
            if (pdfUri != null) {
                String fileName = pdfUri.getPath().substring(pdfUri.getPath().lastIndexOf("/"));
                pdfAttachmentButton.setText(fileName);
                pdfAttachmentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);

                        intent1.setDataAndType(pdfUri, "application/pdf");

                        // TODO: 5/5/2019 for now pdf viewer is directly used later  a built in library can be used or user is forced to have  aodf viewer

                        if (intent1.resolveActivityInfo(getPackageManager(), 0) != null) {
                            startActivity(Intent.createChooser(intent1, "Open with")); //// FIXME: 10/28/2019 file exposed beyond file exception should be resolved using file provider
                        } else {
                            Toast.makeText(NotifictionDetailActivity.this, "no activity fund to view pdf files", Toast.LENGTH_SHORT).show();
                        }
                        // TODO: 5/5/2019 clean this code

                    }
                });
            }

        } else {
            pdfAttachmentButton.setVisibility(View.GONE);
        }
        messageTextView.setText(message + "\n\n" + uris);


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
        getMenuInflater().inflate(R.menu.information, menu);
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
// TODO: 5/4/2019 now
//  select the image and directlly convert to pdf no image files are gonna be sent
//   and send email from with in the app
//   use messages as an aknowledgement factor
//   add image recognition first for sending to groups or indivisuals



//TODO: later if required allow selection of prebuild pdfs from the gallery

//TODO :check that if we can generate the documnet by sending an intent to default text editor of the android
// FIXME: 5/4/2019 solve the first time filtering bug, that was noticed recently
//// TODO: 5/5/2019  the associated files must be in a specific folder and must be read only files if possible
// TODO: 5/5/2019  these files will also be imported and exported alon with the dabases may be we should link them up with google dribve or firebas eif required
