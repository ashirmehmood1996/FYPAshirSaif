package com.android.example.fypnotify.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.R;

import java.util.ArrayList;

public class Contacts extends Fragment {

    ArrayList<String> contactsNamesList, contactIDList;

    View rootView;
    TextView totalContacts;
    Database database;


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
        contactIDList = new ArrayList<>();
        contactsNamesList = new ArrayList<>();
    }

    private void recyclerView() {
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        RecyclerView.Adapter<ViewHolderRt> adapter = new RecyclerView.Adapter<ViewHolderRt>() {
            @NonNull
            @Override
            public ViewHolderRt onCreateViewHolder(@NonNull ViewGroup viewGroup, int ViewType) {
                return new ViewHolderRt(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.displaycontacts_list_blueprint, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolderRt viewHolderRt, final int i) {
                viewHolderRt.ly_display_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            MemberModel model = new MemberModel(Integer.parseInt(contactIDList.get(i)), contactsNamesList.get(i),null,"default");
                            Intent intent = new Intent(rootView.getContext() , ContactsInformation.class);
                            intent.putExtra("contact",model);
                            startActivity(intent);
                        }

                });
                viewHolderRt.contact_name.setText(contactsNamesList.get(i));

                viewHolderRt.tvCircle.setText(String.valueOf(contactsNamesList.get(i).toUpperCase().charAt(0)));
                GradientDrawable magnitudeCircle = (GradientDrawable) viewHolderRt.tvCircle.getBackground();

                // Get the appropriate background color based on the current earthquake magnitude
                int magnitudeColor = getMagnitudeColor(Math.random() * 10);

                // Set the color on the magnitude circle
                magnitudeCircle.setColor(magnitudeColor);

            }


            @Override
            public int getItemCount() {
                return contactsNamesList.size();
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void getContactsInfo() {
        Cursor phoneCursor = rootView.getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        while (phoneCursor.moveToNext()) {
            // checking if the contact is not already present in the list
            String name = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if (!contactsNamesList.contains(name)) {
                String currentContactId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                contactIDList.add(currentContactId);
                contactsNamesList.add(name);
            }


        }
        phoneCursor.close();
        recyclerView();
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



    private class ViewHolderRt extends RecyclerView.ViewHolder {
        TextView contact_name, tvCircle ;
        LinearLayout ly_display_contact , ly_checkbox;
        CheckBox checkBox;

        public ViewHolderRt(@NonNull View itemView) {
            super(itemView);
            ly_display_contact = itemView.findViewById(R.id.ly_display_contacts);
            ly_checkbox = itemView.findViewById(R.id.ly_checkbox_contact_or_groups);
            checkBox = itemView.findViewById(R.id.display_contact_checkBox);
            tvCircle = itemView.findViewById(R.id.tv_display_contact_circle);
            contact_name = itemView.findViewById(R.id.tv_display_contact_contactname);


        }
    }




}


