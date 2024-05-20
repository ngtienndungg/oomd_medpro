package com.example.clinic_appointment.utilities;

import android.app.Application;

import com.google.gson.Gson;

public class App extends Application {
    private static App mSelf;
    private Gson mGSon;

    public static App getSelf() {
        return mSelf;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSelf = this;
        mGSon = new Gson();
    }

    public Gson getGSon() {
        return mGSon;
    }
}
