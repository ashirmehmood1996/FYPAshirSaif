package com.android.example.fypnotify.Activities;

import android.app.Dialog;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.fypnotify.R;

public class FracgmentsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Groups groups;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_saif);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        final Dialog dialog = new Dialog(this);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

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
                                    database.getData("");
                                    Boolean result = database.insertData(title.getText().toString(), "null", "MainGroup");
                                    if (result) {
                                        Toast.makeText(getBaseContext(), "Group created", Toast.LENGTH_SHORT).show();
                                        groups.getDataBaseData();
                                        dialog.dismiss();
                                    } else
                                        Toast.makeText(getBaseContext(), "Opps something went wrong !", Toast.LENGTH_SHORT).show();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_saif, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
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