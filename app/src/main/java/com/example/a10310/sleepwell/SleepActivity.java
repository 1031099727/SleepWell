package com.example.a10310.sleepwell;

import android.bluetooth.BluetoothGattDescriptor;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.*;
import lecho.lib.hellocharts.view.LineChartView;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.a10310.sleepwell.MainActivity.target_Char;

public class SleepActivity extends AppCompatActivity {
    private TextView mText;
    private Button mButton;
    private LineChartView lineChart;
    private MyDatabaseOpenHelper myDatabaseOpenHelper;

    String[] date;//X轴的标注
    double[] score;//图表的数据点
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);


        mText = findViewById(R.id.SleepText);
        mButton = findViewById(R.id.SleepButton);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.flag == false) {
                    Toast.makeText(SleepActivity.this,"请先在主界面连接蓝牙",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(MainActivity.descriptor == null) {
                    MainActivity.mBlueGatt.setCharacteristicNotification(target_Char, true);
                    MainActivity.descriptor = target_Char
                            .getDescriptor(UUID
                                    .fromString
                                            ("00002902-0000-1000-8000-00805f9b34fb"));
                    MainActivity.descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                }

                Toast.makeText(SleepActivity.this,"祝您好梦！",Toast.LENGTH_SHORT).show();
                SQLiteDatabase db = myDatabaseOpenHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("gotobeddate", System.currentTimeMillis());
                values.put("tag", 1);
                db.insert("sleep", null, values);
                values.clear();
                db.delete("sleep", "gotobeddate < ?", new String[] { String.valueOf(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000) });
                Intent intent = new Intent(SleepActivity.this, BackService.class);
                startService(intent);
            }
        });

        myDatabaseOpenHelper = new MyDatabaseOpenHelper(this,"Sleep.db",null,1);
        SQLiteDatabase db = myDatabaseOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("sleep", null, null, null, null, null, null);
        Log.e("test",String.valueOf(cursor.getCount()));
        if(cursor.getCount() != 0) {
            if (cursor.getCount() == 1)
                Toast.makeText(this, "只有一天的数据，无法绘制折线图", Toast.LENGTH_SHORT).show();
            Log.e("Error","test2");
            date = new String[cursor.getCount()];
            score = new double[cursor.getCount()];
            int count = -1;
            long time1;
            long time3;
            if (cursor.moveToFirst()) {
                do {
                    count ++;
                    time1 = cursor.getLong(cursor.getColumnIndex("gotobeddate"));
                    date[count] = getDate(time1);
                    time1 = (cursor.getLong(cursor.getColumnIndex("getupdate")) - time1) / 1000;
                    time3 = cursor.getLong(cursor.getColumnIndex("deep")) / 1000;
                    score[count] = ((double) time3) / time1 * 100;
                } while (cursor.moveToNext());
                cursor.moveToLast();
                time1 = cursor.getLong(cursor.getColumnIndex("gotobeddate"));
                time3 = cursor.getLong(cursor.getColumnIndex("getupdate")) - time1;
                int hour = (int)(time3 % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                int minutes = (int)(time3 % (1000 * 60 * 60))/ (1000 * 60);
                time3 = cursor.getLong(cursor.getColumnIndex("deep"));
                int hour_Deep = (int)(time3 % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                int minutes_Deep = (int)(time3 % (1000 * 60 * 60))/ (1000 * 60);


                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
                int age = Integer.parseInt(spf.getString("edit_text_preference_1", "20"));
                float requir = (float) (21 - 6 * Math.pow(age,0.3) + age / 10.0);
                float grade = (float) (100 - 100 * Math.abs(requir - (hour + minutes / 60.0)) / requir) / 3;
                if (score[cursor.getCount() - 1] >= 25) {
                    grade = grade + (float) 33.33;
                }
                else {
                    grade = (float) (grade + (100 - 4 * (25 - score[cursor.getCount() - 1])) /3);
                }
                int getBedHour = Integer.parseInt(getHour(time1));
                switch (getBedHour) {
                    case 0:
                        grade = grade + (float) (33.33 * 0.6);
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        grade = grade + (float) (33.33 * 0.3);
                        break;
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                        grade = grade + (float) 33.33;
                        break;
                    case 20:
                    case 21:
                    case 22:
                        grade = grade + (float)33.33;
                        break;
                    case 23:
                        grade = grade + (float) (33.33 * 0.8);
                        break;

                }
                mText.setText("您在" + getDate(time1) + "晚上一共休息了: " + String.valueOf(hour)
                        + "小时" + String.valueOf(minutes) + "分，其中，深度睡眠时长为" + String.valueOf(hour_Deep)
                        + "小时" + String.valueOf(minutes_Deep) + "分，占总睡眠时长的"
                        + String.format("%.2f", score[cursor.getCount() - 1]) + "%" + ",睡眠得分："
                        + String.format("%.2f", grade));
            }
        } else {
            Log.e("Error","test2");
            mText.setText("暂无数据");
            Toast.makeText(this,"暂无睡眠情况信息",Toast.LENGTH_SHORT).show();
            return;
        }

        lineChart = (LineChartView)findViewById(R.id.linechart);
        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点
        initLineChart();//初始化
    }

    private String getDate(long time) {
        Date date2=new Date();
        date2.setTime(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
        return simpleDateFormat.format(date2);
    }


    private String getHour(long time) {
        Date date2=new Date();
        date2.setTime(time);
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH");
        return simpleDateFormat1.format(date2);
    }


    private void getAxisXLables() {
        for (int i = 0; i < date.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
        }
    }

    private void getAxisPoints() {
        for (int i = 0; i < score.length; i++) {
            mPointValues.add(new PointValue(i, (float) score[i]));
        }
    }

    private void initLineChart(){
        Line line = new Line(mPointValues).setColor(Color.GREEN);  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(true);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setFormatter(new SimpleLineChartValueFormatter(2));
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);  //设置字体颜色
        axisX.setName("深度睡眠比率(百分比)");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(4); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis().setHasLines(true);
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        data.setAxisYLeft(axisY);

        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right= 7;
        lineChart.setCurrentViewport(v);
    }







}
