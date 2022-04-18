package com.example.golfapp;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * This class handles the multi threading of the recommendation request so that users can continue
 * to use the app while waiting on the response
 */

public class RecommendationAPI {

    private static RecommendationAPI instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private RecommendationAPI(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized RecommendationAPI getInstance(Context context) {
        if (instance == null) {
            instance = new RecommendationAPI(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
