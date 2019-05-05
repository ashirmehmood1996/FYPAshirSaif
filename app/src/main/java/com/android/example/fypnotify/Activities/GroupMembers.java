package com.android.example.fypnotify.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.R;

import java.util.ArrayList;

import static com.android.example.fypnotify.Activities.Database.TABLE_NAME;
// FIXME: 4/23/2019 add a check to detect repetition in groups

public class GroupMembers extends AppCompatActivity {
    private ArrayList<String> contactsIdList;
    private ArrayList<String> contactsNameList;
    private ArrayList<String> newlyAddedContactsId;
    private ArrayList<String> contactType;
    private ArrayList<String> selectedContactsId;
    private ArrayList<String> totalGroupMembers;
    private ArrayList<String> stack;
    private ArrayList<Boolean> isSelected;
    private ArrayList<MemberModel> contactsInfoModel;
    private ArrayList<String> selectedContactsToSend;
    private ArrayList<MemberModel> memberModelsToSend;
    private RecyclerView.Adapter<GroupMembers.ViewHolderRt> adapter;
    private LinearLayout nonEmptyGroupLy;
    private LinearLayout emptyGroupLy;
    private Button btnAddMember;
    private String groupTitle;
    private int count;
    private boolean isSelectionActive = false , isFirstTimeRunning = true ,isNoMember=true ,groupChanged = false,selectionForSend;
    private TextView title;
    private CheckBox toolbarCheckBox;
    private Menu toolbarMenu;
    Database database ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        setSupportActionBar(findViewById(R.id.toolbar_select_contacts));
        initializer();
        getContactsOfGroups("Select * from " + TABLE_NAME + " WHERE Group_Title = '" + groupTitle + "'");
        recyclerView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbarMenu = menu;
        getMenuInflater().inflate(R.menu.group_member, menu);
        menu.findItem(R.id.nav_delete_selected_contacts_group_members).setVisible(false);
        if(isNoMember) {
            menu.findItem(R.id.nav_search_selected_contacts_group_members).setVisible(false);
        }
        if(selectionForSend){
            menu.findItem(R.id.nav_submit_selected_contacts_group_members).setVisible(true);
            menu.findItem(R.id.nav_action_add_member_group_members).setVisible(false);
            menu.findItem(R.id.nav_enable_delete_options_contacts_group_members).setVisible(false);
        }else {
            menu.findItem(R.id.nav_submit_selected_contacts_group_members).setVisible(false);
            }
        isFirstTimeRunning = false;
        MenuItem searchMenuItem = menu.findItem(R.id.nav_search_selected_contacts_group_members);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();// etrach the searchview from menu item // search view must be casted as anroid widget v7
        searchView.setQueryHint("search here");
        ArrayList<String> temp = contactsNameList;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true; // // TODO: 3/8/2019 deal here if needed
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<String> tempfilteredArrayList = getFilteredNotificationsArrayList(temp, newText);

