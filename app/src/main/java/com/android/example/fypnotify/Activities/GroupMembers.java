package com.android.example.fypnotify.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.fypnotify.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static com.android.example.fypnotify.Activities.Database.TABLE_NAME;
// FIXME: 4/23/2019 add a check to detect repetition in groups

public class GroupMembers extends AppCompatActivity {
    private ArrayList<String> contactsIdList;
    private ArrayList<String> contactsNameList;
    private ArrayList<Boolean> hasWhatsappList;
    private ArrayList<String> contactsNumberList;
    private ArrayList<String> contactsEmailList;
    private ArrayList<String> newlyAddedContactsId;
    private ArrayList<String> contactType;
    private ArrayList<String> stack;
    private RecyclerView.Adapter<GroupMembers.ViewHolderRt> adapter;
    private LinearLayout nonEmptyGroupLy;
    private LinearLayout emptyGroupLy;
    private Button btnAddMember;
    private String groupTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        initializer();
        getContactsOfGroups("Select * from " + TABLE_NAME + " WHERE Group_Title = '" + groupTitle + "'");
        recyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_group_member, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_member:

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
                                if (title.getTextSize() != 0) {
                                    Database database = new Database(getBaseContext());
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
                        intent.putExtra("get contact", true);
                        startActivityForResult(intent, 1);
                        selectDialog.dismiss();
                    }
                });
                selectDialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {


            newlyAddedContactsId = data.getStringArrayListExtra("resultArray"); //solved



            Toast.makeText(getBaseContext(), "" + newlyAddedContactsId.size(), Toast.LENGTH_SHORT).show();


            Database database = new Database(getBaseContext());
//            Boolean result = database.insertData(groupTitle, data.getStringExtra("result"), "Member");
//            if (result) {
//                Toast.makeText(getBaseContext(), "Contact Added", Toast.LENGTH_SHORT).show();
//                refreshList(groupTitle);
//            }
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


    private void initializer() {
        contactsIdList = new ArrayList<>();
        contactsNameList = new ArrayList<>();
        contactsNumberList = new ArrayList<>();
        hasWhatsappList = new ArrayList<>();
        contactsEmailList = new ArrayList<>();
        newlyAddedContactsId = new ArrayList<>();
        contactType = new ArrayList<>();
        stack = new ArrayList<>();

        Intent intent = getIntent();
        groupTitle = intent.getStringExtra("title");

        emptyGroupLy = findViewById(R.id.ly_empty_group);
        nonEmptyGroupLy = findViewById(R.id.ly_non_empty_group);

        btnAddMember = findViewById(R.id.btn_add_member_groupmember);


    }

    private void refreshList(String groupTitle) {
        contactsIdList.clear();
        contactsNameList.clear();
        contactsNumberList.clear();
        hasWhatsappList.clear();
        contactsEmailList.clear();
        contactType.clear();
        getContactsOfGroups("Select * from " + TABLE_NAME + " WHERE Group_Title = '" + groupTitle + "'");
        adapter.notifyDataSetChanged();
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

                if (contactType.get(i).equals("Group")) {
                    viewHolderRt.ly_group.setVisibility(View.VISIBLE);
                    viewHolderRt.ly_members.setVisibility(View.GONE);
                    viewHolderRt.totalMembers.setText(contactsIdList.size() + "");
                    viewHolderRt.ly_contact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            stack.add(groupTitle);
                            groupTitle = contactsIdList.get(i);
                            refreshList(contactsIdList.get(i));
                        }
                    });

                    viewHolderRt.ly_contact.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            Database database = new Database(getBaseContext());
                            database.deleteData(groupTitle, contactsIdList.get(i), contactType.get(i));
                            refreshList(groupTitle);
                            return true;
                        }
                    });
                    viewHolderRt.contact_name.setText(contactsIdList.get(i));
                    viewHolderRt.phone_number.setVisibility(View.INVISIBLE);
                    viewHolderRt.email.setVisibility(View.INVISIBLE);
                    viewHolderRt.logo.setVisibility(View.INVISIBLE);

                } else {
                    viewHolderRt.ly_group.setVisibility(View.GONE);
                    viewHolderRt.ly_members.setVisibility(View.VISIBLE);
                    viewHolderRt.ly_contact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //todo code for contacs when clicked
                        }
                    });

                    viewHolderRt.ly_contact.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            Database database = new Database(getBaseContext());
                            database.deleteData(groupTitle, contactsIdList.get(i), contactType.get(i));
                            refreshList(groupTitle);
                            return true;
                        }
                    });
                    viewHolderRt.contact_name.setText(contactsNameList.get(i));
                    viewHolderRt.phone_number.setText(contactsNumberList.get(i));
                    //       viewHolderRt.email.setText(contactsEmailList.get(i));
                    if (!contactsEmailList.get(i).equals("no email")) {
                        viewHolderRt.email.setText(contactsEmailList.get(i));
                    } else {
                        viewHolderRt.email.setVisibility(View.GONE);
                    }
                    if (hasWhatsappList.get(i)) {
                        viewHolderRt.logo.setVisibility(View.VISIBLE);
                    } else {
                        viewHolderRt.logo.setVisibility(View.GONE);
                    }
                }


            }

            @Override
            public int getItemCount() {
                return contactsIdList.size();
            }
        };
        recyclerView.setAdapter(adapter);
    }


    private class ViewHolderRt extends RecyclerView.ViewHolder {
        TextView contact_name, phone_number, email, totalMembers;
        LinearLayout ly_contact, ly_group, ly_members;
        ImageView logo;

        public ViewHolderRt(@NonNull View itemView) {
            super(itemView);
            ly_contact = itemView.findViewById(R.id.ly_contact);
            ly_group = itemView.findViewById(R.id.ly_group);
            ly_members = itemView.findViewById(R.id.ly_members);
            totalMembers = itemView.findViewById(R.id.tv_no_of_members_groups);
            contact_name = itemView.findViewById(R.id.tv_contactname);
            phone_number = itemView.findViewById(R.id.tv_phonenumber);
            email = itemView.findViewById(R.id.tv_email);
            logo = itemView.findViewById(R.id.iv_logo_whatsapp);
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

    private void getContactsOfGroups(String querry) {
        Database database = new Database(this);
        Cursor cursor = database.getData(querry);
        if (cursor != null && cursor.getCount() > 1) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(2);
                String type = cursor.getString(3);
                if (!id.equals("null") && type.equals("Member")) {
                    nonEmptyGroupLy.setVisibility(View.VISIBLE);
                    emptyGroupLy.setVisibility(View.GONE);
                    contactType.add(type);
                    contactsIdList.add(id);
                    getContactsInfo(id);
                    hasWhatsappList.add(true);
                } else if (!id.equals("null") && type.equals("Group")) {
                    nonEmptyGroupLy.setVisibility(View.VISIBLE);
                    emptyGroupLy.setVisibility(View.GONE);
                    contactType.add(type);
                    contactsIdList.add(id);
                    contactsNameList.add("null");
                    contactsNumberList.add("null");
                    contactsEmailList.add("null");
                    hasWhatsappList.add(false);
                }
            }
        } else {
            nonEmptyGroupLy.setVisibility(View.GONE);
            emptyGroupLy.setVisibility(View.VISIBLE);

            btnAddMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(GroupMembers.this, ContactsSelect.class);
                    intent.putExtra("get contact", true);
                    startActivityForResult(intent, 1);
                }
            });
        }
    }

    private void getContactsInfo(String contactId) {
        String currentContactId = contactId;
        Cursor cur = this.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{currentContactId}, null);
        if (cur.moveToNext()) {
            contactsNameList.add(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            contactsNumberList.add(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            contactsEmailList.add(getEmail(currentContactId));
            if (hasWhatsApp(currentContactId) == "yes") {
                hasWhatsappList.add(true);
            } else {
                hasWhatsappList.add(false);
            }
        }
        cur.close();

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


}
