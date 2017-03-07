package com.example.wu.start;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_login);
    }
}
