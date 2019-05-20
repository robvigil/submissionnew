package com.vigil.codechallenge;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.vigil.codechallenge.Data.City;
import com.vigil.codechallenge.Data.CityData;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * An activity representing a list of Cities. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CityDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CityListActivity extends AppCompatActivity {

    private final String API_URL="http://api.openweathermap.org/data/2.5/weather";
    private final String API_KEY = "ca19120fd5bf7745312daf4623cb8dbc";
    private final String TAG = this.getClass().getName();

    private boolean mTwoPane;

    private SimpleItemRecyclerViewAdapter  mCityAdapter;
    private CityData mCityData;
    private ProgressDialog mProgressDialog;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        mContext=CityListActivity.this;
        mProgressDialog=new ProgressDialog(this);
        mCityData = CityData.getInstance(mContext);

        mProgressDialog.setMessage("Loading... Please wait.");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        Button btnAdd=(Button) findViewById(R.id.btnAdd);
        Button btnLoc=(Button) findViewById(R.id.btnLoc);

        btnLoc.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EditText editZipCode = (EditText) findViewById(R.id.editZipCode);
                String zip = editZipCode.getText().toString();
                getLocation();
            }
        });
        btnAdd.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EditText editZipCode = (EditText) findViewById(R.id.editZipCode);
                String zip = editZipCode.getText().toString();
                if(!zip.equals(""))
                    getWeather(zip,-1,-1,true,-1);
                else
                    Toast.makeText(mContext,"Please enter a zip code.",Toast.LENGTH_SHORT).show();

            }
        });

        View recyclerView = findViewById(R.id.city_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.city_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // Check Location Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }


    }

    // Swipe to delete
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        final CityData data=mCityData;
        mCityAdapter=new SimpleItemRecyclerViewAdapter(data.getItems());
        recyclerView.setAdapter(mCityAdapter);
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Item swiped.
                int pos = viewHolder.getAdapterPosition();
                mCityAdapter.removeItem(pos);
                mCityData.remove(pos);
                mCityAdapter.notifyItemRemoved(pos);
            }
        };
        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);
    }

    // Called when user clicks on saved item
    private void getWeatherFromClick(int position){
        City city= mCityData.getItem(position);
        getWeather(city.zipCode, city.lat, city.lng, false,position);
    }

    //  This handles all the calls both new city calls and calls to get current weather on existing city
    //  Zip code = "" then it relies on lat/long.
    //  pos is used as item index when clicked from list
    private void getWeather(final String zip, double lat, double lng, final boolean isNew, final int pos){
        final String url=API_URL + "?units=imperial&" + (!zip.equals("") ? "zip=" + zip + ",us" : "lat=" + lat + "&lon=" + lng) + "&appid=" + API_KEY;
        mProgressDialog.show();
        loadCity(url,new NetworkListner(){
            @Override
            public void onDataReceived(JSONObject data){
                String str=data.toString();
                mProgressDialog.hide();
                if(data.has("name")) {
                    City city = new City(data,zip);
                    if(isNew)
                        mCityData.addItem(city);

                    else {
                        City newcity = new City(data,zip);
                        mCityData.updateItem(newcity,pos);

                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putInt(CityDetailFragment.ARG_ITEM_POS, pos);
                            CityDetailFragment fragment = new CityDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.city_detail_container, fragment)
                                    .commit();
                        } else {
                            Context context = mContext;
                            Intent intent = new Intent(context, CityDetailActivity.class);
                            intent.putExtra(CityDetailFragment.ARG_ITEM_POS, pos);

                            context.startActivity(intent);
                        }
                    }
                    mCityAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "Invalid Request", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNetworkError() {
                mProgressDialog.hide();
                Toast.makeText(mContext, "Invalid location or network issue. Try again.", Toast.LENGTH_SHORT).show();
                super.onNetworkError();
            }
        });

    }

     private void getLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Check Permissions
       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        */

       try {
           Criteria criteria = new Criteria();
           String bestProvider = locationManager.getBestProvider(criteria, false);
           Location location = locationManager.getLastKnownLocation(bestProvider);
           Double lat, lon;

           try {
               lat = location.getLatitude();
               lon = location.getLongitude();
               getWeather("", lat, lon, true, -1);
               //return new LatLng(lat, lon);
           } catch (NullPointerException e) {
               e.printStackTrace();
           }
       } catch(Exception ex){
           Toast.makeText(mContext, "There was an issue getting your location.", Toast.LENGTH_SHORT).show();
      }
    }


    //  Main network call method.
    //  Would create an API helper class if more calls of different types were needed
    //  Time to complete code challenge and the fact only this class uses it, I have
    //  placed it here.
    private void loadCity(String url, final NetworkListner listener) {
        url=url + "&appid=" + API_KEY;
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                         listener.onDataReceived(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        listener.onNetworkError();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }


    // NetworkListener interface
    interface NetworkCallListener {
        void onDataReceived(JSONObject obj);
        void onNetworkError();
    }
    private class NetworkListner {
        public void onDataReceived(JSONObject obj){}
        public void onNetworkError(){}
    }

    //  Adapter Class for Recycler View
    //  Again I would seperate it in an Adapters subpackage
    //  but this is the only adapter for this app
    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        private final ArrayList<City> mValues;

        public SimpleItemRecyclerViewAdapter(ArrayList<City> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.city_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final City  city=mValues.get(position);

            holder.mItem = city;
            Date dt=new Date((long) city.lastChecked * 1000);
            String pattern = "yyyy-MM-dd HH:mm";
            SimpleDateFormat format = new SimpleDateFormat(pattern);

            String citystr=city.cityName + " - " + city.lastTemp + " degs - " + format.format(dt);
            holder.mIdView.setText(citystr);

            //  show ZIP CODE or Coordinates depending on how user added it
            String contentstr="";
            if(city.isZip)
                contentstr="ZIP: " + city.zipCode;
            else
                contentstr="Coords: " + city.lat + ", " + city.lng;
            holder.mContentView.setText(contentstr);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    getWeatherFromClick(position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void removeItem(int index){
            mValues.remove(index);
        }

        public void addItem(City city){
            mValues.add(city);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public City mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
            public void updateItem(City city){
                mItem=city;
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
