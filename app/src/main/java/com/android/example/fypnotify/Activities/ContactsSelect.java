package com.android.example.fypnotify.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.example.fypnotify.R;

import java.util.ArrayList;

public class ContactsSelect extends AppCompatActivity {

    ArrayList<String> contactsList, contactNumberList, contactEmailList, contactID;
    ArrayList<Boolean> contactHasWhatsappList;
    TextView totalContacts;

    Database database;
    Boolean getContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_select);
        initializer();
        getContactsInfo();
    }

    private void initializer() {
        database = new Database(this);

        contactID = new ArrayList<>();
        contactsList = new ArrayList<>();
        contactNumberList = new ArrayList<>();
        contactEmailList = new ArrayList<>();
        contactHasWhatsappList = new ArrayList<>();

        Intent intent1 = this.getIntent();
        getContact = intent1.getBooleanExtra("get contact",false);

    }

    private void recyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_cs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.Adapter<ContactsSelect.ViewHolderRt> adapter = new RecyclerView.Adapter<ContactsSelect.ViewHolderRt>() {
            @NonNull
            @Override
            public ContactsSelect.ViewHolderRt onCreateViewHolder(@NonNull ViewGroup viewGroup, int ViewType) {
                return new ContactsSelect.ViewHolderRt(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contacts_list_blueprint, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull ContactsSelect.ViewHolderRt viewHolderRt, final int i) {

                viewHolderRt.ly_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(getContact){
                            Intent result = new Intent();
                            result.putExtra("result",contactID.get(i));
                            setResult(Activity.RESULT_OK,result);
                            finish();
                        }
                    }
                });
                ((ContactsSelect.ViewHolderRt) viewHolderRt).contact_name.setText(contactsList.get(i));
                ((ContactsSelect.ViewHolderRt) viewHolderRt).phone_number.setText(contactNumberList.get(i));
                if (!contactEmailList.get(i).equals("no email")) {
                    ((ContactsSelect.ViewHolderRt) viewHolderRt).email.setText(contactEmailList.get(i));
                } else {
                    ((ContactsSelect.ViewHolderRt) viewHolderRt).email.setVisibility(View.GONE);
                }
                if (contactHasWhatsappList.get(i)) {
                    ((ContactsSelect.ViewHolderRt) viewHolderRt).logo.setVisibility(View.VISIBLE);
                } else {
                    ((ContactsSelect.ViewHolderRt) viewHolderRt).logo.setVisibility(View.GONE);
                }
            }

            @Override
            public int getItemCount() {
                return contactsList.size();
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void getContactsInfo() {
        Cursor phoneCursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phoneCursor.moveToNext()) {
            // checking if the contact is not already present in the list
            if (!contactsList.contains(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))) {
                String currentContactId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                contactID.add(currentContactId);
                contactsList.add(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                contactNumberList.add(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                contactEmailList.add(getEmail(currentContactId));
                if (hasWhatsApp(currentContactId) == "yes") {
                    contactHasWhatsappList.add(true);
                } else {
                    contactHasWhatsappList.add(false);
                }
            }


        }

        phoneCursor.close();
        recyclerView();
    }

    private String getEmail(String currentContactId) {
        Cursor cur1 = this.getContentResolver().query(
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
        Cursor cursor = this.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor != null) {
            hasWhatsApp = cursor.moveToNext();
            if (hasWhatsApp) {
                whatsAppExists = "yes";
            }
            cursor.close();
        }
        return whatsAppExists;
    }

    private class ViewHolderRt extends RecyclerView.ViewHolder {
        TextView contact_name, phone_number, email;
        LinearLayout ly_contact;
        ImageView logo;

        public ViewHolderRt(@NonNull View itemView) {
            super(itemView);
            ly_contact = itemView.findViewById(R.id.ly_contact);
            contact_name = itemView.findViewById(R.id.tv_contactname);
            phone_number = itemView.findViewById(R.id.tv_phonenumber);
            email = itemView.findViewById(R.id.tv_email);
            logo = itemView.findViewById(R.id.iv_logo_whatsapp);
        }
    }
}