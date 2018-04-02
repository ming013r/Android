package com.example.ming.androidversionjudge;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        int sdkVersion = Build.VERSION.SDK_INT;
        tv =(TextView)findViewById(R.id.tv1);
        tv.setText(Build.VERSION.RELEASE);
    }
}
