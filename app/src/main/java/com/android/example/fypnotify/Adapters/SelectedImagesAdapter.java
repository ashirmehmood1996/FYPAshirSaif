package com.android.example.fypnotify.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.example.fypnotify.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.SelectedImagesViewHolder> {
    ArrayList<Uri> uriArrayList;
    Context context;

    public SelectedImagesAdapter(ArrayList<Uri> uriArrayList, Context context) {
        this.uriArrayList = uriArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public SelectedImagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SelectedImagesViewHolder(
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.selected_images_list_item,
                                viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImagesViewHolder selectedImagesViewHolder, int i) {
        Uri curruntImageUri = uriArrayList.get(i);
        Glide.with(context).load(curruntImageUri).into(selectedImagesViewHolder.imageView);
        selectedImagesViewHolder.crossImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForRemoval(curruntImageUri,i);

            }
        });
    }

    private void askForRemoval(Uri curruntImageUri, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("remove/detach Item ?");
        builder.setMessage("click remove to complete the action");
        builder.setPositiveButton("remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uriArrayList.remove(curruntImageUri);
                notifyItemRemoved(pos);
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    class SelectedImagesViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout containerRelativeLayout;
        ImageView imageView;
        ImageButton crossImageButton;

        public SelectedImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            containerRelativeLayout = itemView.findViewById(R.id.rl_li_selected_images_container);
            imageView = itemView.findViewById(R.id.iv_li_selected_images_image);
            crossImageButton = itemView.findViewById(R.id.ib_li_selected_images_cross);
        }
    }
}
