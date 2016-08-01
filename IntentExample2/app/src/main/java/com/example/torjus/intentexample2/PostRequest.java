package com.example.torjus.intentexample2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Torjus on 2016-07-27.
 */
public class PostRequest {

    private Context context;
    private Activity activity;
    private ProgressDialog progress;

    public PostRequest(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        progress = new ProgressDialog(context);

    }

    public void writeJSON(String username, String password, String ipAddressFromUser, String httpLink) {
        JSONObject object = new JSONObject();

        progress.setTitle("Handling user request");
        progress.setMessage("Please wait...");
        //progress.setCancelable(false);
        progress.show();
        try {

            object.put("username", username);
            object.put("password", password);
            Log.e("TAG", ipAddressFromUser);
            if (StringUtility.validIP(ipAddressFromUser)) {
                postRequest(object, ipAddressFromUser, httpLink);
            } else {
                Toast.makeText(context, "invalid IP input", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            createStatusToast("JSONException");
        }
        System.out.println(object);
    }

    public void postRequest(final JSONObject postOject, final String ipAddressInputFromUser, final String httpLink) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL("http://" + ipAddressInputFromUser + ":8080/rest/test/json/teams/" + httpLink);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //Set to POST
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                    connection.setConnectTimeout(4000);
                    connection.setReadTimeout(10000);
                    connection.connect();
                    DataOutputStream wr = new DataOutputStream(
                            connection.getOutputStream ());
                    wr.writeBytes(postOject.toString());
                    wr.flush();
                    wr.close();

                    if (connection.getResponseCode() == 201) {
                        InputStream is = connection.getInputStream();
                        String inputStreamresult = StringUtility.convertInputStreamToString(is);
                        //TODO Parse the input stream from JSON and send only the message to createToast
                        JSONObject inputFromServer = new JSONObject(inputStreamresult);

                        String successMessage = inputFromServer.get("successMessage").toString();
                        int successGpsid = Integer.parseInt(inputFromServer.get("inputgpsid").toString());
                        createStatusToast(successMessage);
                        Intent intent = new Intent(context, GpsActivity.class);
                        intent.putExtra("gpsid",successGpsid);
                        intent.putExtra("ipAddressInputFromUser", ipAddressInputFromUser);
                        activity.startActivity(intent);
                    }
                    else if (connection.getResponseCode() == 400) {
                        InputStream is = connection.getErrorStream();
                        final String errorstream = StringUtility.convertInputStreamToString(is);
                        JSONObject inputFromServer = new JSONObject(errorstream);
                        String errorMessage = inputFromServer.get("errorMessage").toString();
                        createStatusToast(errorMessage);
                    }
                }  catch (SocketTimeoutException e) {
                    createStatusToast("Connection timed out");
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("Tag", e.toString());
                    e.printStackTrace();
                    progress.dismiss();
                }
            }
        }).start();
    }

    private void createStatusToast(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }
}
