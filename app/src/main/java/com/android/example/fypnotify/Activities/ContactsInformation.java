package com.android.example.fypnotify.Activities;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.R;

public class ContactsInformation extends AppCompatActivity {

    TextView mName , mPhoneNumber , mEmail , mWhatsApp;
    MemberModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_information);
        initializer();
        setValues();
    }

    private void initializer(){
        mName = findViewById(R.id.tv_contacts_info_name);
        mPhoneNumber = findViewById(R.id.tv_contacts_info_phonenumber);
        mEmail = findViewById(R.id.tv_contacts_info_email);
        mWhatsApp = findViewById(R.id.tv_contacts_info_whatsapp);
        model = (MemberModel) getIntent().getSerializableExtra("contact");
    }

    private void setValues(){
        mName.setText(model.getName());
        mPhoneNumber.setText(getPhoneNumber(model.getID()+""));
        mEmail.setText(getEmail(model.getID()+""));
        if(hasWhatsApp(model.getID()+"").equals("yes"))
            mWhatsApp.setText(model.getPhoneNumber());
        else
            mWhatsApp.setText("no WhatsApp");
    }

    private String getPhoneNumber(String currentContactId) {
        Cursor cur1 = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{currentContactId}, null);
        if (cur1.moveToNext()) {
           String number = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
           cur1.close();
            return number;
        } else {
            cur1.close();
            return "no Number";
        }

    }

    private String getEmail(String currentContactId) {
        Cursor cur1 = getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{currentContactId}, null);
        if (cur1.moveToNext()) {
            String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            return email;
        } else {
            cur1.close();
            return "no email";
        }

    }

    private String hasWhatsApp(String contactID) {
        String whatsAppExists = "no";
        boolean hasWhatsApp;

        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND account_type IN (?)";
        String[] selectionArgs = new String[]{contactID, "com.whatsapp"};
        Cursor cursor = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor != null) {
            hasWhatsApp = cursor.moveToNext();
            if (hasWhatsApp) {
                whatsAppExists = "yes";
            }
            cursor.close();
        }
        return whatsAppExists;
    }
}
