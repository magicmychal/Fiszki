package eu.qm.fiszki;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mBoiler on 04.04.2017.
 */

public class NightModeController {

    private static final String NIGHTMODE_STATUS = "nightmode_status";
    private SharedPreferences mNightModePreferences;
    private SharedPreferences.Editor mNightModeEditor;
    private Activity mActivity;

    public NightModeController(Activity activity) {
        mActivity = activity;
        mNightModePreferences = activity.getSharedPreferences(NIGHTMODE_STATUS, Context.MODE_PRIVATE);
        mNightModeEditor = mNightModePreferences.edit();
    }

    public void on(){
        mNightModeEditor.putInt(NIGHTMODE_STATUS, 1).apply();
    }

    public void off(){
        mNightModeEditor.putInt(NIGHTMODE_STATUS, 0).apply();
    }

    public int getStatus(){
        return  mNightModePreferences.getInt(NIGHTMODE_STATUS, 0);
    }

    public void useTheme(){
        if(getStatus()==1){
            mActivity.setTheme(R.style.NightMode);
        }else{
            mActivity.setTheme(R.style.AppTheme);
        }
    }
}
