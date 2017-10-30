package com.zhuhe.AccelerationSensor;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static android.os.Environment.getExternalStorageDirectory;


public class MainActivity extends AppCompatActivity {
    public TextView mSensorInfo;
    public TextView historyInfo;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TestSensorListener mSensorListener;
    private ToggleButton toggleButton_x, toggleButton_y, toggleButton_z;
    private boolean isonRecoding = false;
    private final int MAXDATACOUNT = 1000000;
    private Queue<dataVector> dataCache = new ArrayDeque<>(MAXDATACOUNT);
    private final String FILENAME = "/log.txt";
    private Queue<Float> chartDataQueue = new ArrayDeque<Float>(10);
    private LineChartView linerChart;

    /**
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //得到程序当前的Context，即MainActivity.this
        bindViews();
        mSensorListener = new TestSensorListener();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linerChart = linerChartInit();
        toggleButton_x = (ToggleButton) findViewById(R.id.toggleButton_x);
        toggleButton_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton_x.isChecked()) {
                    toggleButton_y.setChecked(false);
                    toggleButton_z.setChecked(false);
                }
            }
        });
        toggleButton_y = (ToggleButton) findViewById(R.id.toggleButton_y);
        toggleButton_y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton_y.isChecked()) {
                    toggleButton_x.setChecked(false);
                    toggleButton_z.setChecked(false);
                }
            }
        });
        toggleButton_z = (ToggleButton) findViewById(R.id.toggleButton_z);
        toggleButton_z.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton_z.isChecked()) {
                    toggleButton_x.setChecked(false);
                    toggleButton_y.setChecked(false);
                }
            }
        });

        final ImageButton recodeButton = (ImageButton) findViewById(R.id.recordButton);
        recodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isonRecoding) {
                    recodeButton.setImageResource(R.mipmap.recoding);
                    isonRecoding = true;
                } else {
                    recodeButton.setImageResource(R.mipmap.play);
                    String x = new String();
                    try {
                        File f = new File(getExternalStorageDirectory().toString() + FILENAME);
                        if (!f.exists())
                            f.createNewFile();
                        FileOutputStream output = new FileOutputStream(f, false);

                        while (!dataCache.isEmpty()) {
                            String temp = dataCache.poll().toString();
                            x += temp;
                            output.write(temp.getBytes());
                        }
                        output.close();
                        historyInfo.setText(x);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("mmp", "mmp");
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

    private void bindViews() {
        mSensorInfo = (TextView) findViewById(R.id.textView);
        historyInfo = (TextView) findViewById(R.id.debugText);
    }


    private class TestSensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            mSensorInfo.setTextSize(40); //设置40PX
            if (chartDataQueue.size() == 10) chartDataQueue.remove();
            if (isonRecoding) {
                dataCache.add(new dataVector(event.values[0], event.values[1], event.values[2]));//每次接受到数据后放入队列
                if (dataCache.size() == MAXDATACOUNT) dataCache.remove();
            }
            if (toggleButton_x.isChecked()) {
                // mSensorInfo.setText("各个方向加速度值" + String.format(" x:%.1f y:%.1f z:%.1f", event.values[0], event.values[1], event.values[2]));
                mSensorInfo.setText(String.format("%.1f ", event.values[0]));
                chartDataQueue.add(event.values[0]);
            }
            if (toggleButton_y.isChecked()) {
                // mSensorInfo.setText("各个方向加速度值" + String.format(" x:%.1f y:%.1f z:%.1f", event.values[0], event.values[1], event.values[2]));
                mSensorInfo.setText(String.format("%.1f ", event.values[1]));
                chartDataQueue.add(event.values[1]);
            }
            if (toggleButton_z.isChecked()) {
                // mSensorInfo.setText("各个方向加速度值" + String.format(" x:%.1f y:%.1f z:%.1f", event.values[0], event.values[1], event.values[2]));
                mSensorInfo.setText(String.format("%.1f ", event.values[2]));
                chartDataQueue.add(event.values[2]);
            }
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
        List<PointValue> mPointsValues = new ArrayList<PointValue>(10);
        int i = 0;
        for (Float x : chartDataQueue)
            mPointsValues.add(new PointValue(i++, x));
        Line line = new Line(mPointsValues).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        LineChartData data = new LineChartData();
        Axis xaxis = new Axis();
        Axis yaxis = new Axis();
        List<AxisValue> temp = new ArrayList<>();

        data.setAxisXBottom(xaxis);
        data.setAxisYLeft(yaxis);
        data.setLines(lines);

        linerChart.setLineChartData(data);
    }

    class dataVector { //用来存三个数据的集合
        double x, y, z;

        public dataVector() {
            x = y = z = 0;
        }

        public dataVector(double ix, double iy, double iz) {
            x = ix;
            y = iy;
            z = iz;
        }

        public String toString() {
            return String.format("<%.2f,%.2f,%.2f>", x, y, z);
        }
    }
}