package com.omelchenkoaleks.gps;

import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tvEnabledGPS;
    TextView tvStatusGPS;
    TextView tvLocationGPS;
    TextView tvEnabledNet;
    TextView tvStatusNet;
    TextView tvLocationNet;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEnabledGPS = findViewById(R.id.tvEnabledGPS);
        tvStatusGPS = findViewById(R.id.tvStatusGPS);
        tvLocationGPS = findViewById(R.id.tvLocationGPS);
        tvEnabledNet = findViewById(R.id.tvEnabledGPS);
        tvStatusNet = findViewById(R.id.tvStatusNet);
        tvLocationNet = findViewById(R.id.tvLocationNet);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 20, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000 * 20, 0, locationListener);
        // обновляем информацию о включенности провайдера
        checkEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        // новые данные места положения
        @Override
        public void onLocationChanged(Location location) {
            // свой метод, который на экране отобразит место положения
            showLocation(location);
        }

        // изменился статус указанного провайдера
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            // выводим новый статус на экран
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
            }
        }

        // указанный провайдер был включен юзером
        @SuppressLint("MissingPermission")
        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            // запрашиваем последнее место положения отключенного провайдера
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        // указанный провайдер был отключен юзером
        @Override
        public void onProviderDisabled(String provider) {
            // на экране отобразит текущий статус провайдера
            checkEnabled();
        }
    };

    // отображает координаты в соответсвующем текстовом поле
    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            tvLocationGPS.setText(formatLocation(location));
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            tvLocationNet.setText(formatLocation(location));
        }
    }

    // форматируем из данных location строку
    private String formatLocation(Location location) {
        if (location == null)
            return "";
        // какие данные? широта, долгота, время определения
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    // определяем включены или выключены провайдеры и отображаем на экране
    private void checkEnabled() {
        tvEnabledGPS.setText("Enabled: "
                + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        tvLocationNet.setText("Enabled: "
                + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    // метод открывает настройки, чтобы пользователь мог включить или выключить провайдер
    public void onClickLocationSettings(View view) {
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
}
