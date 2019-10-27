package com.android.example.fypnotify.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.example.fypnotify.Activities.MainActivity;
import com.android.example.fypnotify.Models.NotificationModel;
import com.android.example.fypnotify.R;
import com.android.example.fypnotify.interfaces.NotificationItemClickListener;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationsHistoryAdapter extends RecyclerView.Adapter<NotificationsHistoryAdapter.MyViewHolder> {
    private ArrayList<NotificationModel> notificationArrayList;
    private NotificationItemClickListener notificationItemClickListener;
    private Context context;
    private String queryText = "";


    public NotificationsHistoryAdapter(ArrayList<NotificationModel> notificationArrayList, MainActivity mainActivity) {
        this.notificationArrayList = notificationArrayList;
        this.notificationItemClickListener = mainActivity;
        this.context = mainActivity;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.sent_notifications_list_item,
                        viewGroup,
                        false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final NotificationModel currentNotification = notificationArrayList.get(position);
        String title = currentNotification.getTitle();
        String message = currentNotification.getMessage();
        String timeStamp = currentNotification.getTimeStamp();
        String uriCVS = currentNotification.getUriCSV();


        holder.containerLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationItemClickListener.onNotificationItemClick(position, currentNotification);
            }
        });

        holder.containerLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                notificationItemClickListener.onNotificationItemLongClick(position);
                return true;
            }
        });


        long timeLong = Long.parseLong(timeStamp);
        String formattedDateTime = getApproprteDateTime(timeLong);
        holder.notificationTextView.setText(message + "\n\n" + uriCVS);
        holder.timeStampTextView.setText(formattedDateTime);

        if (queryText.length() > 0) {
            holder.titleTextView.setText(highlightText(queryText, title));
        } else {
            holder.titleTextView.setText(title);
        }

        if (!message.isEmpty() && message.length() >= 1) {
            String string = String.valueOf(title.toUpperCase().charAt(0));
            holder.circleTextView.setText(string);
        } else holder.circleTextView.setText("");


        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.circleTextView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(Math.random() * 10);

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

    }


    //took help from here
    //https://stackoverflow.com/questions/12818711/how-to-find-time-is-today-or-yesterday-in-android
    private String getApproprteDateTime(long timeStamp) {
        if (DateUtils.isToday(timeStamp)) {
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm aa");
            return formatter.format(new Date(timeStamp));
        } else if (isYesterday(timeStamp)) {
            SimpleDateFormat formatter = new SimpleDateFormat(" h:mm aa");
            return "yesterday,\n" + formatter.format(new Date(timeStamp));
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy,\n h:mm aa");
            return formatter.format(new Date(timeStamp));
        }
    }

    private boolean isYesterday(long timeStamp) {
        return DateUtils.isToday(timeStamp + DateUtils.DAY_IN_MILLIS); //yesterday time and plus one day will give us today so will return true
    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
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
        return ContextCompat.getColor(context, magnitudeColorResourceId);
    }

    public void setFilter(ArrayList<NotificationModel> filteredArrayList, String queryText) {

        notificationArrayList = filteredArrayList;
        this.queryText = queryText;


    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout containerLinearLayout;
        TextView notificationTextView, timeStampTextView, titleTextView, circleTextView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTextView = itemView.findViewById(R.id.tv_history_li_message);
            timeStampTextView = itemView.findViewById(R.id.tv_cl_history_li_time_stamp);
            circleTextView = itemView.findViewById(R.id.tv_history_li_circle);
            titleTextView = itemView.findViewById(R.id.tv_history_li_title);
            containerLinearLayout = itemView.findViewById(R.id.ll_history_list_item_container);
        }
    }


    //code borrowed from stack over flow link
    //https://stackoverflow.com/a/30668826/6039129

    public static CharSequence highlightText(String search, String originalText) {
        if (search != null && !search.equalsIgnoreCase("")) {
            String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
            int start = normalizedText.indexOf(search);
            if (start < 0) {
                return originalText;
            } else {
                Spannable highlighted = new SpannableString(originalText);
                while (start >= 0) {
                    int spanStart = Math.min(start, originalText.length());
                    int spanEnd = Math.min(start + search.length(), originalText.length());
                    highlighted.setSpan(new ForegroundColorSpan(Color.GREEN), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = normalizedText.indexOf(search, spanEnd);
                }
                return highlighted;
            }
        }
        return originalText;
    }
}
