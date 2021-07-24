package com.example.aditmail.fumida.Settings;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.aditmail.fumida.Activities.TampilanMenuUtama;
import com.example.aditmail.fumida.R;

import java.util.Calendar;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.reactivex.annotations.NonNull;

public class MyWorker extends Worker {

    //public static final String LOCATION_LANGITUDE = "location_langitude";
   // public static final String LOCATION_LONGITUDE = "location_longitude";

    public static final String NAMA_PEGAWAI = "nama_pegawai";
    public static final String CHECK_WHICH_SERVICES_TO_RUN = "which_services";
    public static final String CHECK_ALARM = "check_alarm_5minutes";

    protected Context context;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters){
        super(context, workerParameters);
    }

    @NonNull
    @Override
    public Result doWork(){
        context = getApplicationContext();

        String nama_pegawai = getInputData().getString(NAMA_PEGAWAI);
        String check_which_services = getInputData().getString(CHECK_WHICH_SERVICES_TO_RUN);

        if(check_which_services!=null) {
            if (check_which_services.equalsIgnoreCase("Services_at_six")) {
                displayNotification("Fumida", "Mari Kembali ke Kantor, " + nama_pegawai);
                context.startService((new Intent(context, MyServices.class)));
            } else if (check_which_services.equalsIgnoreCase("Services_at_eight")){
                displayNotification("Fumida", "Mari Memulai dengan Berdoa dan Bekerja, " + nama_pegawai);
                context.startService((new Intent(context, MyServices_Eight.class)));
            } else{
                Log.e("Tag", CHECK_ALARM);
                serviceAtSixClock();
                serviceAtEight();
                displayNotification("Fumida", "Mari Giat Berdoa dan Bekerja, " + nama_pegawai);
            }
        }

        return Result.SUCCESS;
    }

    private void displayNotification(String title, String task){
        NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Fumida_Apps", "Fumida_Apps", NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent intent  = new Intent(context, TampilanMenuUtama.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "Fumida_Apps")
                .setContentTitle(title)
                .setContentTitle(task)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.logo_fumida_icon)
                .setDefaults(Notification.PRIORITY_HIGH)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 500, 400, 300, 200, 400})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(1, notification.build());
        }
    }

    private void serviceAtSixClock(){
        Intent autoStart = new Intent (getApplicationContext(), AutoStart.class);
        boolean autoStart_Running = (PendingIntent.getBroadcast(getApplicationContext(), 1, autoStart, PendingIntent.FLAG_NO_CREATE) != null);

        if (!autoStart_Running){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, autoStart, 0);
            AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 20);
            calendar.set(Calendar.MINUTE, 30);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            if(alarmManager!= null) {
                alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                );
            }
        }
    }

    public void serviceAtEight(){
        //Log.e("tag", "test running onstart");
        Intent autoStart_Eight = new Intent (getApplicationContext(), AutoStart_Eight.class);
        boolean autoStart_Running_Eight = (PendingIntent.getBroadcast(getApplicationContext(), 2, autoStart_Eight, PendingIntent.FLAG_NO_CREATE) != null);

        if (!autoStart_Running_Eight){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 2, autoStart_Eight, 0);
            AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 20);
            calendar.set(Calendar.MINUTE, 35);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            if(alarmManager!=null) {
                alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                );
            }
        }
    }
}
