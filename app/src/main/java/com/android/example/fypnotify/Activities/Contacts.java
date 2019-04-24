package com.android.example.fypnotify.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.R;

import java.util.ArrayList;

public class Contacts extends Fragment {

    ArrayList<String> contactsList, contactNumberList, contactEmailList, contactID;
    ArrayList<Boolean> contactHasWhatsappList;
    ArrayList<MemberModel> memberModels;
    View rootView;
    TextView totalContacts;
    Database database;
    Boolean getContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_contacts, container, false);


        initializer();
        getContactsInfo();   // Method to get all the contacts

        return rootView;
    }

    private void initializer() {
        database = new Database(rootView.getContext());

        contactID = new ArrayList<>();
        contactsList = new ArrayList<>();
        contactNumberList = new ArrayList<>();
        contactEmailList = new ArrayList<>();
        contactHasWhatsappList = new ArrayList<>();
        memberModels = new ArrayList<>();

        Intent intent1 = getActivity().getIntent();
         getContact = intent1.getBooleanExtra("get contact",false);

    }

    private void recyclerView() {
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        RecyclerView.Adapter<ViewHolderRt> adapter = new RecyclerView.Adapter<ViewHolderRt>() {
            @NonNull
            @Override
            public ViewHolderRt onCreateViewHolder(@NonNull ViewGroup viewGroup, int ViewType) {
                return new ViewHolderRt(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contacts_list_blueprint, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolderRt viewHolderRt, final int i) {

                if(getContact){
                    viewHolderRt.ly_checkbox.setVisibility(View.VISIBLE);
                }

                viewHolderRt.ly_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(getContact){
                            MemberModel model = new MemberModel(Integer.parseInt(contactID.get(i)),contactsList.get(i),contactNumberList.get(i),"default");
                            if(contactHasWhatsappList.get(i)){
                                model.setIsOnWhatsApp(true);
                            }
                            memberModels.add(model);
                            Intent result = new Intent();
                            result.putExtra("members_contacts",memberModels);
                            getActivity().setResult(Activity.RESULT_OK,result);
                            getActivity().finish();
                        }
                    }
                });
                ((ViewHolderRt) viewHolderRt).contact_name.setText(contactsList.get(i));
                ((ViewHolderRt) viewHolderRt).phone_number.setText(contactNumberList.get(i));
                if (!contactEmailList.get(i).equals("no email")) {
                    ((ViewHolderRt) viewHolderRt).email.setText(contactEmailList.get(i));
                } else {
                    ((ViewHolderRt) viewHolderRt).email.setVisibility(View.GONE);
                }
                if (contactHasWhatsappList.get(i)) {
                    ((ViewHolderRt) viewHolderRt).logo.setVisibility(View.VISIBLE);
                } else {
                    ((ViewHolderRt) viewHolderRt).logo.setVisibility(View.GONE);
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
        Cursor phoneCursor = rootView.getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
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
        Cursor cur1 = rootView.getContext().getContentResolver().query(
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
        Cursor cursor = rootView.getContext().getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
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
        LinearLayout ly_contact , ly_checkbox;
        ImageView logo;
        CheckBox checkBox;

        public ViewHolderRt(@NonNull View itemView) {
            super(itemView);
            ly_contact = itemView.findViewById(R.id.ly_contact);
            ly_checkbox = itemView.findViewById(R.id.ly_checkbox);
            checkBox = itemView.findViewById(R.id.checkBox);
            contact_name = itemView.findViewById(R.id.tv_contactname);
            phone_number = itemView.findViewById(R.id.tv_phonenumber);
            email = itemView.findViewById(R.id.tv_email);
            logo = itemView.findViewById(R.id.iv_logo_whatsapp);

        }
    }



}


