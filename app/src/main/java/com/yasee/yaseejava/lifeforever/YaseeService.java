package com.yasee.yaseejava.lifeforever;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.yasee.yaseejava.R;

/**
 * 前台服务,控制服务尽可能不被杀死
 * @desc 使用JobScheduler、BroadcastReceiver ? 控制保活??
 * */
public class YaseeService extends Service {
    private static final String CHANNEL_ID = "Yasee";

    public YaseeService() { }

    @Override
    public void onCreate() {

        // 创建并启动HandlerThread
        _handlerThread = new HandlerThread("BleHandlerThread");
        _handlerThread.start();
        _handler = new Handler(_handlerThread.getLooper());

        // 在新线程中执行BLE相关操作
        _handler.post(new Runnable() {
            @Override
            public void run() {

            }
        });


        createNotificationChannel();
        startForegroundService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // 停止HandlerThread
        _handlerThread.quitSafely();
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        // 当任务被移除时调用
        Intent restartServiceIntent = new Intent(getApplicationContext(), YaseeService.class);
        restartServiceIntent.setPackage(getPackageName());
        startForegroundService(restartServiceIntent);
    }

    private HandlerThread _handlerThread;
    private Handler _handler;
    private void startForegroundService() {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);


        RemoteViews _rv = new RemoteViews(getPackageName(), R.layout.notify_view);

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
//                .setContentTitle("Yasee Service")
//                .setContentText("百合医服务正在运行中,为您的测量保驾护航")
                .setOngoing(false)
                .setCategory(Notification.CATEGORY_STATUS)
                .setSmallIcon(R.drawable.notify_icons)
                .setCustomContentView(_rv)
//                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Yasee Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}