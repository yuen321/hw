// IRemoteService.aidl
package com.example.rtktvapiservice;

// Declare any non-default types here with import statements

/** Example service interface */
interface IRemoteService {
    /** Request the process ID of this service, to do evil things with it. */
    int getPid();

    /* Demonstrates some basic types that you can use as parameters and return values in AIDL. */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);

    int addNumbers(int x, int y); //2 arguement methodd to add
}