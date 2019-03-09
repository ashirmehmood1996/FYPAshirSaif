package com.android.example.fypnotify.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.R;
import com.android.example.fypnotify.interfaces.ContactsListener;


import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    ArrayList<MemberModel> memberModelArrayList;
    ContactsListener contactsListener;

    public ContactsAdapter(ArrayList<MemberModel> memberModelArrayList, ContactsListener contactsListener) {
        this.memberModelArrayList = memberModelArrayList;
        this.contactsListener = contactsListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.contacts_list_item,
                        viewGroup,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        final MemberModel currentMember = memberModelArrayList.get(i);

        String name = currentMember.getName();
        String number = currentMember.getPhoneNumber();

        myViewHolder.nameTextView.setText(name);
        myViewHolder.numberTextView.setText(number);
        if (currentMember.isSelected()) { // to keep the state o the selected items when notify data set changed is called
            myViewHolder.checkBox.setChecked(true);
        } else {
            myViewHolder.checkBox.setChecked(false);
        }

        myViewHolder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!myViewHolder.checkBox.isChecked()) {
                    currentMember.setSelected(true);
                    myViewHolder.checkBox.setChecked(true);
                    contactsListener.selectContact(i);
                } else {
                    currentMember.setSelected(false);
                    myViewHolder.checkBox.setChecked(false);
                    contactsListener.unSelectContact(i);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return memberModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, numberTextView;
        LinearLayout mainContainer;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_contatc_li_name);
            numberTextView = itemView.findViewById(R.id.tv_contatc_li_number);
            mainContainer = itemView.findViewById(R.id.ll_contact_li_container);
            checkBox = itemView.findViewById(R.id.cb_contact_li);
        }
    }
}