                if (tempfilteredArrayList.isEmpty()) {
                    contactsNameList.clear();
                } else {
                    contactsNameList = tempfilteredArrayList;
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_enable_delete_options_contacts_group_members:
                isSelectionActive = true;
                adapter.notifyDataSetChanged();
                return true;
            case R.id.nav_action_add_member_group_members:
                final Dialog selectDialog = new Dialog(this);
                selectDialog.setTitle("Select");
                selectDialog.setContentView(R.layout.select_dialog);

                Button createGroup = selectDialog.findViewById(R.id.add_group_dialog_btn);
                createGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectDialog.setContentView(R.layout.create_group_dialog);
                        final String[] group_name = new String[1];

                        final EditText title = selectDialog.findViewById(R.id.et_dialog_group_title);

                        TextView cancel = (TextView) selectDialog.findViewById(R.id.tv_dialog_cancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectDialog.dismiss();
                            }
                        });

                        TextView create = selectDialog.findViewById(R.id.tv_dialog_create_group);
                        create.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (!title.getText().toString().equals("")) {
                                    Cursor cur = database.getData("Select * from " + TABLE_NAME + " WHERE Group_Title = '" + title.getText().toString() + "'");
                                    if(cur.getCount()==0){
                                        group_name[0] = title.getText().toString();
                                        Boolean result = database.insertData(group_name[0], "null", "Group");
                                        Boolean result2 = database.insertData(groupTitle, group_name[0], "Group");
                                        if (result == true && result2 == true) {
                                            Toast.makeText(getBaseContext(), "Group created", Toast.LENGTH_SHORT).show();
                                            selectDialog.dismiss();
                                            refreshList(groupTitle);
                                        } else {
                                            Toast.makeText(getBaseContext(), "Opps something went wrong !", Toast.LENGTH_SHORT).show();
                                            selectDialog.dismiss();
                                        }
                                    }
                                    else {
                                        Toast.makeText(getBaseContext(), "Group/Subgroup with same name exists", Toast.LENGTH_LONG).show();
                                    }
                                } else
                                    Toast.makeText(getBaseContext(), "Enter Title First !", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                Button addContact = selectDialog.findViewById(R.id.add_contact_dialog_btn);
                addContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GroupMembers.this, ContactsSelect.class);
                        startActivityForResult(intent, 1);
                        selectDialog.dismiss();
                    }
                });
                selectDialog.show();

                return true;
            case R.id.nav_delete_selected_contacts_group_members:
                for(int i=0 ; i<contactsIdList.size() ; i++){
                    if(isSelected.get(i)){
                        database.deleteData(groupTitle, contactsIdList.get(i), contactType.get(i));
                    }
                }
                isSelectionActive = false;
                toolbarMenu.findItem(R.id.nav_search_selected_contacts_group_members).setVisible(true);
                toolbarMenu.findItem(R.id.nav_action_add_member_group_members).setVisible(true);
                toolbarMenu.findItem(R.id.nav_delete_selected_contacts_group_members).setVisible(false);
                toolbarCheckBox.setVisibility(View.GONE);
                refreshList(groupTitle);

                return true;
            case R.id.nav_submit_selected_contacts_group_members:

                for (int i = 0; i <selectedContactsId.size(); i++) {
                    String currentContact = selectedContactsId.get(i);
                    if(currentContact.matches("\\d+(?:\\.\\d+)?"))
                    {
                        memberModelsToSend.add(getContactInfoToSend(currentContact));
                    }
                    else
                    {
                        getContactsOfGroupsToSend(currentContact);
                    }
                }
                Toast.makeText(GroupMembers.this,memberModelsToSend.size(),Toast.LENGTH_SHORT).show();
