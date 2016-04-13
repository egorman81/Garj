package com.eddie.garj;


        import android.app.AlarmManager;
        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.os.PowerManager;
        import android.support.v7.app.NotificationCompat;
        import android.widget.Toast;

        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;

public class Alarm extends BroadcastReceiver
{
    static final long STATUS_LIMIT=1000*60*30;//millisecs
    //FIX THIS TO USE THE PREFERNCES POLL AND LIMIT
    private GarageStatusChecker garageStatusChecker = new GarageStatusChecker();
    private GarageStatus garageStatus;
    private String status = "UNSET";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        garageStatus = garageStatusChecker.getGarageStatus();
        if(!garageStatus.getStatus().equals(GarageStatus.GARAGE_CLOSED)) {


            Calendar date = Calendar.getInstance();
            long t= date.getTimeInMillis();
            Date statusLimitDate=new Date(t - ( STATUS_LIMIT));

            Date lastChanged = garageStatus.getLastChanged();
            if(lastChanged.before(statusLimitDate)) {
                showNotification(context);
               // Toast.makeText(context, garageStatus.getStatus(), Toast.LENGTH_LONG).show(); // For example

            }
            //Toast.makeText(context, String.valueOf( t ), Toast.LENGTH_LONG).show(); // For example
        }

        //Toast.makeText(context, garageStatus.getStatus(), Toast.LENGTH_LONG).show(); // For example

        wl.release();
    }

    public void SetAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), STATUS_LIMIT, pi); // Millisec * Second * Minute
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void showNotification(Context context) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        SimpleDateFormat format1 = new SimpleDateFormat("dd MMM hh:mm");

        String formattedDate = format1.format(garageStatus.getLastChanged().getTime());

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 001, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_info_black_24dp);
        mBuilder.setContentTitle(garageStatus.getStatus());
        mBuilder.setContentText(GarageStatus.STATUS_SINCE+ formattedDate);
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }
}
