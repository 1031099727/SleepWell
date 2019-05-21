package com.example.a10310.sleepwell;

import android.app.Service;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.Arrays;

public class BackService extends Service {
    private UpdateUIBroadcastReceiver broadcastReceiver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.BC_ONE_ACTION);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
    }


    class UpdateUIBroadcastReceiver extends BroadcastReceiver {
        private int count = 0;
        private int cycle = 0;
        private double get_max = 0.0;
        private double get_min = 200.0;
        private long lasttime;
        private long totaltime = 0;
        private Boolean tag = false;
        private double get;
        private MyDatabaseOpenHelper myDatabaseOpenHelper = new MyDatabaseOpenHelper(BackService.this,"Sleep.db",null,1);
        @Override
        public void onReceive(Context context, Intent intent) {
            get = intent.getExtras().getDouble("msg");
            Log.e("test",String.valueOf(get));
            if(get < 5.0) {
                count++;
                if(count >= 25) {
                    if(tag) {
                        totaltime = totaltime + System.currentTimeMillis() - lasttime;
                    }
                    SQLiteDatabase db = myDatabaseOpenHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("tag", 2);
                    values.put("getupdate",System.currentTimeMillis());
                    values.put("deep",totaltime);
                    db.update("sleep", values, "tag = ?", new String[] { "1" });
                    Intent i = new Intent(context, BackService.class);
                    stopService(i);
                }
            } else {
                cycle = (cycle + 1) % 10;
                count = 0;
                if(get_max < get) {
                    get_max = get;
                }
                if(get_min > get) {
                    get_min = get;
                }
                if(cycle == 9) {
                    if(get_max - get_min <= 0.5) {
                        if(!tag) {
                            lasttime = System.currentTimeMillis();
                            tag = true;
                        }
                    } else {
                        if(tag) {
                            totaltime = totaltime + System.currentTimeMillis() - lasttime;
                            tag = false;
                        }
                    }
                    get_max = 0.0;
                    get_min = 200.0;
                }
            }
        }

    }
}
