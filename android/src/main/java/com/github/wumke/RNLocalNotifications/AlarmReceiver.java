package com.github.wumke.RNLocalNotifications;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!intent.getAction().equals(Constants.ACTION)) {
            return;
        }

        if (this.isAppOnForeground(context)) {
            return;
        }

        String type = intent.getExtras().getString("type", "normal");

        if (TextUtils.isEmpty(type)) {
            return;
        }

        String largeIconName = intent.getExtras().getString("largeIconName", "ic_launcher");
        String largeIconType = intent.getExtras().getString("largeIconType", "mipmap");
        String smallIconName = intent.getExtras().getString("smallIconName", "notification_small");
        String smallIconType = intent.getExtras().getString("smallIconType", "drawable");

        switch (type) {

            case Constants.TYPE_DEEP_LINK:
                handleDeepLinkAlarm(context, intent, largeIconName, largeIconType, smallIconName, smallIconType);
                break;

            case Constants.TYPE_NORMAL:
                handleNormalAlarm(context, intent, largeIconName, largeIconType, smallIconName, smallIconType);
                break;

            default:
                break;
        }

    }

    private void handleDeepLinkAlarm(
            Context context,
            Intent intent,
            String largeIconName,
            String largeIconType,
            String smallIconName,
            String smallIconType
    ) {

        Integer id = intent.getExtras().getInt("id", 1);
        String title = intent.getExtras().getString("title");
        String body = intent.getExtras().getString("body");
        String deepLink = intent.getExtras().getString("deepLink");

        Uri uri = Helper.parseUri(deepLink);

        if (uri == null) {
            return;
        }

        Intent deepLinkIntent = new Intent(
                Intent.ACTION_VIEW,
                uri
        );

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, deepLinkIntent, 0);

        NotificationCompat.Builder mBuilder = buildBaseNotification(
                context,
                title,
                body,
                smallIconName,
                smallIconType,
                largeIconName,
                largeIconType
        );

        mBuilder.setContentIntent(pendingIntent);
        Helper.notify(context, mBuilder, id);
    }

    private void handleNormalAlarm(
            Context context,
            Intent intent,
            String largeIconName,
            String largeIconType,
            String smallIconName,
            String smallIconType
    ) {

        String text = intent.getExtras().getString("text", "");
        String datetime = intent.getExtras().getString("datetime", "");
        String sound = intent.getExtras().getString("sound", "default");
        String hiddendata = intent.getExtras().getString("hiddendata", "");

        ApplicationInfo appInfo = context.getApplicationInfo();
        String appName = context.getPackageManager().getApplicationLabel(appInfo).toString();
        String packageName = context.getPackageName();

        NotificationCompat.Builder mBuilder = buildBaseNotification(
                context,
                appName,
                text,
                smallIconName,
                smallIconType,
                largeIconName,
                largeIconType
        );

        setSoundIfNeeded(context, sound, mBuilder);

        Class cl = null;
        try {
            cl = Class.forName(packageName + ".MainActivity");
        } catch (ClassNotFoundException e) {
            //TODO: if you want feedback
        }

        Intent openIntent = new Intent(context, cl);
        openIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        openIntent.putExtra("hiddendata", hiddendata);
        openIntent.putExtra("data", hiddendata);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        String shortenedDatetime = datetime.replace(":", "").replace("-", "").replace("/", "").replace("\\", "").replace(" ", "").substring(2);
        Integer mId = Integer.parseInt(shortenedDatetime);
        Helper.notify(context, mBuilder, mId);

    }

    private static NotificationCompat.Builder buildBaseNotification(
            Context context,
            String title,
            String body,
            String smallIconName,
            String smallIconType,
            String largeIconName,
            String largeIconType
    ) {

        // Set the icon, scrolling text and timestamp
        Resources res = context.getResources();

        int largeIconResId = res.getIdentifier(largeIconName, largeIconType, context.getPackageName());
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(res, largeIconResId);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(res.getIdentifier(smallIconName, smallIconType, context.getPackageName()))
                .setLargeIcon(largeIconBitmap)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setVibrate(new long[] { 0, 1000 })
        ;
    }

    private void setSoundIfNeeded(
            Context context,
            String sound,
            NotificationCompat.Builder mBuilder
    ) {

        if (sound == null || sound.equals("silence")) {
            return;
        }

        // Set alarm sound
        if (sound.equals("default")) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            return;
        }

        setCustomSound(context, sound, mBuilder);
    }

    private void setCustomSound(
            Context context,
            String soundName,
            NotificationCompat.Builder mBuilder
    ) {

        if (soundName == null) {
            return;
        }

        // sound name can be full filename, or just the resource name.
        // So the strings 'my_sound.mp3' AND 'my_sound' are accepted
        // The reason is to make the iOS and android javascript interfaces compatible
        int resId;

        if (context.getResources().getIdentifier(soundName, "raw", context.getPackageName()) != 0) {
            resId = context.getResources().getIdentifier(soundName, "raw", context.getPackageName());
        } else {
            soundName = soundName.substring(0, soundName.lastIndexOf('.'));
            resId = context.getResources().getIdentifier(soundName, "raw", context.getPackageName());
        }

        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);

        mBuilder.setSound(soundUri);
    }

    private boolean isAppOnForeground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        if (appProcesses == null) {
            return false;
        }

        final String packageName = context.getPackageName();

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {

            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    appProcess.processName.equals(packageName)
            ) {
                return true;
            }
        }

        return false;
    }
}
