package com.example.gwteam.hichat;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import connect.message;

public class MainActivity extends AppCompatActivity {
    Button logon ;
    EditText usrname;
    private Socket socket;
    private  String mName;
    private  String mPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logon = (Button)findViewById(R.id.logon);
        usrname = (EditText)findViewById(R.id.name);
        //Get Wifi service
        WifiManager wifiManager = (WifiManager) getSystemService((Context.WIFI_SERVICE));
        //Whether wifi is open
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        final String ip = intToIp(ipAddress);
        //final String ip = "192.168.209.2";
        logon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mName = usrname.getText().toString();
                SocketSingleInstance.mName = mName;
                UserLoginTask loginTask = new UserLoginTask(ip);
                loginTask.execute();
            }
        });
    }
    private String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {


        private String localIp;
        public UserLoginTask(String localIp)
        {
            this.localIp = localIp;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {

                if(mName == null) {
                    Toast.makeText(MainActivity.this, "PLEASE ENTER USERNAME", Toast.LENGTH_SHORT);
                    return false;
                }
                socket = SocketSingleInstance.getInstance();
                new ObjectOutputStream(socket.getOutputStream()).writeObject(new message(mName,localIp));
            }catch (IOException e)
            {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "NO CONNECTION", Toast.LENGTH_SHORT);
                return false;
            }


            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            /*mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }*/
            if(success)
            {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                MainActivity.this.startActivity(intent);
            }

        }

        @Override
        protected void onCancelled() {
           /* mAuthTask = null;
            showProgress(false);*/
        }

    }
}
