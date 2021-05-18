package com.example.rtktvapiservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

import androidx.annotation.NonNull;

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


    Handler mainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        // Handler of incoming messages from clients.
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            try {
                if(msg.what == MSG_SHOW_SUM){
                    msg.getData();
                    Bundle bundle = msg.getData();
                    int x = bundle.getInt(KEY_X, 0);
                    int y = bundle.getInt(KEY_Y, 0);
                    int total = calculateSum(x, y);
                    Toast.makeText(getApplicationContext(), "sum:" + total, Toast.LENGTH_SHORT).show();
                    publishResults(total);
                }
                return  true;
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return false;
        }

        private void publishResults(int total) {
            Intent intent = new Intent(NOTIFICATION);
            intent.putExtra(KEY_TOTAL, total);
            sendBroadcast(intent);
        }
    });

    /* When binding to the service, we return an interface to our messenger for sending messages to the service. */
    @Override
    public IBinder onBind(Intent intent) {
//        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        mMessenger = new Messenger(mainHandler);
        return mMessenger.getBinder();
    }

    public static int calculateSum(int x, int y){
        return x + y;
    }

}