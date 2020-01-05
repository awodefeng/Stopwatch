package com.xxun.watch.xunstopwatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "XunStopwatchActivity ";
    private TextView timerTextMin;
    private TextView timerTextSec;
    private TextView timerTextMilisec;
    private ImageView btStart;
    private ImageView btStartPause;
    private ImageView btPause;
    private ImageView btReset;
    private boolean isPause = false;//是否暂停
    private long currentTime = 0;//当前毫秒数
    private long initialTime = 0;
    private int timerMin;
    private int timerSec;
    private int timerMilisec;
    private double incrementMsecValue;
    //计时器
    private Handler mhandle = new Handler();
    private PowerManager.WakeLock mWakeLock = null;
    private  StopHandler sHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sHandler = new StopHandler(MainActivity.this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        acquireWakeLock();
        sHandler.sendEmptyMessageDelayed(0, 30000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
    }

    private void initView() {
        timerTextMin = (TextView) findViewById(R.id.time_min);
        timerTextSec = (TextView) findViewById(R.id.time_sec);
        timerTextMilisec = (TextView) findViewById(R.id.time_msec);
        btStart = (ImageView) findViewById(R.id.start_icon);
        btPause = (ImageView) findViewById(R.id.pause_icon);
        btStartPause = (ImageView) findViewById(R.id.start_pause_icon);
        btReset = (ImageView) findViewById(R.id.reset_icon);

        btPause.setVisibility(View.GONE);
        btStartPause.setVisibility(View.GONE);
        btReset.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.start_icon) {

            btStart.setVisibility(View.GONE);
            btPause.setVisibility(View.VISIBLE);
            initialTime = System.currentTimeMillis();
            mhandle.postDelayed(timeRunable, 100);

        } else if (id == R.id.pause_icon) {

            btPause.setVisibility(View.GONE);
            btStartPause.setVisibility(View.VISIBLE);
            btReset.setVisibility(View.VISIBLE);
            isPause = true;

        } else if (id == R.id.start_pause_icon) {

            btStartPause.setVisibility(View.GONE);
            btReset.setVisibility(View.GONE);
            btPause.setVisibility(View.VISIBLE);
            isPause = false;
            mhandle.postDelayed(timeRunable, 100);
            initialTime = System.currentTimeMillis();

        } else if (id == R.id.reset_icon) {

            btStartPause.setVisibility(View.GONE);
            btReset.setVisibility(View.GONE);
            btStart.setVisibility(View.VISIBLE);
            isPause = false;
            updateTime(true);
        }
    }

    private Runnable timeRunable = new Runnable() {
        @Override
        public void run() {

            currentTime = System.currentTimeMillis();
            increment_time(currentTime);
            initialTime = currentTime;
            updateTime(false);

            if (!isPause) {
                mhandle.postDelayed(this, 85);
            }
        }
    };

    private void updateTime(boolean reset) {

        if (reset) {
            timerMin = 0;
            timerSec = 0;
            timerMilisec = 0;
        }
        timerTextMin.setText(timerMin < 10 ? "0" + timerMin : timerMin + "");
        timerTextSec.setText(timerSec < 10 ? "0" + timerSec : timerSec + "");
        timerTextMilisec.setText(timerMilisec < 10 ? "0" + timerMilisec : timerMilisec + "");
    }

    private void increment_time(long curTime) {

        double totalTime;
        long currTime = currentTime;
        long initTime = initialTime;

        if (initTime > currTime) {
            totalTime = (0xffffffff - initTime) + currTime;
        } else {
            totalTime = currTime - initTime;
        }
        //Log.d(TAG,"increment_time : "+totalTime);
        if (totalTime >= 10) {
            timerMilisec += (int) totalTime / 10;
            totalTime = totalTime - ((int) totalTime / 10) * 10;
        }

        if (totalTime > 0) {
            incrementMsecValue = incrementMsecValue + totalTime;
            if (incrementMsecValue > 10.0) {
                timerMilisec++;
                incrementMsecValue = incrementMsecValue - 10.0;
            }
        }

        if (timerMilisec >= 100) {
            timerSec += timerMilisec / 100;
            timerMilisec -= (timerMilisec / 100) * 100;

            if (timerSec >= 60) {
                timerMin += timerSec / 60;
                timerSec -= (timerSec / 60) * 60;
                if (timerMin > 59) {
                    timerMin = 0;
                }
            }
        }
    }

    public static class StopHandler extends Handler {

        WeakReference<MainActivity> weakReference;

        public StopHandler(MainActivity activity) {
            this.weakReference = new WeakReference(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG,"handleMessage");
            super.handleMessage(msg);
            weakReference.get().releaseWakeLock();
            Log.i(TAG, "handleMessage: lyly 30 miao dao le activity === " + weakReference.get());
        }
    }


    /**
     * 获取唤醒锁
     */
    private void acquireWakeLock()
    {
        Log.d(TAG,"acquireWakeLock");

        if(mWakeLock == null)
        {
            PowerManager mPM = (PowerManager) this.getSystemService(this.POWER_SERVICE);
            mWakeLock = mPM.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"Stopwatch");
            if(mWakeLock!=null)
            {
                Log.d(TAG,"wakelock.acquire");
                mWakeLock.acquire();
            }
        }
    }

    /**
     * 释放锁
     */
    public   void releaseWakeLock()
    {
        Log.d(TAG,"releaseWakeLock");
        if(mWakeLock!=null)
        {
            mWakeLock.release();
            mWakeLock = null;
        }

        if (sHandler != null) {
            sHandler.removeCallbacksAndMessages(null);
        }
    }

}



