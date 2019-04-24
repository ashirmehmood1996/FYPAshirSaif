package com.android.example.fypnotify.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.android.example.fypnotify.Adapters.ContactsAdapter;
import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.R;
import com.android.example.fypnotify.dataBase.DatabaseContract;
import com.android.example.fypnotify.dataBase.MembersDatabaseHelper;
import com.android.example.fypnotify.interfaces.ContactsListener;

import java.util.ArrayList;

import static com.android.example.fypnotify.dataBase.DatabaseContract.MembersEntry.COLOUMN_ID;


public class ContactsActivity extends AppCompatActivity implements ContactsListener {
    private RecyclerView recyclerView;
    private ContactsAdapter contactsAdapter;
    private ArrayList<MemberModel> memberModelArrayList;
    private ArrayList<MemberModel> selectedContacts;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        toolbar = findViewById(R.id.toolbar_select_contacts);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rv_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        memberModelArrayList = new ArrayList<>();
        selectedContacts = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(memberModelArrayList, this);
        loadContacts();
        recyclerView.setAdapter(contactsAdapter);
    }

    private void loadContacts() {
        MembersDatabaseHelper membersDatabaseHelper = new MembersDatabaseHelper(this);
        SQLiteDatabase database = membersDatabaseHelper.getReadableDatabase();
        String[] projection = {COLOUMN_ID,
                DatabaseContract.MembersEntry.COLOUMN_NAME,//array of coloumns that I want to retrieve from the tabe
                DatabaseContract.MembersEntry.COLOUMN_PHONE_NUMBER,
                DatabaseContract.MembersEntry.COLOUMN_USER_TYPE};
        Cursor resultCursor = database.query(DatabaseContract.MembersEntry.TABLE_NAME,
                projection, null, null, null, null, null);
        int coloumnNameIndex = resultCursor.getColumnIndex(DatabaseContract.MembersEntry.COLOUMN_NAME);
        int coloumnPhoneNumberIndex = resultCursor.getColumnIndex(DatabaseContract.MembersEntry.COLOUMN_PHONE_NUMBER);
        int coloumnMemberTypeIndex = resultCursor.getColumnIndex(DatabaseContract.MembersEntry.COLOUMN_USER_TYPE);
        int coloumnIDIndex = resultCursor.getColumnIndex(COLOUMN_ID);

        while (resultCursor.moveToNext()) {
            int memberID = resultCursor.getInt(coloumnIDIndex);
            String name = resultCursor.getString(coloumnNameIndex);
            String phoneNumber = resultCursor.getString(coloumnPhoneNumberIndex);
            String memberType = resultCursor.getString(coloumnMemberTypeIndex);
            memberModelArrayList.add(new MemberModel(memberID, name, phoneNumber, memberType));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_contacts_delete_selected:
                if (selectedContacts.size() > 0) {
                    memberModelArrayList.removeAll(selectedContacts);
                    deleteMembersFromDatabase(selectedContacts);
                    selectedContacts.clear();
                    contactsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "select some contacts to delete", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void selectContact(int position) {

        selectedContacts.add(memberModelArrayList.get(position));

    }

    @Override
    public void unSelectContact(int position) {
        selectedContacts.remove(memberModelArrayList.get(position));
    }

    private void deleteMembersFromDatabase(ArrayList<MemberModel> members) {
        for (MemberModel currenMmber : members) {
            MembersDatabaseHelper membersDatabaseHelper = new MembersDatabaseHelper(this);
            SQLiteDatabase db = membersDatabaseHelper.getWritableDatabase();
            String whereClause = COLOUMN_ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(currenMmber.getID())};
            int deleteResult = db.delete(DatabaseContract.MembersEntry.TABLE_NAME, whereClause, whereArgs);
            Toast.makeText(this, "deleted member = " + deleteResult, Toast.LENGTH_SHORT).show();

// TODO: 2/11/2019 remember to close the cursor in all places where they are used
        }
    }
}
