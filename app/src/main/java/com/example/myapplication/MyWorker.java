package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.model.LocationModel;
import com.example.myapplication.utils.Preferences;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyWorker extends Worker {

    private static final String TAG = "HH";

    public static final String CHANNEL_ID = "khfjshfkshfks";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * The current location.
     */
    private Location mLocation;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    private Context mContext;
    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    ArrayList<LocationModel> locationDataList;

    Preferences preferences;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        preferences = new Preferences(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: Done");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                Location location = locationResult.getLastLocation();
                Log.d(TAG, "onLocationResult: " + location);
                super.onLocationResult(locationResult);
            }
        };

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            mFusedLocationClient
                    .getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                                Log.d(TAG, "Location at : " + Calendar.getInstance().getTime() + "  " + mLocation);

                                preferences.setString("HH", preferences.getString("HH") + "  Time " + Calendar.getInstance().getTime() + "  " + "long : " + mLocation.getLongitude() + "   lat : " + mLocation.getLatitude() + " \n !!!");

                                createNotification(mLocation.toString(), Calendar.getInstance().getTime() + "  ");

                                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }

        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, null);
        } catch (SecurityException unlikely) {
            //Utils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }


        return Result.success();
    }

    public void createNotification(String location, String time) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(mContext)
                .setContentTitle("time : " + time)
                .setContentText("location : " + location).setSmallIcon(R.drawable.common_google_signin_btn_text_dark_focused)
                .setContentIntent(pIntent)
                .addAction(R.drawable.common_google_signin_btn_icon_dark, "Call", pIntent)
                .addAction(R.drawable.common_google_signin_btn_icon_dark_normal, "More", pIntent)
                .addAction(R.drawable.common_google_signin_btn_icon_disabled, "And more", pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

        Log.d(TAG, "FROM PREFERENCES: " + preferences.getString("HH"));

    }





}
