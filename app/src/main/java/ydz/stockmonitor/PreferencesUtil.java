package ydz.stockmonitor;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static okhttp3.internal.Internal.instance;

/**
 * Created by YDZ on 2016/12/19.
 */

public class PreferencesUtil {

    private static SharedPreferences prefs;
    private static PreferencesUtil instance;

    private PreferencesUtil(){
    }

    public static PreferencesUtil getInstance() {
        if (instance == null) {
            instance = new PreferencesUtil();
            prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        }
        return instance;
    }

    public int getFetchDealy(){
        int fetchDelay=Integer.valueOf(prefs.getString("fetch_delay","10"))*1000;
        return fetchDelay;
    }

    public float getWarnChg(){
        float warnChg=Float.valueOf(prefs.getString("warn_chg","1.5"));
        return warnChg;
    }

    public boolean ifHintTone(){
        boolean hintTone=Boolean.valueOf(prefs.getBoolean("hint_tone",false));
        return hintTone;
    }

}