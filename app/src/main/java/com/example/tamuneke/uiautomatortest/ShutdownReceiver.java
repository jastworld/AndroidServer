package com.example.tamuneke.uiautomatortest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by TAmuneke on 7/3/2017.
 */

public class ShutdownReceiver extends BroadcastReceiver {
    private String abortApp;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN))
        {
            // DO WHATEVER YOU NEED TO DO HERE
            setAbortMsg("device shutting down");
            Toast.makeText(context, "closing app", Toast.LENGTH_LONG).show();

        }
    }

    public String getAbortMsg(){
        return abortApp;
    }

    public void setAbortMsg(String send){
        abortApp=send;
    }

}