package com.android.example.fypnotify.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.example.fypnotify.Database.Database;
import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.R;

import java.util.ArrayList;

public class ContactsSelect extends AppCompatActivity {

    ArrayList<String> selectedContactsId, contactNameList;
    ArrayList<MemberModel> memberModelsToSend ,tempMemberModel;
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
        getContactsIdAndName();
    }

    private void initializer() {
        database = new Database(this);
        tempMemberModel = new ArrayList<>();
        contactNameList = new ArrayList<>();
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
                    for (int i = 0; i < tempMemberModel.size(); i++) {
                        tempMemberModel.get(i).setSelected(true);
                        count++;
                    }
                    title.setText("Selected Contacts "+count);
                    adapter.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < tempMemberModel.size(); i++) {
                        tempMemberModel.get(i).setSelected(false);
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
                return new ContactsSelect.ViewHolderRt(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.displaycontacts_list_blueprint, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull ContactsSelect.ViewHolderRt viewHolderRt, final int i) {
                viewHolderRt.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked && !tempMemberModel.get(i).isSelected()) {
                                tempMemberModel.get(i).setSelected(true);
                                count++;
                                title.setText("Selected Contacts " + count);
                            } else if (!isChecked && tempMemberModel.get(i).isSelected()) {
                                tempMemberModel.get(i).setSelected(false);
                                count--;
                                title.setText("Selected Contacts " + count);
                            }
                        }

                });

                if (tempMemberModel.get(i).isSelected()){
                    viewHolderRt.checkBox.setChecked(true);
                }else{

                    viewHolderRt.checkBox.setChecked(false);
                }


                viewHolderRt.ly_display_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tempMemberModel.get(i).isSelected()) {
                            viewHolderRt.checkBox.setChecked(false);
                        } else {
                            viewHolderRt.checkBox.setChecked(true);
                        }
                    }
                });

                viewHolderRt.contact_name.setText(tempMemberModel.get(i).getName());
                viewHolderRt.ly_checkbox.setVisibility(View.VISIBLE);

                viewHolderRt.tvCircle.setText(String.valueOf(tempMemberModel.get(i).getName().toUpperCase().charAt(0)));
                GradientDrawable magnitudeCircle = (GradientDrawable) viewHolderRt.tvCircle.getBackground();

                // Get the appropriate background color based on the current earthquake magnitude
                int magnitudeColor = getMagnitudeColor(Math.random() * 10);

                // Set the color on the magnitude circle
                magnitudeCircle.setColor(magnitudeColor);
            }

            @Override
            public int getItemCount() {
                return tempMemberModel.size();
            }
        };
        recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.nav_delete_selected_contacts).setVisible(false);

        MenuItem searchMenuItem = menu.findItem(R.id.nav_search_selected_contacts);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();// etrach the searchview from menu item // search view must be casted as anroid widget v7
        searchView.setQueryHint("search here");
        ArrayList<MemberModel> temp = tempMemberModel;
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
                    tempMemberModel.clear();
                } else {
                    tempMemberModel = tempfilteredArrayList;
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
                for (int i = 0; i < tempMemberModel.size(); i++) {
                    if (tempMemberModel.get(i).isSelected()) {
                        String currentContactId = Integer.toString(tempMemberModel.get(i).getID());
                        String email = getEmail(currentContactId);
                        MemberModel tempModel = new MemberModel(tempMemberModel.get(i).getID(),tempMemberModel.get(i).getName(),getPhoneNumber(currentContactId),"default");
                        tempModel.setEmail(email);
                        memberModelsToSend.add(tempModel);
                    }

                }
                Intent result = new Intent();
                result.putExtra("members_contacts", memberModelsToSend);
                setResult(Activity.RESULT_OK,result);
                finish();
            }else{
                for (int i = 0; i < tempMemberModel.size(); i++) {
                    if (tempMemberModel.get(i).isSelected()) {
                        selectedContactsId.add(Integer.toString(tempMemberModel.get(i).getID()));
                    }
                }

                Intent result = new Intent();
                result.putStringArrayListExtra("resultArray", selectedContactsId);//fixed
                setResult(Activity.RESULT_OK, result);
                finish();
            }

        }
///add changes to test
        return true;
    }

    private void getContactsIdAndName() {
        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        while (phoneCursor.moveToNext()) {
            // checking if the contact is not already present in the list
            String name = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if (!contactNameList.contains(name)) {
                String currentContactId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                contactNameList.add(name);
                MemberModel model = new MemberModel(Integer.parseInt(currentContactId),name,null,"default");
                model.setSelected(false);
                tempMemberModel.add(model);
            }
        }
        phoneCursor.close();
        recyclerView();
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
        Cursor cur1 = this.getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{currentContactId}, null);
        if (cur1.moveToNext()) {
            String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            return email;
        } else {
            cur1.close();
            return null;
        }

    }

    private ArrayList<MemberModel> getFilteredNotificationsArrayList(ArrayList<MemberModel> tempMemberModelList, String queryText) {
        ArrayList<MemberModel> filteredArrayList = new ArrayList<>();
        queryText = queryText.toLowerCase();
        for (MemberModel model : tempMemberModelList) {
            String title = model.getName().toLowerCase();
            if (title.contains(queryText)) {
                filteredArrayList.add(model);
            }
        }
        return filteredArrayList;
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

    private class ViewHolderRt extends RecyclerView.ViewHolder {
        TextView contact_name, tvCircle ;
        LinearLayout ly_display_contact , ly_checkbox;
        CheckBox checkBox;

        public ViewHolderRt(@NonNull View itemView) {
            super(itemView);
            ly_display_contact = itemView.findViewById(R.id.ly_display_contacts);
            ly_checkbox = itemView.findViewById(R.id.ly_display_contact_checkbox);
            checkBox = itemView.findViewById(R.id.display_contact_checkBox);
            tvCircle = itemView.findViewById(R.id.tv_display_contact_circle);
            contact_name = itemView.findViewById(R.id.tv_display_contact_contactname);
        }

    }

}
