package com.zhuhe.AccelerationSensor;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static android.os.Environment.getExternalStorageDirectory;


public class MainActivity extends AppCompatActivity {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TestSensorListener mSensorListener;
    private boolean isonRecoding = false;
    private final int MAXDATACOUNT = 1000000;
    private Queue<dataVector> dataCache = new ArrayDeque<>(MAXDATACOUNT);
    private final String FILENAME = "/log.txt";
    private Queue<dataVector> chartDataQueue = new ArrayDeque<>(10);
    private LineChartView linerChart;
    private TextView xInfo,yInfo,zInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //得到程序当前的Context，即MainActivity.this
        mSensorListener = new TestSensorListener();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linerChart = linerChartInit();
        xInfo = (TextView)findViewById(R.id.xInfo);
        yInfo = (TextView)findViewById(R.id.yInfo);
        zInfo = (TextView)findViewById(R.id.zInfo);
        final ImageButton recodeButton = (ImageButton) findViewById(R.id.recordButton);
        recodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isonRecoding) {
                    recodeButton.setImageResource(R.mipmap.recoding);
                    isonRecoding = true;
                } else {
                    recodeButton.setImageResource(R.mipmap.play);
                    try {
                        File f = new File(getExternalStorageDirectory().toString() + FILENAME);
                        if (!f.exists())
                            f.createNewFile();
                        FileOutputStream output = new FileOutputStream(f, false);

                        String x = "";
                        while (!dataCache.isEmpty()) {
                            String temp = dataCache.poll().toString();
                            x += temp;
                            output.write(temp.getBytes());
                        }
                        output.close();
                        Toast.makeText(getApplicationContext(),("文件已保存到" + getExternalStorageDirectory().toString() + FILENAME),Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"文件保存出错",Toast.LENGTH_LONG).show();
                    }
                    isonRecoding = false;
                }
            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Toast.makeText(this, "乐观队制作", Toast.LENGTH_LONG).show();
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mAccelerometer, 100000);//设置刷新率10Hz
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorListener);
    }

    private class TestSensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (chartDataQueue.size() == 10) chartDataQueue.remove();
            if (isonRecoding) {
                dataCache.add(new dataVector(event.values[0], event.values[1], event.values[2]));//每次接受到数据后放入队列
                if (dataCache.size() == MAXDATACOUNT) dataCache.remove();
            }
            chartDataQueue.add(new dataVector(event.values[0],event.values[1],event.values[2]));
            xInfo.setText(String.format("X:%.2f m/s²",event.values[0]));
            yInfo.setText(String.format("Y:%.2f m/s²",event.values[1]));
            zInfo.setText(String.format("Z:%.2f m/s²",event.values[2]));
            chartViewUpdate();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    }

    private LineChartView linerChartInit() {
        LineChartView linerChart = (LineChartView) findViewById(R.id.linerChart);
        Viewport v = new Viewport(linerChart.getMaximumViewport());
        linerChart.setInteractive(false);
        v.bottom = -5;
        v.top = 15;
        linerChart.setCurrentViewport(v);
        linerChart.setMaximumViewport(v);
        linerChart.setZoomEnabled(false);
        return linerChart;
    }

    private void chartViewUpdate() {
        List<PointValue> xPointsValues = new ArrayList<>(10);
        List<PointValue> yPointsValues = new ArrayList<>(10);
        List<PointValue> zPointsValues = new ArrayList<>(10);
        int i = 0;
        for (dataVector index: chartDataQueue) {
            xPointsValues.add(new PointValue(i, (float)index.x));
            yPointsValues.add(new PointValue(i, (float)index.y));
            zPointsValues.add(new PointValue(i, (float)index.z));
            i++;
        }
        Line xline = new Line(xPointsValues).setColor(Color.argb(0xff,0x00,0x99,0xCC)).setCubic(true);
        Line yline = new Line(yPointsValues).setColor(Color.argb(0xff,0x66,0x99,0x00)).setCubic(true);
        Line zline = new Line(zPointsValues).setColor(Color.argb(0xff,0xff,0x88,0x00)).setCubic(true);
        List<Line> lines = new ArrayList<>();
        lines.add(xline);
        lines.add(yline);
        lines.add(zline);
        LineChartData data = new LineChartData();
        Axis xaxis = new Axis();
        Axis yaxis = new Axis();

        data.setAxisXBottom(xaxis);
        data.setAxisYLeft(yaxis);
        data.setLines(lines);

        linerChart.setLineChartData(data);
    }

    private class dataVector { //用来存三个数据的集合
        double x, y, z;

        dataVector(double ix, double iy, double iz) {
            x = ix;
            y = iy;
            z = iz;
        }

        public String toString() {
            return String.format("<%.2f,%.2f,%.2f>", x, y, z);
        }
    }
}