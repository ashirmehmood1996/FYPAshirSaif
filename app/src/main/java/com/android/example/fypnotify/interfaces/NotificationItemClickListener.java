package com.android.example.fypnotify.interfaces;

import com.android.example.fypnotify.Models.NotificationModel;

public interface NotificationItemClickListener {
    void onNotificationItemClick(int position, NotificationModel notificationModel);
    void onNotificationItemLongClick(int position);
}
