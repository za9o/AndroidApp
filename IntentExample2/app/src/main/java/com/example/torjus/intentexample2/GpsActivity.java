package com.example.torjus.intentexample2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import android.widget.Button;

/**
 * Created by Torjus on 2016-07-23.
 */
public class GpsActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_INTERNET = 1;
    LocationManager locationManager;
    double longitudeGPS, latitudeGPS;
    TextView longitudeValueGPS, latitudeValueGPS;
    String[] permissionRequest = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    String ipAddressInputFromUser;
    int inputIntGpsid;
    Button btn;

    private int[] permission = {0};


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.gps_activity);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        longitudeValueGPS = (TextView) findViewById(R.id.longitudeValueGPS);
        latitudeValueGPS = (TextView) findViewById(R.id.latitudeValueGPS);
        Bundle extras = getIntent().getExtras();
        inputIntGpsid = extras.getInt("gpsid");
        btn = (Button) findViewById(R.id.activateGPSButton);
        ipAddressInputFromUser = extras.getString("ipAddressInputFromUser");

        toggleGPSUpdates();
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        try {
            locationManager.removeUpdates(locationListenerGPS);
        } catch (SecurityException e) {
            Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
        }
        finish();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void toggleGPSUpdates() {
        if (!checkLocation())
            return;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                        ActivityCompat.requestPermissions(this, permissionRequest, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                Toast.makeText(GpsActivity.this, "Inside the else statement, how do i get here?", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, permissionRequest, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }

        } else {
            Toast.makeText(GpsActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
            //return;
        }
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueGPS.setText(longitudeGPS + "");
                    latitudeValueGPS.setText(latitudeGPS + "");
                    Toast.makeText(GpsActivity.this, "GPS Provider update", Toast.LENGTH_SHORT).show();
                    writeJSON(latitudeGPS,longitudeGPS);
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public void deactivateGPS(View view) {
        try {
            btn.setText("Activate GPS");
            btn.setEnabled(true);
            locationManager.removeUpdates(locationListenerGPS);
        } catch (SecurityException e) {
            Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
        }
    }
    public void activateGPS(View view) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                        ActivityCompat.requestPermissions(this, permissionRequest, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            btn.setEnabled(false);
            btn.setText("Searching for GPS...");
            latitudeValueGPS.setText("0.000000");
            longitudeValueGPS.setText("0.00000");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListenerGPS);
        }
    }

/*    public void activateNetwork() {
//Trenger jeg denne?
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {
                ActivityCompat.requestPermissions(this, permissionRequest, MY_PERMISSIONS_REQUEST_ACCESS_INTERNET);
            }
        } else {
          //TODO
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(GpsActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GpsActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    public void writeJSON(double latitudeFromGPS, double longitudeFromGPS) {
        JSONObject object = new JSONObject();
        try {

            object.put("latitude", latitudeFromGPS);
            object.put("longitude", longitudeFromGPS);
            object.put("gpsid",inputIntGpsid);
            postRequest(object);
            //excutePost("http://192.168.1.2:8080/rest/test/json/teams/setgpsposition", object);

        } catch (JSONException e) {
            e.printStackTrace();
        }
/*
        Toast.makeText(GpsActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
*/
        System.out.println(object);
    }

    public void postRequest(final JSONObject postOject) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL("http://" + ipAddressInputFromUser + ":8080/rest/test/json/teams/setgpsposition");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //Set to POST
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                    connection.setReadTimeout(10000);
/*                    Writer writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(postOject.toString());
                    writer.flush();
                    writer.close();*/
                    connection.connect();
                    DataOutputStream wr = new DataOutputStream(
                            connection.getOutputStream ());
                    wr.writeBytes(postOject.toString());
                    wr.flush();
                    wr.close ();

                    InputStream is;
                    int response = connection.getResponseCode();
                    if (response >= 200 && response <=399){
                        //return is = connection.getInputStream();
                        //return true;
                    } else {
                        //return is = connection.getErrorStream();
                        //return false;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("Tag", e.toString());
                    e.printStackTrace();
                    //Toast.makeText(GpsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    public static Boolean excutePost2(final String targetURL, final JSONObject jsonParam)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            connection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            connection.connect();

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream ());
            wr.writeBytes(jsonParam.toString());
            wr.flush();
            wr.close ();

            InputStream is;
            int response = connection.getResponseCode();
            if (response >= 200 && response <=399){
                //return is = connection.getInputStream();
                //return true;
            } else {
                //return is = connection.getErrorStream();
                //return false;
            }


        } catch (Exception e) {
            Log.e("Tag", e.toString());
            e.printStackTrace();
            //return false;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }

            }
        }).start();
        return false;
    }
}
