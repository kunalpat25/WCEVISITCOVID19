package com.wce.wcevisitcovid19.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.wce.wcevisitcovid19.MainActivity;
import com.wce.wcevisitcovid19.NormalUserActivity;
import com.wce.wcevisitcovid19.R;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        String data =intent.getStringExtra("data");
        if("notify".equals(data))
        {
            SharedPreferences preferences = context.getSharedPreferences("WCEVISITCOVID19", 0);
            String status = preferences.getString("dailyAssessmentStatus",null);
            if("notFilled".equals(status)) {
                createNotificationChannel(context);
                sendNotification(context, "Please fill daily assessment form");

                //TODO: replace value with exact interval
                long afterFifteenMinutes = 15*60*1000; //for testing should be 8 * 1000
                scheduleAlarm(afterFifteenMinutes, "again", PendingIntent.FLAG_ONE_SHOT, context);
            }
        }
        else if("stop".equals(data))
        {
            //change dailyAssessmentFilled status to filled
            SharedPreferences preferences = context.getSharedPreferences("WCEVISITCOVID19", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("dailyAssessmentStatus", "notFilled");
            editor.apply();
            editor.commit();
        }
    }

    public static void scheduleAlarm(long afterThisTime,String data,int flag,Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("data", data);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, flag);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        afterThisTime = System.currentTimeMillis() + afterThisTime;

        if("notify".equals(data))
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 10);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND,0);
            long assessmentFillingTime = calendar.getTimeInMillis();
            if (System.currentTimeMillis() > assessmentFillingTime) {
                assessmentFillingTime = System.currentTimeMillis();
            }
            //TODO: replace interval value to INTERVAL_DAY
            //for testing interval = 60 * 1000;
            long interval = AlarmManager.INTERVAL_DAY;
            //TODO: remove this line
//            startUpTime = afterThisTime;
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    assessmentFillingTime, interval, pendingIntent);

        }
        else if("again".equals(data))
        {
            Intent alarmIntent1 = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("data", "notify");
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 1, alarmIntent1, PendingIntent.FLAG_ONE_SHOT);
            //notify after every fifteen minutes until user fills daily assessment form
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,afterThisTime,pendingIntent1);
            }
            else
                alarmManager.set(AlarmManager.RTC_WAKEUP,afterThisTime,pendingIntent1);
        }
        else if("stop".equals(data))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        afterThisTime, pendingIntent);
            else
                alarmManager.setExact
                        (AlarmManager.RTC_WAKEUP,
                                afterThisTime, pendingIntent);
        }

        Toast.makeText(context, "Alarm set in " + afterThisTime + " seconds", Toast.LENGTH_LONG).show();
    }

    private void createNotificationChannel(Context context)
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "WCE";
            String description = "WCE Rakshanopay channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(context.getString(R.string.default_notification_channel_id), context.getString(R.string.default_notification_channel_name), importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500, 200, 500, 200, 500, 200, 500, 200, 500});
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void sendNotification(Context context, String messageBody) {
        Intent intent = new Intent(context, NormalUserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, context.getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(messageBody)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 200, 500, 200, 500, 200, 500, 200, 500, 200, 500})
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        Toast.makeText(context, "Alarm now called..", Toast.LENGTH_LONG).show();
    }
}