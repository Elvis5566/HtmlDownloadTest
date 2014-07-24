package com.example.elvislee.htmldownloadtest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by elvislee on 7/21/14.
 */
public class DownloadService extends Service{
    static final int MAX_THREADS = 4;
    ExecutorService mExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        mExecutor = Executors.newFixedThreadPool(MAX_THREADS) ;
    }

    public static Intent makeIntent(Context context,
                                    Handler handler,
                                    String uri) {

        return DownloadUtils.makeMessengerIntent(context, DownloadService.class, handler, uri);
    }

    @Override
    public int onStartCommand(final Intent intent,
                              int flags,
                              int startId) {
        Runnable downloadRunnable = new Runnable() {
            public void run() {
                Messenger messenger = (Messenger) intent.getExtras().get(DownloadUtils.MESSENGER_KEY);
                DownloadUtils.downloadAndRespond(DownloadService.this, intent.getData(), messenger);
            }
        };

        mExecutor.execute(downloadRunnable);

        // Tell the Android framework how to behave if this service is
        // interrupted.  In our case, we want to restart the service
        // then re-deliver the intent so that all files are eventually
        // downloaded.
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        // Ensure that the threads used by the ThreadPoolExecutor
        // complete and are reclaimed by the system.

        mExecutor.shutdown();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
