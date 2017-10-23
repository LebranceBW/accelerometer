package com.zhuhe.AccelerationSensor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private boolean flag = false;
    private boolean SensorFlag = false;
    protected static final int SensorCorcollection = 0x101;
    private EditText editname;
    private Button btnreset;
    private Button btnread;
    private Button btnstart;
    private Context mContext;
    public TextView mSensorInfo;
    public TextView historyInfo;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TestSensorListener mSensorListener;
    public TextView AccelerationInfo;
    private ImageView showimage;
    private ToggleButton toggleButton_x;
    private ToggleButton toggleButton_y;
    private ToggleButton toggleButton_z;

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SensorCorcollection:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //得到程序当前的Context，即MainActivity.this
        mContext = getApplicationContext();
        bindViews();
        new Thread(new myThread()).start();
        mSensorListener = new TestSensorListener();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        showimage = (ImageView) findViewById(R.id.imageView);
        toggleButton_x = (ToggleButton) findViewById(R.id.toggleButton_x);
        toggleButton_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton_x.isChecked() == true) {
                    toggleButton_y.setChecked(false);
                    toggleButton_z.setChecked(false);
                    showimage.setImageResource(R.drawable.x_2_173173);
                    showimage.refreshDrawableState();
                }


            }
        });
        toggleButton_y = (ToggleButton) findViewById(R.id.toggleButton_y);
        toggleButton_y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton_y.isChecked() == true) {
                    toggleButton_x.setChecked(false);
                    toggleButton_z.setChecked(false);
                    showimage.setImageResource(R.drawable.y_2_173173);
                    showimage.refreshDrawableState();
                }
            }
        });
        toggleButton_z = (ToggleButton) findViewById(R.id.toggleButton_z);
        toggleButton_z.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton_z.isChecked() == true) {
                    toggleButton_x.setChecked(false);
                    toggleButton_y.setChecked(false);
                    showimage.setImageResource(R.drawable.z_2_173173);
                    showimage.refreshDrawableState();
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
        mSensorManager.registerListener(mSensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorListener);
    }

    private void bindViews() {
        editname = (EditText) findViewById(R.id.edit_name);
        btnreset = (Button) findViewById(R.id.reset);
        btnread = (Button) findViewById(R.id.read);
        btnstart = (Button) findViewById(R.id.start);
        mSensorInfo = (TextView) findViewById(R.id.textView);
        historyInfo = (TextView) findViewById(R.id.textView2);
        btnstart.setOnClickListener(this);
        btnreset.setOnClickListener(this);
        btnread.setOnClickListener(this);
    }


    class TestSensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            mSensorInfo.setTextSize(40); //设置40PX
            SensorFlag = true;
            if (toggleButton_x.isChecked() == true) {
                // mSensorInfo.setText("各个方向加速度值" + String.format(" x:%.1f y:%.1f z:%.1f", event.values[0], event.values[1], event.values[2]));
                mSensorInfo.setText(String.format("%.1f ", event.values[0]));
            }
            if (toggleButton_y.isChecked() == true) {
                // mSensorInfo.setText("各个方向加速度值" + String.format(" x:%.1f y:%.1f z:%.1f", event.values[0], event.values[1], event.values[2]));
                mSensorInfo.setText(String.format("%.1f ", event.values[1]));
            }
            if (toggleButton_z.isChecked() == true) {
                // mSensorInfo.setText("各个方向加速度值" + String.format(" x:%.1f y:%.1f z:%.1f", event.values[0], event.values[1], event.values[2]));
                mSensorInfo.setText(String.format("%.1f ", event.values[2]));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            mSensorInfo.setText("Hello!");
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset:
                try {
                    FileOutputStream output = mContext.openFileOutput("m.txt", Context.MODE_PRIVATE);
                    output.write(" ".getBytes());  //将String字符串以字节流的形式写入到输出流中
                    output.close();         //关闭输出流
                } catch (Exception e) {
                    //写入异常时
                    e.printStackTrace();
                }
                break;
            case R.id.start:
                if (flag) {
                    btnstart.setText("开始");
                    flag = false;
                    //    AccelerationInfo.setText(AccelerationInfo.getText().toString()+mSensorInfo.getText().toString());
                } else {
                    btnstart.setText("结束");
                    //   Toast.makeText(getApplicationContext(), AccelerationInfo.getText(), Toast.LENGTH_SHORT).show();
                    flag = true;
                }
                break;
            case R.id.read:
                //定论一个detail，默认为空用来存放要输出的内容
                try {
                    //得到输入框中文件名获得文件内容，因为可以写入多个不同名文件，所以要根据文件名来获得文件内容
                    FileInputStream input = mContext.openFileInput("m.txt");
                    //调用read()方法，传入上面获得的文件保，将返回的内容赋值给detail
                    byte[] temp = new byte[1024];
                    //定义字符串变量
                    StringBuilder sb = new StringBuilder("");
                    int len = 0;
                    //读取文件内容，当文件内容长度大于0时，
                    while ((len = input.read(temp)) > 0) {
                        //把字条串连接到尾部
                        sb.append(new String(temp, 0, len));
                    }
                    //关闭输入流
                    input.close();
                    historyInfo.setText(sb.toString());
                    String s1[] = sb.toString().trim().split(" ");
                    double[] dou = new double[sb.toString().length()];
                    int[] sensor = new int[sb.toString().length()];
                    String[] date = new String[s1.length];
                    for (int i = 0; i < s1.length; i++) {
                        dou[i] = Double.valueOf(s1[i]);
                        sensor[i] = (int) dou[i] * 10;
                        date[i] = i + "";
                    }
                    Intent intent =new Intent(MainActivity.this,SecondActivity.class);

                    //用Bundle携带数据
                    Bundle bundle=new Bundle();
                    //传递name参数为tinyphp
                    bundle.putIntArray("sensor",sensor);
                    bundle.putStringArray("date",date);
                    intent.putExtras(bundle);
                    startActivity(intent);

                } catch (IOException e) {
                    e.printStackTrace();
                }


        }
    }



    class myThread implements Runnable {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = new Message();
                message.what = MainActivity.SensorCorcollection;
                while (flag) {
                    try {
                        if (SensorFlag) {
                            try {
                                FileOutputStream output = mContext.openFileOutput("m.txt", Context.MODE_APPEND);
                                output.write(mSensorInfo.getText().toString().getBytes());  //将String字符串以字节流的形式写入到输出流中
                                output.close();         //关闭输出流
                                SensorFlag = false;
                            } catch (Exception e) {
                                //写入异常时
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        //写入异常时
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}