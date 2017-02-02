package com.example.saltin.weatherproject;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updateField, timeField, dateField, dolarBuyField, dolarSaleField, euroBuyField, euroSaleField;

    Typeface weatherFont;

    private BroadcastReceiver broadcastReceiver;

    double lat, lon;

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //coordinate değişkene ata onu kullan
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");

        cityField = (TextView) findViewById(R.id.city_field);
        updateField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_tempature_field);
        humidity_field = (TextView) findViewById(R.id.humidity_field);
        pressure_field = (TextView) findViewById(R.id.pressure_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);
        timeField = (TextView) findViewById(R.id.time_field);
        dateField = (TextView) findViewById(R.id.date_field);

        dolarBuyField = (TextView) findViewById(R.id.txtDolarBuy);
        dolarSaleField = (TextView) findViewById(R.id.txtDolarSale);
        euroBuyField = (TextView) findViewById(R.id.txtEuroBuy);
        euroSaleField = (TextView) findViewById(R.id.txtEuroSale);
//        curUpField = (TextView) findViewById(R.id.txtCurUpTime);
//        RefreshCurrency();


        String weekDay;
        Calendar dt = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("E,d MMMM yyyy", Locale.US);
        TextView txtCurrentDate = (TextView) findViewById(R.id.date_field);
        weekDay = dateFormat.format(dt.getTime());
        String currentDate = (weekDay);
        txtCurrentDate.setText(currentDate);

        Thread myThread = null;
        Runnable myRunnableThread = new CountDownRunner();
        myThread = new Thread(myRunnableThread);
        myThread.start();


        Function.placeIdTask asyncTask = new Function.placeIdTask(new Function.AsyncResponse() {
            @Override
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {

                cityField.setText(weather_city);
                updateField.setText("Last Update " + weather_updatedOn);
                detailsField.setText(weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText("Humidity: " + weather_humidity);
                pressure_field.setText("Pressure: " + weather_pressure);
                weatherIcon.setText(Html.fromHtml(weather_iconText));

//                SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.US); // Günü çekme. E=Thu EEEE=Thursday
//                TextView txtCurrentDate = (TextView) findViewById(R.id.date_field);
//                weekDay = dayFormat.format(dt.getTime());
//                int day = dt.get(Calendar.DAY_OF_MONTH);
//                int month = dt.get(Calendar.MONTH) + 1;
////              int month = dt.get(Calendar.MONTH);
//                int year = dt.get(Calendar.YEAR);
//                String currentDate = (weekDay) + "," + (day < 10 ? ("0" + day) : (day)) + "." + (month < 2 ? ("0" + month) : (month)) + "." + (year);
            }
        }, lat, lon);
        asyncTask.execute("25.180000", "89.530000");
        Currency.currencyIdTask asyncCurrencyTask = new Currency.currencyIdTask(new Currency.AsyncResponse() {
            @Override
            public void processUpdate(String dolar_buy, String dolar_sale, String euro_buy, String euro_sale) {
                dolarBuyField.setText(dolar_buy);
                dolarSaleField.setText(dolar_sale);
                euroBuyField.setText(euro_buy);
                euroSaleField.setText(euro_sale);
//                curUpField.setText("Last Update " + currency_updatedOn);
            }
        });
        asyncCurrencyTask.execute();

        if (!runtime_permissions())
            enable_service();
    }

    private void enable_service() {
        Intent intent = new Intent(MainActivity.this, GPS_Service.class);
        startService(intent);
    }

    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                enable_service();
            } else {
                runtime_permissions();
            }
        }
    }

    private class CountDownRunner implements Runnable {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(1000); // Pause of 1 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }

        //        YEAR + MONTH + DAY_OF_WEEK_IN_MONTH + DAY_OF_WEEK
        public void doWork() {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        Calendar dt = Calendar.getInstance();
                        TextView txtCurrentTime = (TextView) findViewById(R.id.time_field);
                        int hours = dt.get(Calendar.HOUR_OF_DAY);
                        int minutes = dt.get(Calendar.MINUTE);
                        int seconds = dt.get(Calendar.SECOND);
                        String currentTime = (hours < 10 ? ("0" + hours) : (hours)) + ":" + (minutes < 10 ? ("0" + minutes) : (minutes)) + ":" + (seconds < 10 ? ("0" + seconds) : (seconds));
                        txtCurrentTime.setText(currentTime);
                    } catch (Exception e) {
                    }
                }
            });
        }

    }

}



