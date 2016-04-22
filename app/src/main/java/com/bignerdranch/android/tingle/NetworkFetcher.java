package com.bignerdranch.android.tingle;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkFetcher {

    private static final String TAG = "NetworkFetcher";
    private static final String API_KEY = "3acf04d2d2ae779b6471a8ff9efa0356";

    public byte[] getProductInfo(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new IOException(connection.getResponseMessage() + ": width " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0 )
            {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();

        }
    finally { connection.disconnect(); }
    }

    public String getProductString(String urlSpec) throws IOException {
        return new String(getProductInfo(urlSpec));
    }

    public String fetchItems(String barcode) {
        try
        {
            String url = Uri.parse("https://api.outpan.com/v2/products/" + barcode + "?apikey=" + API_KEY)
                    .buildUpon()
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getProductString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            String thingName = jsonBody.getString("name");
            return thingName;
        }
        catch (JSONException je)
        {
            Log.e(TAG, "Failed to parse JSON", je);
            return null;
        }
        catch (IOException ioe)
        {
            Log.e(TAG, "Failed to fetch items", ioe);
            return null;
        }
    }



}
