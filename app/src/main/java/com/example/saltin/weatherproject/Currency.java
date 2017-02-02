package com.example.saltin.weatherproject;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import org.json.JSONException;
import java.net.URL;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by saltin on 27.01.2017.
 */

public class Currency {

    public interface AsyncResponse {
        void processUpdate(String output1, String output2, String output3, String output4);
    }

    public static class currencyIdTask extends AsyncTask<String, Void, JSONObject> {
        public AsyncResponse delegate = null;

        public currencyIdTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonCurrency = null;
            try {
                jsonCurrency = getCurrencyJSON();
            } catch (Exception e) {
                Log.d("Error", "Cannot process JSON result", e);
            }
            return jsonCurrency;
        }


        protected void onPostExecute(JSONObject json) {

            try {
                if (json != null) {
//                  String dolarBuy = Html.fromHtml("₺ " + json.getString("dolar")).toString();
//                    DateFormat df = DateFormat.getDateTimeInstance();
                    String dolarBuy = json.getString("dolar") + " ₺";
                    String dolarSale = json.getString("dolar2") + " ₺";
                    String euroBuy = json.getString("euro") + " ₺";
                    String euroSale = json.getString("euro2") + " ₺";
//                    String updatedOn = df.format(new Date(json.getLong("guncelleme")));
                    delegate.processUpdate(dolarBuy, dolarSale, euroBuy, euroSale);
                }

            } catch (JSONException e) {
            }
        }

    }

    public static JSONObject getCurrencyJSON() {
        try {
            String strUrl = "http://www.doviz.gen.tr/doviz_json.asp";
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null) {
                json.append(tmp).append("\n");
            }
            reader.close();
            JSONObject data = new JSONObject(json.toString());
            if (data == null) {
                return null;
            }
            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
