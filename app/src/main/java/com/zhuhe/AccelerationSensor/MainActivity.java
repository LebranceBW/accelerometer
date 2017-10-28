package com.zhuhe.AccelerationSensor;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import static android.os.Environment.getExternalStorageDirectory;


public class MainActivity extends AppCompatActivity{
    public TextView mSensorInfo;
    public TextView historyInfo;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TestSensorListener mSensorListener;
    private ToggleButton toggleButton_x,toggleButton_y,toggleButton_z;
    private boolean isonRecoding = false;
    private final int MAXDATACOUNT = 1000000;
    private Queue<dataVector> dataCache = new ArrayDeque<>(MAXDATACOUNT);
    private final String FILENAME = "/log.txt";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
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

        final ImageView showimage = (ImageView) findViewById(R.id.imageView);
        toggleButton_x = (ToggleButton) findViewById(R.id.toggleButton_x);
        toggleButton_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton_x.isChecked()) {
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
                if (toggleButton_y.isChecked()) {
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
                if (toggleButton_z.isChecked()) {
                    toggleButton_x.setChecked(false);
                    toggleButton_y.setChecked(false);
                    showimage.setImageResource(R.drawable.z_2_173173);
                    showimage.refreshDrawableState();
                }
            }
        });

       final ImageButton recodeButton = (ImageButton) findViewById(R.id.recordButton);
        recodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isonRecoding)
                {
                    recodeButton.setImageResource(R.mipmap.recoding);
                    isonRecoding = true;
                }
                else {
                    recodeButton.setImageResource(R.mipmap.play);
                    String x = new String();
                    try{
                        File f = new File(getExternalStorageDirectory().toString() + FILENAME);
//                        File f = new File("/storage/0778-1D"+FILENAME);
                        if(!f.exists())
                            f.createNewFile();
                        FileOutputStream output = new FileOutputStream(f,false);

                        while(!dataCache.isEmpty()) {
                            String temp = dataCache.poll().toString();
                            x += temp;
                            output.write(temp.getBytes());
                        }
                        output.close();
                        historyInfo.setText(x);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        Log.e("mmp","mmp");
                    }
                    isonRecoding = false;
//                    try {
//                        //得到输入框中文件名获得文件内容，因为可以写入多个不同名文件，所以要根据文件名来获得文件内容
//                        FileInputStream input = mContext.openFileInput("m.txt");
//                        //调用read()方法，传入上面获得的文件保，将返回的内容赋值给detail
//                        byte[] temp = new byte[1024];
//                        //定义字符串变量
//                        StringBuilder sb = new StringBuilder("");
//                        int len = 0;
//                        //读取文件内容，当文件内容长度大于0时，
//                        while ((len = input.read(temp)) > 0) {
//                            //把字条串连接到尾部
//                            sb.append(new String(temp, 0, len));
//                        }
//                        //关闭输入流
//                        input.close();
//                        historyInfo.setText(sb.toString());
//                        String s1[] = sb.toString().trim().split(" ");
//                        double[] dou = new double[sb.toString().length()];
//                        int[] sensor = new int[sb.toString().length()];
//                        String[] date = new String[s1.length];
//                        for (int i = 0; i < s1.length; i++) {
//                            dou[i] = Double.valueOf(s1[i]);
//                            sensor[i] = (int) dou[i] * 10;
//                            date[i] = i + "";
//                        }
//                        Intent intent =new Intent(MainActivity.this,SecondActivity.class);
//
//                        //用Bundle携带数据
//                        Bundle bundle=new Bundle();
//                        //传递name参数为tinyphp
//                        bundle.putIntArray("sensor",sensor);
//                        bundle.putStringArray("date",date);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//
//                    } catch (IOException e) {
//
//                        e.printStackTrace();
//                    }
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
        mSensorManager.registerListener(mSensorListener, mAccelerometer,100000);//设置刷新率10Hz
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorListener);
    }

    private void bindViews() {
        mSensorInfo = (TextView) findViewById(R.id.textView);
        historyInfo = (TextView)findViewById(R.id.debugText);
    }


    private class TestSensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            mSensorInfo.setTextSize(40); //设置40PX
            if(isonRecoding) {
                dataCache.add(new dataVector(event.values[0], event.values[1], event.values[2]));//每次接受到数据后放入队列
                if (dataCache.size() == MAXDATACOUNT) dataCache.remove();
            }
            if (toggleButton_x.isChecked()) {
                // mSensorInfo.setText("各个方向加速度值" + String.format(" x:%.1f y:%.1f z:%.1f", event.values[0], event.values[1], event.values[2]));
                mSensorInfo.setText(String.format("%.1f ", event.values[0]));
            }
            if (toggleButton_y.isChecked()) {
                // mSensorInfo.setText("各个方向加速度值" + String.format(" x:%.1f y:%.1f z:%.1f", event.values[0], event.values[1], event.values[2]));
                mSensorInfo.setText(String.format("%.1f ", event.values[1]));
            }
            if (toggleButton_z.isChecked()) {
                // mSensorInfo.setText("各个方向加速度值" + String.format(" x:%.1f y:%.1f z:%.1f", event.values[0], event.values[1], event.values[2]));
                mSensorInfo.setText(String.format("%.1f ", event.values[2]));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            mSensorInfo.setText("Hello!");
        }

    }


//        public void run() {
//            while (!Thread.currentThread().isInterrupted()) {
//                while (isonRecoding) {
//                    try {
//                        if (SensorFlag) {
//                            try {
//                                FileOutputStream output = mContext.openFileOutput("m.txt", Context.MODE_APPEND);
//                                if(mSensorInfo.getText()!="Hello!")
//                                     output.write(String.valueOf(mSensorInfo.getText()).getBytes());  //将String字符串以字节流的形式写入到输出流中
//                                output.close();         //关闭输出流
//                                SensorFlag = false;
//                            } catch (Exception e) {
//                                //写入异常时
//                                e.printStackTrace();
//                            }
//                        }
//
//                    } catch (Exception e) {
//                        //写入异常时
//                        Log.e("IOerror","error Writting");
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        }
//    }
    private class dataVector{ //用来存三个数据的集合
        public double x,y,z;

        public dataVector(){
            x=y=z=0;
        }
        public dataVector(double ix,double iy,double iz)
        {
            x = ix;y =iy;z =iz;
        }
        public String toString()
        {
            return String.format("<%.2f,%.2f,%.2f>",x,y,z);
        }
    }
}