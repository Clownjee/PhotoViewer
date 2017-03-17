package com.clownjee.photoviewer.app;

import android.app.Application;
import android.content.Context;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

/**
 * Created by Clownjee on 2017/3/15.
 */
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        CustomActivityOnCrash.install(this);
    }

    public static Context getContext() {
        return mContext;
    }
}
