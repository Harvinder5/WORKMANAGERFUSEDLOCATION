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
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.Preferences;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyWorker extends Worker {

    private static final String TAG = "HH";

    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private Context mContext;
    private LocationCallback mLocationCallback;
    private Preferences preferences;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        preferences = new Preferences(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
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

    private void createNotification(String location, String time) {
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent, 0);
        Notification notification = new Notification.Builder(mContext)
                .setContentTitle("time : " + time)
                .setContentText("location : " + location).setSmallIcon(R.drawable.common_google_signin_btn_text_dark_focused)
                .setContentIntent(pIntent)
                .addAction(R.drawable.common_google_signin_btn_icon_dark, "Open", pIntent)
                .addAction(R.drawable.common_google_signin_btn_icon_dark_normal, "Open", pIntent)
                .addAction(R.drawable.common_google_signin_btn_icon_disabled, "And Open", pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        if (notificationManager != null) {
            notificationManager.notify(0, notification);
        }
    }

}
