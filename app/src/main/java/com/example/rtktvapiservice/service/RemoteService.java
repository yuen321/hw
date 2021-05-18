package com.example.rtktvapiservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;

import com.example.rtktvapiservice.IRemoteService;

public class RemoteService extends Service {
    public static final String PACKAGE_NAME = "com.example.rtktvapiservice.service";
    public static final String SERVICE_NAME = "service.calc";

    //aidl interface implementation
    private final IRemoteService.Stub binder = new IRemoteService.Stub() {
        @Override
        public int getPid() {
            return Process.myPid();
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            //Do nothing
        }

        @Override
        public int addNumbers(int x, int y) {
            return x + y;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //return the interface
        return binder;
    }

}