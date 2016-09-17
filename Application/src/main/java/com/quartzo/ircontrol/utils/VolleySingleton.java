package com.quartzo.ircontrol.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.quartzo.ircontrol.BuildConfig;

/**
 * Created by victoraldir on 13/09/2016.
 */
public class VolleySingleton {

    private static VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String TAG = "myIrcontrolLog";

    private VolleySingleton(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized VolleySingleton getVolleySingleton(Context context) {
        if (volleySingleton == null) {
            volleySingleton = new VolleySingleton(context);
        }
        return volleySingleton;
    }

    public RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {

        if(BuildConfig.DEBUG){
            Log.d(TAG,"VolleySingleton.addToRequestQueue: requesting URL " + req.getUrl());
        }

        getRequestQueue().add(req);

    }
}
