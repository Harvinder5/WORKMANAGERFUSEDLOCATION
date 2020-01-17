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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.Interfaces.iOnItemClick;
import com.example.myapplication.MyWorker;
import com.example.myapplication.R;
import com.example.myapplication.adapter.MyAdapter;
import com.example.myapplication.model.LocationModel;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.Preferences;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements iOnItemClick {

    public static final String TAG = "HH";

    ArrayList<LocationModel> locationDataList;
    Preferences preferences;
    RecyclerView recyclerView;
    Button btn_StartWorker, btn_StopWorker;
    boolean isPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initializeViews();

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                Constants.REQUEST_FINE_LOCATION);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter myAdapter = new MyAdapter(this, getData());
        recyclerView.setAdapter(myAdapter);


    }

    private void initializeViews() {
        preferences = new Preferences(this);
        btn_StartWorker= findViewById(R.id.start);
        btn_StartWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermissionGranted){
                    startLocationWorker();
                }
            }
        });
        btn_StopWorker= findViewById(R.id.stop);
        btn_StopWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkManager.getInstance().cancelAllWorkByTag(Constants.WORK_REQUEST_TAG);
                Log.d(TAG, "stop worker: is called ");
            }
        });
        recyclerView = findViewById(R.id.recyclerview);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_FINE_LOCATION: {
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

                      isPermissionGranted = true;
                        
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                Constants.REQUEST_COARSE_LOCATION);

                    }

                } else {
                    Log.d("HH", "!!!! FINE LOCATION not granted");
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            Constants.REQUEST_FINE_LOCATION);

                }
                return;
            }


            case Constants.REQUEST_COARSE_LOCATION: {

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


    private void startLocationWorker() {
        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES)
                .addTag(Constants.WORK_REQUEST_TAG)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.KEEP, periodicWork);
    }

    private ArrayList<LocationModel> getData() {
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
