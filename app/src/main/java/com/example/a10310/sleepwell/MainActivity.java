package com.example.a10310.sleepwell;

import android.bluetooth.*;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    protected static BluetoothDevice targetDevice = null;
    protected static FloatingActionButton fab;
    protected static BluetoothGattService target_Service;
    protected static BluetoothGattCharacteristic target_Char;
    protected static BluetoothGattDescriptor descriptor;
    protected static Boolean flag = false;
    private ImageView mUpText;
    private ImageView mDownText;
    private MyDatabaseOpenHelper dbHelper;
    protected static Double toSend;
    protected static Intent intent = new Intent();
    protected ScanCallback leScanCallvack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BluetoothDevice device = result.getDevice();
                    Log.d("blt","!" + device.getName() + "!");
                    if(device.getName() != null && device.getName().equals("GH01-D900B689")) {
                        Log.d("blt","!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        MainActivity.this.targetDevice = device;
                    }
                }
            });
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
    protected BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                mBlueGatt.discoverServices();
            }
            else
                Snackbar.make(fab, "连接失败！请检查蓝牙设置", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                target_Service = mBlueGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                target_Char = target_Service.getCharacteristic(UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb"));
                fab.setImageResource(R.drawable.bo2);
                Snackbar.make(fab, "连接成功！", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                flag = true;
                Log.e("AAAAA","1:BluetoothGattService UUID=:" + target_Service.getUuid());
                Log.e("a","2:   BluetoothGattCharacteristic UUID=:" + target_Char.getUuid());
            } else {
                Snackbar.make(fab, "获取蓝牙服务失败！请检查蓝牙设置", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }


        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e("AAAAA","right!");
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            Log.e("hahahahahhahaha!!!!!!!!!!!!", "读取成功" +characteristic.getValue());
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            toSend = Double.parseDouble(new String(characteristic.getValue()));
            intent.setAction(Config.BC_ONE_ACTION);
            intent.putExtra("msg",toSend);
            sendBroadcast(intent);
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }
    };
    protected static BluetoothLeScanner scanner;
    protected static BluetoothGatt mBlueGatt;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        flag = false;

        dbHelper = new MyDatabaseOpenHelper(this,"Sleep.db",null,1);
        dbHelper.getWritableDatabase();

        mUpText = findViewById(R.id.myUpView);
        mUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                if(spf.getString("edit_text_preference_1", "请输入年龄").equals("请输入年龄") ||
                        spf.getString("edit_text_preference_2", "请输入身高").equals("请输入身高")) {
                    Toast.makeText(MainActivity.this, "请先设置年龄、身高", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, SleepActivity.class);
                startActivity(intent);
//                if(target_Char != null) {
//                    Intent intent = new Intent(MainActivity.this, SleepActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(MainActivity.this, "请先连接蓝牙", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        mDownText = findViewById(R.id.myDownView);
        mDownText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                if(spf.getString("edit_text_preference_1", "请输入年龄").equals("请输入年龄") ||
                        spf.getString("edit_text_preference_2", "请输入身高").equals("请输入身高")) {
                    Toast.makeText(MainActivity.this, "请先设置年龄、身高", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(target_Char != null) {
                    Intent intent = new Intent(MainActivity.this, WeightActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "请先连接蓝牙", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,0);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "请稍后，蓝牙连接中...", Toast.LENGTH_LONG).show();
                //scanLeDevice();

                scanner = mBluetoothAdapter.getBluetoothLeScanner();
                // Stops scanning after a pre-defined scan period.
                // 预先定义停止蓝牙扫描的时间（因为蓝牙扫描需要消耗较多的电量）
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanner.stopScan(leScanCallvack);
                        if(targetDevice != null) {
                            mBlueGatt = targetDevice.connectGatt(MainActivity.this, false, callback);
                        }
                        else {
                            Snackbar.make(fab, "连接失败！请检查蓝牙设置", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }, 2000);
                // 定义一个回调接口供扫描结束处理
                scanner.startScan(leScanCallvack);

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this,"无蓝牙权限",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("hahaha","dsdfasdfdsf");
        Intent i = new Intent(this, SettingActivity.class);
        startActivity(i);
        return true;
    }

}

class Config {
    public static final String BC_ONE_ACTION = "com.example.testbroadcasetwo.bcone";
}