//                Intent result = new Intent();
//                result.putExtra("members_contacts", memberModelsToSend);
//                setResult(Activity.RESULT_OK,result);
//                finish(); // todo select groups to send notification
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (stack.size() == 0) {
                        finish();
                    } else {
                        groupTitle = stack.get(stack.size() - 1);
                        refreshList(groupTitle);
                        stack.remove(stack.size() - 1);
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            newlyAddedContactsId = data.getStringArrayListExtra("resultArray");
            Toast.makeText(getBaseContext(),  newlyAddedContactsId.size()+" Contacts Added", Toast.LENGTH_SHORT).show();

            Database database = new Database(getBaseContext());
            for(int i=0 ;i<newlyAddedContactsId.size() ; i++){
                database.insertData(groupTitle, newlyAddedContactsId.get(i), "Member");
            }
            refreshList(groupTitle);

        }

    }


    private void initializer() {
        contactsIdList = new ArrayList<>();
        contactsNameList = new ArrayList<>();
        newlyAddedContactsId = new ArrayList<>();
        contactType = new ArrayList<>();
        isSelected = new ArrayList<>();
        stack = new ArrayList<>();
        totalGroupMembers = new ArrayList<>();
        contactsInfoModel = new ArrayList<>();
        selectedContactsToSend = new ArrayList<>();
        selectedContactsId = new ArrayList<>();
        memberModelsToSend = new ArrayList<>();
        database = new Database(getBaseContext());
        Intent intent = getIntent();
        groupTitle = intent.getStringExtra("title");
        selectionForSend = intent.getBooleanExtra("selectionForSend",false);
        if(selectionForSend){
            isSelectionActive = true;
        }
        emptyGroupLy = findViewById(R.id.ly_empty_group);
        nonEmptyGroupLy = findViewById(R.id.ly_non_empty_group);
        btnAddMember = findViewById(R.id.btn_add_member_groupmember);

        //Toolbar
        toolbarCheckBox = findViewById(R.id.checkBox_select_all_contacts);
        title = findViewById(R.id.tv_toolbar_counter);
        toolbarCheckBox.setVisibility(View.GONE);
        title.setText(groupTitle);

        toolbarCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!groupChanged){
                    if (isChecked) {
                        count=0;
                        for (int i = 0; i < contactsIdList.size(); i++) {
                            if(selectionForSend){
                                if(selectedContactsToSend.contains(contactsIdList.get(i)))
                                    return;
                                else
                                selectedContactsToSend.add(contactsIdList.get(i));
                            }
                            else{
                            isSelected.set(i,true);
                            }
                            count++;
                        }
                        title.setText("Selected Contacts "+count);
                        adapter.notifyDataSetChanged();
                    } else {
                        for (int i = 0; i < contactsIdList.size(); i++) {
                            if(selectionForSend){
                                if(selectedContactsToSend.contains(contactsIdList.get(i))){
                                    for(int j=0 ;j<selectedContactsToSend.size() ;j++){
                                        if(selectedContactsToSend.get(j).equals(contactsIdList.get(i))){
                                            selectedContactsToSend.remove(j);
                                        }
                                    }
                                }
                            }
                            else{
                                isSelected.set(i,false);
                            }
                            count--;
                        }
                        title.setText("Selected Contacts "+count);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

    private void recyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_contacts_group);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerView.Adapter<GroupMembers.ViewHolderRt>() {
            @NonNull
            @Override
            public GroupMembers.ViewHolderRt onCreateViewHolder(@NonNull ViewGroup viewGroup, int ViewType) {
                return new GroupMembers.ViewHolderRt(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contacts_list_blueprint, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull GroupMembers.ViewHolderRt viewHolderRt, final int i) {

                if(isSelectionActive && !isFirstTimeRunning){
                    toolbarCheckBox.setVisibility(View.VISIBLE);
                    viewHolderRt.ly_checkbox.setVisibility(View.VISIBLE);
                    title.setText("Members Selected "+count);
                    if(selectionForSend){
                        toolbarMenu.findItem(R.id.nav_delete_selected_contacts_group_members).setVisible(false);
                        toolbarMenu.findItem(R.id.nav_submit_selected_contacts_group_members).setVisible(true);
                    }else {
                        toolbarMenu.findItem(R.id.nav_delete_selected_contacts_group_members).setVisible(true);
                    }
                    toolbarMenu.findItem(R.id.nav_search_selected_contacts_group_members).setVisible(false);
                    toolbarMenu.findItem(R.id.nav_action_add_member_group_members).setVisible(false);
                    toolbarMenu.findItem(R.id.nav_enable_delete_options_contacts_group_members).setVisible(false);
                }else if(!isSelectionActive && !isFirstTimeRunning){
                    toolbarCheckBox.setVisibility(View.GONE);
                    viewHolderRt.ly_checkbox.setVisibility(View.GONE);
                    title.setText(groupTitle);
                    groupChanged = false;
                    toolbarMenu.findItem(R.id.nav_search_selected_contacts_group_members).setVisible(true);
                    toolbarMenu.findItem(R.id.nav_action_add_member_group_members).setVisible(true);
                    toolbarMenu.findItem(R.id.nav_delete_selected_contacts_group_members).setVisible(false);
                    toolbarMenu.findItem(R.id.nav_enable_delete_options_contacts_group_members).setVisible(true);
                }else{

                }

                if (contactType.get(i).equals("Group")) {
                    viewHolderRt.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (selectionForSend) {
                                if (isChecked && !selectedContactsToSend.contains(contactsIdList.get(i))) {
                                    selectedContactsToSend.add(contactsIdList.get(i));
                                    count++;
                                    title.setText("Members Selected " + count);
                                } else if (!isChecked && selectedContactsToSend.contains(contactsIdList.get(i))) {
                                    for (int j = 0; j < selectedContactsToSend.size(); j++) {
                                        if (selectedContactsToSend.get(j).equals(contactsIdList.get(i))) {
                                            selectedContactsToSend.remove(j);
                                        }
                                    }
                                    count--;
                                    title.setText("Members Selected " + count);
                                }
                            } else {
                                if (isChecked && !isSelected.get(i)) {
                                    isSelected.set(i, true);
                                    count++;
                                    title.setText("Members Selected " + count);
                                } else if (!isChecked && isSelected.get(i)) {
                                    isSelected.set(i, false);
                                    count--;
                                    title.setText("Members Selected " + count);
                                }
                            }
                         }
                    });

                    if(selectionForSend){
                        if(selectedContactsToSend.contains(contactsIdList.get(i))){
                            viewHolderRt.checkBox.setChecked(true);
                        }else{
                            viewHolderRt.checkBox.setChecked(false);
                        }
                    }else{
                        if (isSelected.get(i)){
                            viewHolderRt.checkBox.setChecked(true);
                        }else{

                            viewHolderRt.checkBox.setChecked(false);
                        }
                    }

                    viewHolderRt.ly_group.setVisibility(View.VISIBLE);
                    viewHolderRt.totalMembers.setText(totalGroupMembers.get(i));  // todo set this line
                    viewHolderRt.ly_contact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            stack.add(groupTitle);
                            groupTitle = contactsIdList.get(i);
                            toolbarCheckBox.setChecked(false);
                            refreshList(contactsIdList.get(i));
                        }
                    });

                    viewHolderRt.ly_contact.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            isSelectionActive = true;
                            isSelected.set(i,true);
                            count++;
                            adapter.notifyDataSetChanged();
                            return true; //todo
                        }
                    });
                    viewHolderRt.contact_name.setText(contactsNameList.get(i));
                    viewHolderRt.tvCircle.setText("G");
                    GradientDrawable magnitudeCircle = (GradientDrawable) viewHolderRt.tvCircle.getBackground();

                    // Get the appropriate background color based on the current earthquake magnitude
                    int magnitudeColor = getMagnitudeColor(Math.random() * 10);

                    // Set the color on the magnitude circle
                    magnitudeCircle.setColor(magnitudeColor);


                } else {
                    viewHolderRt.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (selectionForSend) {
                                if(isChecked && !selectedContactsToSend.contains(contactsIdList.get(i))){
                                    selectedContactsToSend.add(contactsIdList.get(i));
                                    count++;
                                    title.setText("Members Selected " + count);
                                }else if(!isChecked && selectedContactsToSend.contains(contactsIdList.get(i))){
                                    for(int j=0 ;j<selectedContactsToSend.size() ; j++){
                                        if(selectedContactsToSend.get(j).equals(contactsIdList.get(i))){
                                            selectedContactsToSend.remove(j);
                                        }
                                    }
                                    count--;
                                    title.setText("Members Selected " + count);
                                }
                            } else {
                                if (isChecked && !isSelected.get(i)) {
                                    isSelected.set(i, true);
                                    count++;
                                    title.setText("Members Selected " + count);
                                } else if (!isChecked && isSelected.get(i)) {
                                    isSelected.set(i, false);
                                    count--;
                                    title.setText("Members Selected " + count);
                                }
                            }
                        }
                    });

                    if(selectionForSend){
                        if(selectedContactsToSend.contains(contactsIdList.get(i))){
                            viewHolderRt.checkBox.setChecked(true);
                        }else{
                            viewHolderRt.checkBox.setChecked(false);
                        }
                    }else{
                        if (isSelected.get(i)){
                            viewHolderRt.checkBox.setChecked(true);
                        }else{

                            viewHolderRt.checkBox.setChecked(false);
                        }
                    }

                    viewHolderRt.ly_group.setVisibility(View.GONE);
                    viewHolderRt.ly_contact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!isSelectionActive && !selectionForSend){
                                Intent intent = new Intent(GroupMembers.this , ContactsInformation.class);
                                intent.putExtra("contact",contactsInfoModel.get(i));
                                startActivity(intent);
                            }else if(isSelectionActive){
                                if (isSelected.get(i)) {
                                    viewHolderRt.checkBox.setChecked(false);
                                } else {
                                    viewHolderRt.checkBox.setChecked(true);
                                }
                            }else{
                                if(selectedContactsToSend.contains(contactsIdList.get(i))){
                                    viewHolderRt.checkBox.setChecked(false);
                                }
                                else{
                                    viewHolderRt.checkBox.setChecked(true);
                                }
                            }
                        }
                    });

                    viewHolderRt.ly_contact.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            isSelectionActive = true;
                            isSelected.set(i,true);
                            count++;
                            adapter.notifyDataSetChanged();
                            return true;
                        }
                    });
                    viewHolderRt.contact_name.setText(contactsNameList.get(i));
                    viewHolderRt.tvCircle.setText(String.valueOf(contactsNameList.get(i).toUpperCase().charAt(0)));
                    GradientDrawable magnitudeCircle = (GradientDrawable) viewHolderRt.tvCircle.getBackground();

                    // Get the appropriate background color based on the current earthquake magnitude
                    int magnitudeColor = getMagnitudeColor(Math.random() * 10);

                    // Set the color on the magnitude circle
                    magnitudeCircle.setColor(magnitudeColor);

                }


            }

            @Override
            public int getItemCount() {
                return contactsNameList.size();
            }
        };
        recyclerView.setAdapter(adapter);
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

    private void getContactsOfGroups(String querry) {
        Cursor cursor = database.getData(querry);
        if (cursor != null && cursor.getCount() > 1) {
            isNoMember = false;
            if(!isFirstTimeRunning){
                toolbarMenu.findItem(R.id.nav_search_selected_contacts_group_members).setVisible(true);
            }
            while (cursor.moveToNext()) {
                String id = cursor.getString(2);
                String type = cursor.getString(3);
                if (!id.equals("null") && type.equals("Member")) {
                    nonEmptyGroupLy.setVisibility(View.VISIBLE);
                    emptyGroupLy.setVisibility(View.GONE);
                    getContactsInfo(id,type);
                } else if (!id.equals("null") && type.equals("Group")) {
                    nonEmptyGroupLy.setVisibility(View.VISIBLE);
                    emptyGroupLy.setVisibility(View.GONE);
                    contactType.add(type);
                    contactsIdList.add(id);
                    contactsNameList.add(id); //array for searching contacts
                    totalGroupMembers.add(getTotalGroupMembers(id));
                    contactsInfoModel.add(null);
                    isSelected.add(false);
                }
            }
        } else { if(!isFirstTimeRunning){
            toolbarMenu.findItem(R.id.nav_search_selected_contacts_group_members).setVisible(false);
        }
            nonEmptyGroupLy.setVisibility(View.GONE);
            emptyGroupLy.setVisibility(View.VISIBLE);

            btnAddMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(GroupMembers.this, ContactsSelect.class);
                    startActivityForResult(intent, 1);
                }
            });
        }
    }
    private void getContactsOfGroupsToSend(String groupName){
        Cursor cursor = database.getData("Select * from " + TABLE_NAME + " WHERE Group_Title = '" + groupName + "'");
        if (cursor != null && cursor.getCount() > 1) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(2);
                String type = cursor.getString(3);
                if (!id.equals("null") && type.equals("Member")) {
                    memberModelsToSend.add(getContactInfoToSend(id));
                } else if (!id.equals("null") && type.equals("Group")) {
                    getContactsOfGroupsToSend(id);
                }
            }
        }
    }
    private void getContactsInfo(String contactId,String type) {
        String currentContactId = contactId;
        Cursor cur = this.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{currentContactId}, null);
        if (cur.moveToNext()) {
            String currentName = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            contactsIdList.add(currentContactId);
            contactType.add(type);
            contactsNameList.add(currentName);
            MemberModel temp = new MemberModel(Integer.parseInt(currentContactId),currentName , null , "default");
            contactsInfoModel.add(temp);
            totalGroupMembers.add(null);
            isSelected.add(false);
        }else{
            database.deleteData(groupTitle,currentContactId,type);
        }
        cur.close();

    }
    private MemberModel getContactInfoToSend(String contactId){
        MemberModel tempModel = null ;
        Cursor cur = this.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{contactId}, null);
        if(cur.moveToNext()){
            String name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            tempModel = new MemberModel(Integer.parseInt(contactId),name,number ,"default");

        }
        cur.close();
        return tempModel;
    }

    private String getTotalGroupMembers(String groupTitle){
        Cursor cur = database.getData("Select * from " + TABLE_NAME + " WHERE Group_Title = '" + groupTitle + "'");
        String members =Integer.toString(cur.getCount()-1);
        cur.close();
        return members;
    }

    private void refreshList(String groupTitle) {
        contactsIdList.clear();
        contactsNameList.clear();
        selectedContactsId.clear();
        isSelected.clear();
        contactType.clear();
        totalGroupMembers.clear();
        contactsInfoModel.clear();
        getContactsOfGroups("Select * from " + TABLE_NAME + " WHERE Group_Title = '" + groupTitle + "'");
        adapter.notifyDataSetChanged();

        //toolbar related
        groupChanged = true;
        isSelectionActive = false;
        count=0;
        title.setText(groupTitle);
    }

    private ArrayList<String> getFilteredNotificationsArrayList(ArrayList<String> tempList, String queryText) {
        ArrayList<String> filteredArrayList = new ArrayList<>();
        queryText = queryText.toLowerCase();
        for (String currentName : tempList) {
            String title = currentName.toLowerCase();
            if (title.contains(queryText)) {
                filteredArrayList.add(currentName);
            }
        }
        return filteredArrayList;
    }

    private class ViewHolderRt extends RecyclerView.ViewHolder {
        TextView contact_name, totalMembers ,tvCircle;
        LinearLayout ly_contact, ly_group, ly_checkbox;
        CheckBox checkBox;

        public ViewHolderRt(@NonNull View itemView) {
            super(itemView);
            tvCircle = itemView.findViewById(R.id.tv_contact_or_groups_circle);
            ly_checkbox = itemView.findViewById(R.id.ly_checkbox_contact_or_groups);
            checkBox = itemView.findViewById(R.id.checkBox_contact_or_groups);
            ly_contact = itemView.findViewById(R.id.ly_contact_contact_or_groups);
            ly_group = itemView.findViewById(R.id.ly_group_contact_or_groups);
            totalMembers = itemView.findViewById(R.id.tv_no_of_members_groups);
            contact_name = itemView.findViewById(R.id.tv_contactname_contact_or_groups);
        }
    }
}
