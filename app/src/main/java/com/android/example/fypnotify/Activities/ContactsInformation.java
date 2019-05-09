package com.android.example.fypnotify.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.R;

import java.util.ArrayList;

import static com.android.example.fypnotify.Activities.Database.TABLE_NAME;

public class ContactsInformation extends AppCompatActivity {

    TextView mName , mPhoneNumber , mEmail , mTvCircle;
    MemberModel model;
    ListView listView;
    Database database;
    ArrayList<String> list;
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
        mTvCircle = findViewById(R.id.tv_contacts_info_circle);
        listView = findViewById(R.id.listView_contacts_info);
        model = (MemberModel) getIntent().getSerializableExtra("contact");
        list = new ArrayList<>();
        database = new Database(this);
        Cursor cursor = database.getData("Select * from " + TABLE_NAME + " WHERE MEMBER_ID = '" + model.getID() + "'");
        while (cursor.moveToNext()){
            list.add(cursor.getString(1));
        }
        cursor.close();
    }

    private void setValues(){
        mName.setText(model.getName());
        String phoneNumber = getPhoneNumber(model.getID()+"");
        mPhoneNumber.setText(phoneNumber);
        mEmail.setText(getEmail(model.getID()+""));

        mTvCircle.setText(String.valueOf(model.getName().toUpperCase().charAt(0)));
        GradientDrawable magnitudeCircle = (GradientDrawable) mTvCircle.getBackground();
        magnitudeCircle.setColor(getMagnitudeColor(Math.random() * 10));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.simple_listview_item,list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContactsInformation.this, GroupMembers.class);
                intent.putExtra("title", list.get(position));
                startActivity(intent);
            }
        });

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


    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(this, magnitudeColorResourceId);
    }

}
