package com.android.example.fypnotify.Activities;


import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import com.android.example.fypnotify.R;

import java.util.ArrayList;

import static com.android.example.fypnotify.Activities.Database.TABLE_NAME;


public class Groups extends Fragment {
    private View rootView;
    private LinearLayout emptyGroupSection,nonemptyGroupSection;
    private Button addGroup;
    private Database database;
    private ArrayList<String> groupsTitleList, groupsIDList ;
    private ArrayList<Boolean> isSelected ;
    private ArrayList<Integer> groupTotalMembers;
    private Menu menuOptions;
    private Boolean deleteMode = false;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.groups, container, false);
        setHasOptionsMenu(true);
        initialize();
        getDataBaseData();
        return rootView;
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_maingroups,menu);
        menuOptions = menu;
        menuOptions.findItem(R.id.nav_delete_selected_groups).setVisible(false);
        menuOptions.findItem(R.id.nav_cancel_selected_groups).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_delete_selected_groups) {
            for(int i=0; i<isSelected.size(); i++){
                if(isSelected.get(i)){
                    deleteSubGroup(groupsTitleList.get(i));
                    database.deleteGroup(groupsTitleList.get(i), "null");
                }
            }
            deleteMode = false;
            menuOptions.findItem(R.id.nav_enable_delete_options).setVisible(true);
            menuOptions.findItem(R.id.nav_delete_selected_groups).setVisible(false);
            menuOptions.findItem(R.id.nav_cancel_selected_groups).setVisible(false);
            getDataBaseData();
            return true;
        }else if(id == R.id.nav_enable_delete_options) {
            if(emptyGroupSection.getVisibility()==View.VISIBLE){
                Toast.makeText(rootView.getContext(),"No Item Found",Toast.LENGTH_SHORT).show();
            }
            else {
                menuOptions.findItem(R.id.nav_enable_delete_options).setVisible(false);
                menuOptions.findItem(R.id.nav_delete_selected_groups).setVisible(true);
                menuOptions.findItem(R.id.nav_cancel_selected_groups).setVisible(true);
                getDataBaseData();
                deleteMode = true;
            }

        }else if(id == R.id.nav_cancel_selected_groups){
            menuOptions.findItem(R.id.nav_enable_delete_options).setVisible(true);
            menuOptions.findItem(R.id.nav_delete_selected_groups).setVisible(false);
            menuOptions.findItem(R.id.nav_cancel_selected_groups).setVisible(false);
            deleteMode = false;
            getDataBaseData();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        groupsIDList = new ArrayList<>();
        groupsTitleList = new ArrayList<>();
        groupTotalMembers = new ArrayList<>();
        isSelected = new ArrayList<>();
        database = new Database(rootView.getContext());
        emptyGroupSection = rootView.findViewById(R.id.ly_empty_groups_section);
        nonemptyGroupSection = rootView.findViewById(R.id.ly_nonempty_groups_section);
        addGroup = rootView.findViewById(R.id.btn_add_group);

    }

    private void recyleView() {
        final RecyclerView recyclerView = rootView.findViewById(R.id.rv_groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        RecyclerView.Adapter<ViewHolderd> adapter = new RecyclerView.Adapter<ViewHolderd>() {
            @NonNull
            @Override
            public ViewHolderd onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new ViewHolderd(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.groups_list_blueprint, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolderd viewHolder, final int i) {

                viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked && !isSelected.get(i)) {
                            isSelected.set(i, true);
                        } else if (!isChecked && isSelected.get(i)) {
                            isSelected.set(i, false);
                        }
                    }

                });

                if (isSelected.get(i)){
                    viewHolder.checkBox.setChecked(true);
                }else{

                    viewHolder.checkBox.setChecked(false);
                }

                if(deleteMode){
                    viewHolder.ly_checkbox_mainGroup.setVisibility(View.VISIBLE);
                }


                viewHolder.totalMembers.setText(groupTotalMembers.get(i)+ "");
                viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(deleteMode){
                            if (isSelected.get(i)) {
                                viewHolder.checkBox.setChecked(false);
                            } else {
                                viewHolder.checkBox.setChecked(true);
                            }
                        }else{
                            Intent intent = new Intent(getContext(), GroupMembers.class);
                            intent.putExtra("title", groupsTitleList.get(i));
                            startActivity(intent);
                        }
                    }
                });

                viewHolder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(!deleteMode) {
                            menuOptions.findItem(R.id.nav_enable_delete_options).setVisible(false);
                            menuOptions.findItem(R.id.nav_delete_selected_groups).setVisible(true);
                            menuOptions.findItem(R.id.nav_cancel_selected_groups).setVisible(true);

                            deleteMode = true;
                            getDataBaseData();
                        }
                        return true;
                    }
                });
                ((ViewHolderd) viewHolder).groupName.setText(groupsTitleList.get(i));

                viewHolder.tvCircle.setText(String.valueOf(groupsTitleList.get(i).toUpperCase().charAt(0)));
                GradientDrawable magnitudeCircle = (GradientDrawable) viewHolder.tvCircle.getBackground();

                // Get the appropriate background color based on the current earthquake magnitude
                int magnitudeColor = getMagnitudeColor(Math.random() * 10);

                // Set the color on the magnitude circle
                magnitudeCircle.setColor(magnitudeColor);
            }


            @Override
            public int getItemCount() {
                return groupsTitleList.size();
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private class ViewHolderd extends RecyclerView.ViewHolder {
        TextView groupName, totalMembers ,tvCircle;
        LinearLayout mainLayout , ly_checkbox_mainGroup;
        CheckBox checkBox;

        public ViewHolderd(@NonNull View itemView) {
            super(itemView);
            tvCircle = itemView.findViewById(R.id.tv_groups_circle);
            mainLayout = itemView.findViewById(R.id.ly_group_contact_or_groups);
            totalMembers = itemView.findViewById(R.id.tv_no_of_members);
            groupName = itemView.findViewById(R.id.tv_group_name);
            ly_checkbox_mainGroup = itemView.findViewById(R.id.ly_checkbox_mainGroups);
            checkBox = itemView.findViewById(R.id.checkbox_mainGroups);
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
        return ContextCompat.getColor(rootView.getContext(), magnitudeColorResourceId);
    }

    private void deleteSubGroup(String groupTitle) {
        Cursor cursor = database.getData("Select * from " + TABLE_NAME + " WHERE Group_Title = '" + groupTitle + "'");

        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("Group")) {
                String title = cursor.getString(2);
                if (!title.equals("null"))
                    deleteSubGroup(title);
            }
        }
        database.deleteGroup(groupTitle, "null");

    }

    public void getDataBaseData() {
        groupsIDList.clear();
        groupsTitleList.clear();
        isSelected.clear();
        Cursor cursor = database.getData(" Select * from " + TABLE_NAME + " WHERE TYPE = 'MainGroup' ");
        boolean value = cursor.moveToNext();
        if(value){
            nonemptyGroupSection.setVisibility(View.VISIBLE);
            emptyGroupSection.setVisibility(View.GONE);
            while (value) {
                String title = cursor.getString(1);
                if (!groupsTitleList.contains(title)) {
                    groupsTitleList.add(title);
                    groupsIDList.add(cursor.getString(2));
                    isSelected.add(false);
                    Cursor cur = database.getData("Select * from " + TABLE_NAME + " WHERE Group_Title = '" + title + "'");
                    groupTotalMembers.add(cur.getCount()-1);
                    cur.close();
                }
                value = cursor.moveToNext();
            }
        }else {
            nonemptyGroupSection.setVisibility(View.GONE);
            emptyGroupSection.setVisibility(View.VISIBLE);
            final Dialog dialog = new Dialog(rootView.getContext());
            addGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                            Database database = new Database(rootView.getContext());
                            if (!title.getText().toString().equals("")) {
                                database.getData("");
                                Boolean result = database.insertData(title.getText().toString(), "null", "MainGroup");
                                if (result) {
                                    Toast.makeText(rootView.getContext(), "Group created", Toast.LENGTH_SHORT).show();
                                    getDataBaseData();
                                    dialog.dismiss();
                                } else
                                    Toast.makeText(rootView.getContext(), "Opps something went wrong !", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(rootView.getContext(), "Enter Title First !", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.show();
                }
            });
        }
        cursor.close();
        recyleView();
    }




}
