package com.example.torjus.intentexample2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    EditText username;
    EditText password;
    EditText ipAddress;
    CheckConnectivity checkConnectivity;
    PostRequest postRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        ipAddress = (EditText)findViewById(R.id.ipaddressinput);
        postRequest = new PostRequest(MainActivity.this, this);
        checkConnectivity = new CheckConnectivity(MainActivity.this, this);
    }

    public void checkServerConnectivityFromButton(View view) {
        if (StringUtility.validIP(ipAddress.getText().toString())) {
            if ("Connected".equals(checkConnectivity.checkConnectivityToServer(ipAddress.getText().toString()))) {
            }
        } else {
            Toast.makeText(MainActivity.this, "invalid IP input", Toast.LENGTH_SHORT).show();
        }
    }

    public void Login(View view) {

        if(!username.getText().toString().matches("")) {
            if (!password.getText().toString().matches("")) {
                postRequest.writeJSON(username.getText().toString(), password.getText().toString(), ipAddress.getText().toString(), "login");
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
