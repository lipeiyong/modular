package com.komlin.libcommon.util.android.gps;

import android.Manifest;
import android.app.Activity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import java.util.List;

/**
 * @author lipeiyong
 * @date on 2018/8/28 上午9:38
 */
public class LifeLocationListener implements LifecycleObserver {
    private final Context context;
    private final LocationCallBack locationCallBack;
    private LocationManager locationManager;

    public LifeLocationListener(Context context) {
        this(context, null);
    }

    private LifeLocationListener(Context context, LocationCallBack locationCallBack) {
        this.context = context;
        this.locationCallBack = locationCallBack;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_START)
    void onStart() {
        List<String> providers = locationManager.getProviders(true);
        String locationProvider;
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            new AlertDialog.Builder(context)
                    .setTitle("开启位置服务")
                    .setMessage("请开启定位服务，应用需确认您当前位置是否处于学校范围")
                    .setPositiveButton("前往设置", (dialog, which) -> {
                        Intent i = new Intent();
                        i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(i);
                    })
                    .setNegativeButton("暂不开启", (dialog, which) -> {
                        if (context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    })
                    .show();

            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "权限缺失", Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, Looper.getMainLooper());
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_STOP)
    void onStop() {
        locationManager.removeUpdates(locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "权限缺失", Toast.LENGTH_LONG).show();
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            updateLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            updateLocation(null);
        }

        @Override
        public void onLocationChanged(Location location) {
            updateLocation(location);
        }
    };


    private Location location = null;

    public Location getLocation() {
        return location;
    }

    private void updateLocation(Location location) {
        this.location = location;
        if (null != locationCallBack) {
            locationCallBack.onLocationChange(location);
        }
    }

    interface LocationCallBack {

        void onLocationChange(Location location);

    }

}
