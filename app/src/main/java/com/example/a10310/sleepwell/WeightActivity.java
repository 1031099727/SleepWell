package com.example.a10310.sleepwell;

import android.bluetooth.*;
import android.content.*;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.UUID;

import static com.example.a10310.sleepwell.MainActivity.mBlueGatt;
import static com.example.a10310.sleepwell.MainActivity.target_Char;

public class WeightActivity extends AppCompatActivity {

    protected TextView mText;
    protected TextView mBmiText;
    protected TextView mLastText;
    private double get = 0.0;
    private int age;
    private double high;
    private double bmi;
    MyReiceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);
        mText = findViewById(R.id.weight_text);
        mBmiText = findViewById(R.id.bmiText);
        mLastText = findViewById(R.id.Text);
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
        age = Integer.parseInt(spf.getString("edit_text_preference_1", "0"));
        high = Double.parseDouble(spf.getString("edit_text_preference_2", "175")) / 100.0;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.BC_ONE_ACTION);
        broadcastReceiver = new MyReiceiver();
        registerReceiver(broadcastReceiver, filter);

        setSend();


    }

    protected static void setSend() {
        //开启监听
        MainActivity.mBlueGatt.setCharacteristicNotification(target_Char, true);
        MainActivity.descriptor = target_Char
                .getDescriptor(UUID
                        .fromString
                                ("00002902-0000-1000-8000-00805f9b34fb"));
        MainActivity.descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

     class MyReiceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            get = intent.getExtras().getDouble("msg");
            if(get >= 5.0) {
                bmi = get / high / high;
                mText.setText(String.valueOf(intent.getExtras().getDouble("msg")) + "KG");
                mBmiText.setText("当前BMI指数:" + String.format("%.2f", bmi));
                if (bmi < 18.5) {
                    mLastText.setTextColor(Color.YELLOW);
                    mLastText.setText("偏瘦");
                } else if (bmi <= 23.9) {
                    mLastText.setTextColor(Color.GREEN);
                    mLastText.setText("正常");
                } else {
                    mLastText.setTextColor(Color.RED);
                    mLastText.setText("超重");
                }
            }
        }
    }
}
