package com.example.rtktvapiservice.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.rtktvapiservice.MainActivity;
import com.example.rtktvapiservice.R;

public class MessengerService extends Service {
    // Command to the service to display a message
    public static final int MSG_SHOW_SUM = 1;
    // KEY to retrieve bundle data
    public static final String KEY_X = "X";
    public static final String KEY_Y = "Y";
    public static final String KEY_TOTAL = "TOTAL";

    // Target publish for clients to send messages to IncomingHandler.
    Messenger mMessenger;
    public static final String NOTIFICATION = "com.example.rtktvapiservice.service";
    public final String HANDLER_THREAD_NAME = "SUM_OF_2_NUMBER";

    private IncomingHandler incomingHandler;
    private NotificationManager mNM;

    private final int NOTIFICATION_ID = 1;
    private final String NOTIFICATION_CHANNEL_ID = "1";
    private final String NOTIFICATION_CHANNEL_NAME=  "NOTIFICATION_CHANNEL_NAME";
    private final String NOTIFICATION_CHANNEL_DESC = "NOTIFICATION_CHANNEL_DESC";

    public int calculateSum(int x, int y){
        return x + y;
    }

    @Override
    public void onCreate() {
        // The service is being created
        super.onCreate();
        HandlerThread thread = new HandlerThread(HANDLER_THREAD_NAME);
        thread.start();
        // Get the HandlerThread's Looper and use it for our Handler
        incomingHandler = new IncomingHandler(thread.getLooper());

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        int total = calculateSum(intent.getIntExtra(KEY_X, 0), intent.getIntExtra(KEY_Y, 0));
        publishResults(total);
        // If we get killed, after returning from here, restart
        return START_REDELIVER_INTENT;
    }

    /* When binding to the service, we return an interface to our messenger for sending messages to the service. */
    @Override
    public IBinder onBind(Intent intent) {
        mMessenger = new Messenger(incomingHandler);
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // A client is binding to the service with bindService(),
//        return super.onUnbind(intent);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //cancel persistent notification
        mNM.cancel(R.string.service_started);
        //tell user it stopped
        Toast.makeText(this, R.string.service_stopped, Toast.LENGTH_SHORT).show();
        Log.d("svcOnDestroy",getString(R.string.service_stopped));
    }

    private void showNotification(){
        try {
            // In this sample, we'll use the same text for the ticker and the expanded notification
            CharSequence text = getText(R.string.service_started);
            // The PendingIntent to launch our activity if the user selects this notification
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), 0);

            // Set the info for the views that show in the notification panel.
            Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)  // the status icon
                    .setTicker(text)  // the status text
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentTitle(getText(R.string.service_label_local))  // the label of the entry
                    .setContentText(text)  // the contents of the entry
                    .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                    .build();
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(NOTIFICATION_CHANNEL_DESC);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }
            startForeground(NOTIFICATION_ID, notification);
        }catch (Exception e){
            Log.e("errShowNotify", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private class IncomingHandler extends Handler{
        public IncomingHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MSG_SHOW_SUM){
                msg.getData();
                Bundle bundle = msg.getData();
                int total = calculateSum(bundle.getInt(KEY_X, 0), bundle.getInt(KEY_Y, 0));
                Toast.makeText(getApplicationContext(), String.format(getApplicationContext().getString(R.string.sum), total), Toast.LENGTH_SHORT).show();
                publishResults(total);
            }else{
                super.handleMessage(msg);
            }
        }
    }

    private void publishResults(int total) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(KEY_TOTAL, total);
        sendBroadcast(intent);
    }
}