package com.android.example.fypnotify.Activities;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.example.fypnotify.R;

import java.util.ArrayList;

import static com.android.example.fypnotify.Activities.Database.TABLE_NAME;


public class Groups extends Fragment {
    View rootView;
    Database database;
    ArrayList<String> groupsTitleList, groupsIDList;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.groups, container, false);
        initialize();
        getDataBaseData();
        return rootView;
    }


    private class ViewHolderd extends RecyclerView.ViewHolder {
        TextView groupName, totalMembers;
        LinearLayout mainLayout;

        public ViewHolderd(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.ly_group);
            totalMembers = itemView.findViewById(R.id.tv_no_of_members);
            groupName = itemView.findViewById(R.id.tv_group_name);

        }
    }

    private void initialize() {
        groupsIDList = new ArrayList<>();
        groupsTitleList = new ArrayList<>();
        database = new Database(rootView.getContext());

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
                Cursor cursor = database.getData(" Select * from " + TABLE_NAME + " WHERE Group_Title = ' " + groupsTitleList.get(i) + " ' ");
                viewHolder.totalMembers.setText(cursor.getCount() + "");
                viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), GroupMembers1.class);
                        intent.putExtra("title", groupsTitleList.get(i));
                        startActivity(intent);
                    }
                });

                viewHolder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Database database = new Database(rootView.getContext());

                        deleteSubGroup(groupsTitleList.get(i));
                        database.deleteGroup(groupsTitleList.get(i), "null");

                        getDataBaseData();
                        return true;
                    }
                });
                ((ViewHolderd) viewHolder).groupName.setText(groupsTitleList.get(i));
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


            @Override
            public int getItemCount() {
                return groupsTitleList.size();
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public void getDataBaseData() {
        groupsIDList.clear();
        groupsTitleList.clear();
        Cursor cursor = database.getData(" Select * from " + TABLE_NAME + " WHERE TYPE = 'MainGroup' ");
        while (cursor.moveToNext()) {
            String title = cursor.getString(1);
            if (!groupsTitleList.contains(title)) {
                groupsTitleList.add(title);
                groupsIDList.add(cursor.getString(2));
            }
        }
        recyleView();
    }


}
