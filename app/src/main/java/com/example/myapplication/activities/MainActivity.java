package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.MyWorker;
import com.example.myapplication.R;
import com.example.myapplication.adapter.MyAdapter;
import com.example.myapplication.model.LocationModel;
import com.example.myapplication.utils.Preferences;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MyAdapter.MyInterface {
    public static final int REQUEST_FINE_LOCATION = 100;
    public static final int REQUEST_COARSE_LOCATION = 101;
    public static final String TAG = "HH";

    ArrayList<LocationModel> locationDataList;

    Preferences preferences;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = new Preferences(this);


        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter myAdapter = new MyAdapter(this, getData());
        recyclerView.setAdapter(myAdapter);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.d("HH", "fine location Permission has been granted");
                    //now ask for coarse permission
                    boolean hasCoarseLocationPermission = (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
                    if (hasCoarseLocationPermission) {
                        Log.d("HH", "access location has been granted");

                        startWorker();


                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                REQUEST_COARSE_LOCATION);

                    }

                } else {
                    Log.d("HH", "!!!! FINE LOCATION not granted");
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_FINE_LOCATION);

                }
                return;
            }


            case REQUEST_COARSE_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse permission has been granted");
                } else {
                    Log.d(TAG, "coarse permission Not Granted");
                }
                return;
            }
        }

    }


    public void startWorker() {
        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES)
                .addTag(TAG)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.KEEP, periodicWork);
    }


    public ArrayList<LocationModel> getData() {
        locationDataList = new ArrayList<>();
        String[] locationArray = preferences.getString("HH").split("!!!");
        LocationModel locationModel;
        for (String string : locationArray) {
            locationModel = new LocationModel();
            locationModel.setLat(string);
            locationDataList.add(locationModel);
        }

        return locationDataList;
    }


    @Override
    public void onItemClicked(int i) {

        Toast.makeText(this, locationDataList.get(i).getLat(), Toast.LENGTH_SHORT).show();
    }
}
