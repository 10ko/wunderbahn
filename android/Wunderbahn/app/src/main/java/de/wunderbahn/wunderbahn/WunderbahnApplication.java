package de.wunderbahn.wunderbahn;

import android.app.Application;

import io.relayr.RelayrSdk;

public class WunderbahnApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RelayrSdk.init(this);
    }
}
