package com.example.aditmail.fumida.Settings;

import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.aditmail.fumida.Activities.TampilanMenuUtama;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Log.e("tag", "test Running Intent");
            if (intent.getAction() != null) {
                Log.e("tag", "Get Action Means Not Null");
                if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                    //context.startService((new Intent(context, MyServices.class)));
                    TampilanMenuUtama autoStart = new TampilanMenuUtama();
                    autoStart.serviceAtSix();
                    Log.e("tag", "Service at Six After Boot");
                }
            } else {
                Log.e("tag", "Get Action Means Null");
                //context.startService(new Intent(context, MyServices.class));

                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .setRequiredNetworkType(NetworkType.METERED)
                        .build();

                Data data = new Data.Builder()
                        .putString(MyWorker.NAMA_PEGAWAI, TampilanMenuUtama.namaLengkap)
                        .putString(MyWorker.CHECK_WHICH_SERVICES_TO_RUN, "Services_at_six")
                        .build();

                //Worker
                final OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                        .setConstraints(constraints)
                        .setInputData(data)
                        .build();

                WorkManager.getInstance().enqueue(workRequest);

                Log.e("tag", "Service at Six");
            }
        }
    }
}
