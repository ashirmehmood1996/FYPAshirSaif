package com.android.example.fypnotify.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.example.fypnotify.Adapters.ContactsAdapter;
import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.R;
import com.android.example.fypnotify.dataBase.DatabaseContract;
import com.android.example.fypnotify.dataBase.MembersDatabaseHelper;
import com.android.example.fypnotify.interfaces.ContactsListener;


import java.util.ArrayList;


public class ContactsSelectionActivity extends AppCompatActivity implements ContactsListener {
    private RecyclerView contatactsSelectionRecyclerView;
    private ArrayList<MemberModel> memberModelArrayList;
    private ArrayList<MemberModel> selectedContacts;
    private ContactsAdapter contactsAdapter;
    private Button sendNotificatonButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_selection);
        sendNotificatonButton = findViewById(R.id.bt_cs_send_notification);

        //recyclerview related
        contatactsSelectionRecyclerView = findViewById(R.id.rv_cs_contacts_holder);
        contatactsSelectionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        memberModelArrayList = new ArrayList<>();
        loadContacts();
        selectedContacts = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(memberModelArrayList, this);
        contatactsSelectionRecyclerView.setAdapter(contactsAdapter);

        sendNotificatonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if size is >0
                if (selectedContacts.size() > 0) {
                    sendNotificationTo(selectedContacts);
                } else {
                    Toast.makeText(ContactsSelectionActivity.this, "please select some recipient contacts", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadContacts() {
        MembersDatabaseHelper membersDatabaseHelper = new MembersDatabaseHelper(this);
        SQLiteDatabase database = membersDatabaseHelper.getReadableDatabase();
        String[] projection = {DatabaseContract.MembersEntry.COLOUMN_ID,
                DatabaseContract.MembersEntry.COLOUMN_NAME,//array of coloumns that I want to retrieve from the tabe
                DatabaseContract.MembersEntry.COLOUMN_PHONE_NUMBER,
                DatabaseContract.MembersEntry.COLOUMN_USER_TYPE};
        Cursor resultCursor = database.query(DatabaseContract.MembersEntry.TABLE_NAME,
                projection, null, null, null, null, null);
        int coloumnNameIndex = resultCursor.getColumnIndex(DatabaseContract.MembersEntry.COLOUMN_NAME);
        int coloumnPhoneNumberIndex = resultCursor.getColumnIndex(DatabaseContract.MembersEntry.COLOUMN_PHONE_NUMBER);
        int coloumnMemberTypeIndex = resultCursor.getColumnIndex(DatabaseContract.MembersEntry.COLOUMN_USER_TYPE);
        int coloumnIDIndex = resultCursor.getColumnIndex(DatabaseContract.MembersEntry.COLOUMN_ID);

        while (resultCursor.moveToNext()) {
            int memberID = resultCursor.getInt(coloumnIDIndex);
            String name = resultCursor.getString(coloumnNameIndex);
            String phoneNumber = resultCursor.getString(coloumnPhoneNumberIndex);
            String memberType = resultCursor.getString(coloumnMemberTypeIndex);
            memberModelArrayList.add(new MemberModel(memberID, name, phoneNumber, memberType));
        }
    }

    private void sendNotificationTo(ArrayList<MemberModel> memberModels) {
        Intent intent = new Intent();
        intent.putExtra("members_contacts", memberModels);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void selectContact(int position) {
        selectedContacts.add(memberModelArrayList.get(position));
    }

    @Override
    public void unSelectContact(int position) {
        selectedContacts.remove(memberModelArrayList.get(position));
    }
}
