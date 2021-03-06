package com.example.torjus.intentexample2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends Activity {

    EditText username;
    EditText password;
    EditText ipAddress;
    CheckConnectivity checkConnectivity;
    PostRequest postRequest;
    EncryptInputToServer encryptInputToServer;
    View vi;
    RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        ipAddress = (EditText)findViewById(R.id.ipaddressinput);
        postRequest = new PostRequest(MainActivity.this, this);
        checkConnectivity = new CheckConnectivity(MainActivity.this, this);
        encryptInputToServer = new EncryptInputToServer();
        showDisclaimerAlert();
    }

    public void checkServerConnectivityFromButton(View view) {
        if (StringUtility.validIP(ipAddress.getText().toString())) {
            if ("Connected".equals(checkConnectivity.checkConnectivityToServer(ipAddress.getText().toString()))) {
            }
        } else {
            Toast.makeText(MainActivity.this, "invalid IP input", Toast.LENGTH_SHORT).show();
        }
    }

    public void Login(View view) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        if(!username.getText().toString().matches("")) {
            if (!password.getText().toString().matches("")) {
                postRequest.writeJSON(username.getText().toString(), encryptInputToServer.SHA1(password.getText().toString()), ipAddress.getText().toString(), "login");
            } else {
                Toast.makeText(MainActivity.this, "password field empty", Toast.LENGTH_SHORT).show();
            }
        }  else {
            Toast.makeText(MainActivity.this, "Username field empty", Toast.LENGTH_SHORT).show();
        }

/*                if (username.getText().toString().equals("Admin") && password.getText().toString().equals("admin")) {
                    Intent intent = new Intent(this, GpsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Wrong username and/or password", Toast.LENGTH_SHORT).show();
                }*/
    }

    private void showDisclaimerAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.disclaimer_heading)
                .setMessage(R.string.disclaimer_text)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    public void Register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }

/*    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
    //    WifiInfo wifiInfo = connMgr.getActiveNetworkInfo();
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected())
                return true;
        else
                return false;
    }*/
}
