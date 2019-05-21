package com.vigil.codechallenge.Data;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by rvigil on 5/17/2019.
 * Main DTO - City with last read temps and other info
 */

public class City {

    public static String TAG_ISZIP = "isZip";
    public static String TAG_CITYNAME = "cityName";
    public static String TAG_ID = "id";
    public static String TAG_ZIPCODE = "zipCode";
    public static String TAG_LAT = "lat";
    public static String TAG_LONG = "long";
    public static String TAG_LASTTEMP = "lastTemp";
    public static String TAG_LASTTEMPMIN = "lastTempMain";
    public static String TAG_LASTTEMPMAX = "lastTempMax";
    public static String TAG_LASTCHECKED = "lastChecked";

    private String TAG = this.getClass().getName();

    public boolean isZip;
    public String cityName;
    public String zipCode;
    public double lat;
    public double lng;
    public float lastTemp;
    public float lastTempMin;
    public float lastTempMax;

    public int id;
    public long lastChecked;


    /*
    {
        "coord":{"lon":-115.23,"lat":36.02},
        "weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04d"}],
        "base":"stations",
        "main":{"temp":293.88,"pressure":1014,"humidity":24,"temp_min":290.93,"temp_max":296.48},
        "visibility":16093,"wind":{"speed":2.1},
        "clouds":{"all":75},
        "dt":1558200944,
        "sys":{"type":1,"id":4664,"message":0.0087,"country":"US","sunrise":1558182784,"sunset":1558233712},
        "id":420025270,
        "name":"Las Vegas",
        "cod":200
    }
    */
    //  Constructor for data received from API
    public City(JSONObject obj, String zip){
        this.id=-1;

        try {
            this.isZip = (zip != "");
            this.zipCode = zip;
            this.lat = obj.getJSONObject("coord").getDouble("lat");
            this.lng = obj.getJSONObject("coord").getDouble("lon");
            this.lastTemp = (float) obj.getJSONObject("main").getDouble("temp");
            this.lastChecked = System.currentTimeMillis() / 1000L;
            this.cityName = obj.getString("name");
            this.id = obj.getInt("id");
            this.lastTempMin = (float) obj.getJSONObject("main").getDouble("temp_min");
            this.lastTempMax = (float) obj.getJSONObject("main").getDouble("temp_max");
        } catch (Exception ex){
            Log.d(TAG,ex.toString());

        }

    }

    //  Constructor using device stored JSON Object

    public City(JSONObject obj){
        this.id=-1;

        try {


            this.isZip = obj.getBoolean(TAG_ISZIP);
            this.zipCode = obj.getString(TAG_ZIPCODE);
            this.lat = obj.getDouble(TAG_LAT);
            this.lng = obj.getDouble(TAG_LONG);
            this.lastTemp = (float) obj.getDouble(TAG_LASTTEMP);
            this.lastChecked = obj.getLong(TAG_LASTCHECKED);
            this.cityName = obj.getString(TAG_CITYNAME);
            this.id = obj.getInt(TAG_ID);
            this.lastTempMin = (float) obj.getDouble(TAG_LASTTEMPMIN);
            this.lastTempMax = (float) obj.getDouble(TAG_LASTTEMPMAX);
        } catch (Exception ex){
            Log.d(TAG,ex.toString());

        }
    }
    public String toString(){
        JSONObject obj=new JSONObject();
        String retstr="";
        try {
            obj.put(TAG_ISZIP, this.isZip);
            obj.put(TAG_CITYNAME, this.cityName);
            obj.put(TAG_ZIPCODE, this.zipCode);
            obj.put(TAG_LAT, this.lat);
            obj.put(TAG_LONG, this.lng);
            obj.put(TAG_LASTTEMP, this.lastTemp);
            obj.put(TAG_LASTTEMPMIN, this.lastTempMin);
            obj.put(TAG_LASTTEMPMAX, this.lastTempMax);
            obj.put(TAG_LASTCHECKED, this.lastChecked);
            retstr=obj.toString();

        } catch(Exception ex) {
            Log.d(TAG,ex.toString());
        }
        return retstr;




    }

    public String getWeather(){
        return "Temperature is " + this.lastTemp + " degrees.  ";
    }




}
