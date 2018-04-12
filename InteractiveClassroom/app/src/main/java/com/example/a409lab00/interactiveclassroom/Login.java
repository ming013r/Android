package com.example.a409lab00.interactiveclassroom;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    LiteAdapter helper;
    private BluetoothAdapter mBluetoothAdapter;
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    BluetoothManager bluetoothManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("LIPS互動問答APP-"+ConfigFile.version+"版");

        Button btn =(Button)findViewById(R.id.btn_login);
        helper = new LiteAdapter(this);
        setUser();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    200);
        }


        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 1);


        }



        if(ConfigFile.enableBLE)
        {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        }


        //btnclock
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

        loginClick();

            }
        });


        final Button btn_autologin=(Button)findViewById(R.id.btn_loginauto);
        btn_autologin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                EditText email=(EditText)findViewById(R.id.email);
                Button btnn=(Button)findViewById(R.id.btn_loginauto);
                EditText pswd=(EditText)findViewById(R.id.pswd);
                // btnn.setText("UselessButton");
                email.setText("teacher@gmail.com");
                pswd.setText("swater0");
            }
        });

    }

    public BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            int startByte = 2;
            boolean patternFound = false;
            // 寻找ibeacon
            while (startByte <= 5) {

                if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 &&
                        ((int) scanRecord[startByte + 3] & 0xff) == 0x15) {
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound) {
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);
                int major =(scanRecord[startByte + 20] & 0xff) * 0x100
                        + (scanRecord[startByte + 21] & 0xff);


                Log.d("Major:",major+"");
                //在掃描過程中，判斷是否有掃到
                if(major==65534
                        ||major==123)
                {
                    loginClick();
                }

            }
        }
    };
    void loginClick()
    {

        EditText email=(EditText)findViewById(R.id.email);
        String Email=email.getText().toString();
        EditText pswd=(EditText)findViewById(R.id.pswd);
        String password=pswd.getText().toString();


        CheckBox rememberMe =(CheckBox)findViewById(R.id.remem);
        if(rememberMe.isChecked())
            helper.updateUser(Email,password);
        else
            helper.delete(Email);
        WebApi webapi=new WebApi();


        String token=webapi.POST("AccountApi/getToken","email="+Email+"&pswd="+password);

        if(token.length()==8)
        {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);

            Intent it =new Intent();
            it.setClass(Login.this,CourseEntry.class);
            it.putExtra("token",token);
            startActivity(it);
        }
        else if(token.equals("Error"))
        {
            Toast.makeText(Login.this,"帳號或密碼輸入錯誤! 請於網站版本中檢查",Toast.LENGTH_SHORT).show();
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        else
        {
            Toast.makeText(Login.this, token , Toast.LENGTH_SHORT).show();
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }
    private static String bytesToHex(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    void setUser()
    {
        if(helper.NameList().size()!=0)
        {
            String info=helper.getData();
            String[] userInfo = info.split(",");
            EditText email=(EditText)findViewById(R.id.email);
            EditText pswd=(EditText)findViewById(R.id.pswd);

            email.setText(userInfo[0]);
            pswd.setText(userInfo[1]);
            CheckBox rememberMe =(CheckBox)findViewById(R.id.remem);
            rememberMe.setChecked(true);
        }
    }

}
