package com.vigil.codechallenge.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rvigil on 5/17/2019.
 */

public class CityData {

    private final String TAG = this.getClass().getName();
    private final String SPFILE = "RVCODECHALLENGEL";
    private final String TAG_DATA = "CITYDATA";

    private static CityData sInstance;
    private Context mContext;
    private ArrayList<City> mItems;

    public static CityData getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new CityData(context);
        }
        return sInstance;
    }

    public CityData(Context context){
        mContext=context;
        loadData();
    }

    public void loadData() {
        SharedPreferences sp=mContext.getSharedPreferences(SPFILE,Context.MODE_PRIVATE);
        String data=sp.getString(TAG_DATA,"");
        mItems=new ArrayList<City>();
        if(data!=""){
            try {
                JSONArray arritems = new JSONArray(data);
                for(int x=0; x<arritems.length(); x++){
                    JSONObject obj=arritems.getJSONObject(x);
                    City city=new City(obj);
                    mItems.add(city);
                }
            } catch (Exception ex){
                Log.d(TAG,ex.toString());
            }
        }



    }

    public City getItem(int pos){
        return mItems.get(pos);
    }

    public void addItem(City city){
        mItems.add(city);
        this.saveData();
    }
    public void updateItem(City city, int pos){
        mItems.set(pos,city);
        this.saveData();
    }
    public void remove(int index){
        mItems.remove(index);
        this.saveData();
    }
    public void saveData() {
        String str="[";
        for(int x=0; x<mItems.size(); x++){
            if(x>0)
                str += ",";
            str += mItems.get(x).toString();
        }
        str += "]";
        SharedPreferences sp=mContext.getSharedPreferences(SPFILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor ed= sp.edit();
        ed.putString(TAG_DATA, str);
        ed.commit();
    }

    public ArrayList<City> getItems(){
        return mItems;

    }
}
