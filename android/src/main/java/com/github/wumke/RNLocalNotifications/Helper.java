package com.github.wumke.RNLocalNotifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class Helper {

    static Uri parseUri(String deepLink) {

        Uri uri = null;

        try {
            uri = Uri.parse(deepLink);
        } catch (Exception ex) {
        }

        return uri;
    }

    static void notify(
            Context context,
            NotificationCompat.Builder mBuilder,
            Integer id
    ) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        mBuilder.setChannelId(context.getPackageName() + ".reactnativelocalnotifications");
        NotificationChannel channel = new NotificationChannel(
                context.getPackageName() + ".reactnativelocalnotifications",
                "Local Scheduled Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setLightColor(Color.RED);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(id, mBuilder.build());
    }

    static Long parseTimestampString(String s) {

        Long result = null;

        try {
            result = Long.parseLong(s);
        } catch (Exception ex) {

        }

        return result;
    }
}
