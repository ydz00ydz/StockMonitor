package ydz.stockmonitor;

import android.app.Application;
import android.content.Context;

/**
 * Created by YDZ on 2017/10/18.
 */

public class MyApplication extends Application {

    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
