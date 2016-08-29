package com.example.torjus.intentexample2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Torjus on 2016-07-25.
 */
public class CheckConnectivity {

    EditText ipAddress;
    ProgressDialog progress;
    String result = "";
    private Context context;
    private Activity activity;
    private final Handler handler;


    public CheckConnectivity(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        progress = new ProgressDialog(context);
        ipAddress  = (EditText)this.activity.findViewById(R.id.ipaddressinput);
        handler = new Handler(context.getMainLooper());
    }

    public String checkConnectivityToServer(final String ipAddressInputFromUser) {
        progress.setTitle("Connecting");
        progress.setMessage("Please wait while connecting...");
        //progress.setCancelable(false);
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                InputStream inputStream = null;
                try {
                    URL url = new URL("http://" + ipAddressInputFromUser + ":8080/rest/test/get/checkconnectivity");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(4000);

                    connection.connect();
                    inputStream = connection.getInputStream();
                    result = StringUtility.convertInputStreamToString(inputStream);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if("Connected".equals(result)) {
                                ipAddress.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                                progress.dismiss();
                                createStatusToast("Connected");
                            } else {
                                ipAddress.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                                result = "";
                                progress.dismiss();
                                createStatusToast("Hvorfor er jeg her?");
                            }
                        }
                    });

                    Log.i("TAG",result);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (SocketTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleTimeout();
                        }
                    });

                    Log.i("TAG","Unable to connect");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Looper.loop();
            }

            private void runOnUiThread(Runnable runnable) {
                handler.post(runnable);
            }

        }).start();
        return result;
    }

    private void handleTimeout() {
        ipAddress.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        progress.dismiss();
        Toast.makeText(context, "Connection to server timed out", Toast.LENGTH_SHORT).show();
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
