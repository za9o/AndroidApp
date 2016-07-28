package com.example.torjus.intentexample2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Torjus on 2016-07-23.
 */
public class RegisterActivity extends Activity {

    EditText username;
    EditText password;
    EditText ipAddress;
    CheckConnectivity checkConnectivity;
    PostRequest postRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        ipAddress = (EditText)findViewById(R.id.ipaddressinput);
        postRequest = new PostRequest(RegisterActivity.this, this);

        checkConnectivity = new CheckConnectivity(RegisterActivity.this, this);

    }

    public void RegisterNewUser(View view) {

        if(!username.getText().toString().matches("")) {
                if (!password.getText().toString().matches("")) {
                    postRequest.writeJSON(username.getText().toString(), password.getText().toString(), ipAddress.getText().toString(), "registernewuser");
                } else {
                Toast.makeText(RegisterActivity.this, "password field empty", Toast.LENGTH_SHORT).show();
            }
        }  else {
            Toast.makeText(RegisterActivity.this, "Username field empty", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkServerConnectivityFromButton(View view) {

        if (StringUtility.validIP(ipAddress.getText().toString())) {
            if ("Connected".equals(checkConnectivity.checkConnectivityToServer(ipAddress.getText().toString()))) {
            }
        } else {
            Toast.makeText(RegisterActivity.this, "invalid IP input", Toast.LENGTH_SHORT).show();
        }
    }
}
