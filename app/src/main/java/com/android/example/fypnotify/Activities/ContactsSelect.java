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
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.Models.NotificationModel;
import com.android.example.fypnotify.R;

import java.util.ArrayList;

public class ContactsSelect extends AppCompatActivity {

    ArrayList<String> selectedContactsId, contactNameList;
    ArrayList<Boolean>  isSelected ;
    ArrayList<MemberModel> memberModelsToSend , contactsModelList , filteredArrayList;
    TextView title;
    CheckBox checkBox;
    Database database;
    int count;
    RecyclerView.Adapter<ContactsSelect.ViewHolderRt> adapter;
    private Boolean getContact;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_select);
        setSupportActionBar(findViewById(R.id.toolbar_select_contacts));
        initializer();
        getContactsInfo();
    }

    private void initializer() {
        database = new Database(this);
        contactNameList = new ArrayList<>();
        contactsModelList = new ArrayList<>();
        filteredArrayList = new ArrayList<>();
        isSelected = new ArrayList<>();
        memberModelsToSend = new ArrayList<>();
        selectedContactsId = new ArrayList<>();

         getContact = getIntent().getBooleanExtra("get contact",false);

        //Toolbar
        checkBox = findViewById(R.id.checkBox_select_all_contacts);
        title = findViewById(R.id.tv_toolbar_counter);
        checkBox.setVisibility(View.VISIBLE);
        title.setText("Selected Contacts ");

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    count=0;
                    for (int i = 0; i < isSelected.size(); i++) {
                        selectedContactsId.add(contactsModelList.get(i).getID()+"");
                        isSelected.set(i, true);
                        count++;
                    }
                    title.setText("Selected Contacts "+count);
                    adapter.notifyDataSetChanged();
                } else {
                    selectedContactsId.clear();
                    for (int i = 0; i < isSelected.size(); i++) {
                        isSelected.set(i, false);
                        count--;
                    }
                    title.setText("Selected Contacts "+count);
                    adapter.notifyDataSetChanged();
                }
            }
        });


    }

    private void recyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_cs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerView.Adapter<ContactsSelect.ViewHolderRt>() {
            @NonNull
            @Override
            public ContactsSelect.ViewHolderRt onCreateViewHolder(@NonNull ViewGroup viewGroup, int ViewType) {
                return new ContactsSelect.ViewHolderRt(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contacts_list_blueprint, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull ContactsSelect.ViewHolderRt viewHolderRt, final int i) {
                viewHolderRt.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked && !isSelected.get(i)) {
                                isSelected.set(i, true);
                                count++;
                                title.setText("Selected Contacts " + count);
                            } else if (!isChecked && isSelected.get(i)) {
                                    isSelected.set(i, false);
                                    count--;
                                    title.setText("Selected Contacts " + count);
                            }
                        }

                });

                if (isSelected.get(i)){
                    viewHolderRt.checkBox.setChecked(true);
                }else{

                    viewHolderRt.checkBox.setChecked(false);
                }

                viewHolderRt.ly_checkbox.setVisibility(View.VISIBLE);
                viewHolderRt.ly_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isSelected.get(i)) {
                            viewHolderRt.checkBox.setChecked(false);
                        } else {
                            viewHolderRt.checkBox.setChecked(true);
                        }
                    }
                });

                viewHolderRt.contact_name.setText(contactsModelList.get(i).getName());
                viewHolderRt.phone_number.setText(contactsModelList.get(i).getPhoneNumber());
                if (!contactsModelList.get(i).getEmail().equals("no email")) {
                     viewHolderRt.email.setText(contactsModelList.get(i).getEmail());
                } else {
                    viewHolderRt.email.setVisibility(View.GONE);
                }
                if (contactsModelList.get(i).isOnWhatsApp()) {
                    viewHolderRt.logo.setVisibility(View.VISIBLE);
                } else {
                    viewHolderRt.logo.setVisibility(View.GONE);
                }
            }

            @Override
            public int getItemCount() {
                return contactsModelList.size();
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void getContactsInfo() {
        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        while (phoneCursor.moveToNext()) {
            // checking if the contact is not already present in the list
            String name = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if (!contactNameList.contains(name)) {
                contactNameList.add(name);
                String currentContactId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                MemberModel tempModel = new MemberModel(Integer.parseInt(currentContactId),name,phoneNumber,"default");
                tempModel.setEmail(getEmail(currentContactId));
                isSelected.add(false);
                if (hasWhatsApp(currentContactId) == "yes") {
                   tempModel.setIsOnWhatsApp(true);
                } else {
                    tempModel.setIsOnWhatsApp(false);
                }
                contactsModelList.add(tempModel);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.nav_delete_selected_contacts).setVisible(false);

        MenuItem searchMenuItem = menu.findItem(R.id.nav_search_selected_contacts);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();// etrach the searchview from menu item // search view must be casted as anroid widget v7
        searchView.setQueryHint("search here");

        ArrayList<MemberModel> temp = contactsModelList;
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

                ArrayList<MemberModel> tempfilteredArrayList = getFilteredNotificationsArrayList(temp, newText);

                if (tempfilteredArrayList.isEmpty()) {
                    // TODO: 3/9/2019 deal with this later
                    //show no reslts view
                } else {
                    contactsModelList = tempfilteredArrayList;
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_submit_selected_contacts) {

            if(getContact){
                for (int i = 0; i < isSelected.size(); i++) {
                    if (isSelected.get(i)) {
                        memberModelsToSend.add(contactsModelList.get(i));
                    }
                }
                Intent result = new Intent();
                result.putExtra("members_contacts", memberModelsToSend);
                setResult(Activity.RESULT_OK,result);
                finish();
            }else{
                for (int i = 0; i < isSelected.size(); i++) {
                    if (isSelected.get(i)) {
                        selectedContactsId.add(contactsModelList.get(i).getID()+"");
                    }
                }

                Intent result = new Intent();
                result.putStringArrayListExtra("resultArray", selectedContactsId);//fixed
                setResult(Activity.RESULT_OK, result);
                finish();
            }

        }

        return true;
    }

    private ArrayList<MemberModel> getFilteredNotificationsArrayList(ArrayList<MemberModel> contactsModelList, String queryText) {
        ArrayList<MemberModel> filteredArrayList = new ArrayList<>();
        queryText = queryText.toLowerCase();
        for (MemberModel currentNotification : contactsModelList) {
            String title = currentNotification.getName().toLowerCase();
            if (title.contains(queryText)) {
                filteredArrayList.add(currentNotification);
            }
        }
        return filteredArrayList;
    }

    private class ViewHolderRt extends RecyclerView.ViewHolder {
        TextView contact_name, phone_number, email;
        LinearLayout ly_contact;
        LinearLayout ly_checkbox;
        ImageView logo;
        CheckBox checkBox;

        public ViewHolderRt(@NonNull View itemView) {
            super(itemView);
            ly_contact = itemView.findViewById(R.id.ly_contact);
            ly_checkbox = itemView.findViewById(R.id.ly_checkbox);
            contact_name = itemView.findViewById(R.id.tv_contactname);
            phone_number = itemView.findViewById(R.id.tv_phonenumber);
            email = itemView.findViewById(R.id.tv_email);
            logo = itemView.findViewById(R.id.iv_logo_whatsapp);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }



}
