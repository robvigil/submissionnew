package com.vigil.codechallenge.Util;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.content.Context;



/**
 * Created by rob on 5/14/19.
 */

public class Networking {

/*
    private static Networking sInstance;
    private Context mContext;

    private String TAG = this.getClass().getName();

    private RequestQueue mRequestQueue;

    private static final String API_URL="http://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY = "ca19120fd5bf7745312daf4623cb8dbc"

    public static Networking getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new Networking();
        }
        return sInstance;
    }


    public Networking(Context context){
        mRequestQueue = Volley.newRequestQueue(context);

    }
    public void getWeatherByZip(String zip,Response.Listener<JSONObject> listiner,Response){
        JSONObject obj;
        this.sendNetworkRquest(API_URL + "?zip=" + zip + ",us&units=imperial");


    }

    public JSONObject getWeatherByCoords(double lat, double lng) {
        JSONObject obj;

        return obj;


    }

    private void sendNetworkRquest(String url) {
        url=url + "&appid=" + API_KEY;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){

                    }
                }
        );

        // Add JsonObjectRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest);


    }

    */
}
