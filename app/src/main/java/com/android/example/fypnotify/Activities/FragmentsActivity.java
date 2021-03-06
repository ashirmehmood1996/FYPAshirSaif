package com.android.example.fypnotify.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.fypnotify.Database.Database;
import com.android.example.fypnotify.Fragments.Contacts;
import com.android.example.fypnotify.Fragments.Groups;
import com.android.example.fypnotify.R;

import static com.android.example.fypnotify.Database.Database.TABLE_NAME;

// just added comment
public class FragmentsActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Groups groups;

    private ViewPager mViewPager;
    private Toolbar toolbar;
    private CheckBox checkBox;
    private TextView title;
    private Boolean getContact;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_saif);
        toolbar = findViewById(R.id.toolbar_select_contacts);
        setSupportActionBar(toolbar);

        initializer();
        setmViewPager();
        setFloatingActionButton();

    }

    private void initializer(){
        title = toolbar.findViewById(R.id.tv_toolbar_counter);
        getContact = getIntent().getBooleanExtra("get contact",false);
        if(getContact) {
            checkBox = toolbar.findViewById(R.id.checkBox_select_all_contacts);
            checkBox.setVisibility(View.VISIBLE);
            title.setText("Selected Contacts "+count);

        }else{
            title.setText("Contacts");
        }
    }

    private void setmViewPager(){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setFloatingActionButton(){
        final Dialog dialog = new Dialog(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mViewPager.getCurrentItem() == 0) {
                            Intent intent = new Intent(Intent.ACTION_INSERT,
                            ContactsContract.Contacts.CONTENT_URI);
                            startActivity(intent);
                } else {

                    dialog.setTitle("Create Group");
                    dialog.setContentView(R.layout.create_group_dialog);

                    final EditText title = dialog.findViewById(R.id.et_dialog_group_title);

                    TextView cancel = (TextView) dialog.findViewById(R.id.tv_dialog_cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    TextView create = dialog.findViewById(R.id.tv_dialog_create_group);
                    create.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Database database = new Database(getBaseContext());
                            if (!title.getText().toString().equals("")) {
                                Cursor cur = database.getData("Select * from " + TABLE_NAME + " WHERE Group_Title = '" + title.getText().toString() + "'");
                                if(cur.getCount()==0){
                                    Boolean result = database.insertData(title.getText().toString(), "null", "MainGroup");
                                    if (result) {
                                        Toast.makeText(getBaseContext(), "Group created", Toast.LENGTH_SHORT).show();
                                        groups.getDataBaseData();
                                        dialog.dismiss();
                                    } else
                                        Toast.makeText(getBaseContext(), "Opps something went wrong !", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getBaseContext(), "Group/Subgroup with same name exists", Toast.LENGTH_LONG).show();
                                }
                            } else
                                Toast.makeText(getBaseContext(), "Enter Title First !", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.show();

                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        menu.findItem(R.id.nav_search_selected_contacts).setVisible(false);
        if(getContact){
            menu.findItem(R.id.nav_delete_selected_contacts).setVisible(false);
            menu.findItem(R.id.nav_submit_selected_contacts).setVisible(true);
        }

        else{
            menu.findItem(R.id.nav_submit_selected_contacts).setVisible(false);
            menu.findItem(R.id.nav_delete_selected_contacts).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_submit_selected_contacts) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Contacts contacts = new Contacts();
                    return contacts;

                case 1:
                    groups = new Groups();
                    return groups;
                default:
                    return null;
            }
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Contacts";

                case 1:
                    return "Groups";

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {

            return 2;
        }
    }


}
