package com.example.test;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    long millisec_time = 0L, start_time = 0L, buff=0L, update_time = 0L ;
    long saved_time = 0L;
    String save_text = "START";

    Handler handler;

    int secs = 0, mins = 0, millisecs = 0;

    Button start , reset ;
    TextView timer ;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start);
        reset = findViewById(R.id.reset);
        timer = findViewById(R.id.timer);
        reset.setText("RESET");
        start.setText("START");
        timer.setText("0:00:000");

        handler = new Handler() ;
    }


    @SuppressLint("SetTextI18n")
    public void startTime(View v)
    {
        if(start.getText().equals("STOP"))
        {
            start.setText("START");
            buff += millisec_time;
            handler.removeCallbacks(runnable);
        }
        else if(start.getText().equals("START"))
        {
            start.setText("STOP");
            start_time = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
        }
    }

    @SuppressLint("SetTextI18n")
    public void resetTime(View v)
    {
        start.setText("START");
        handler.removeCallbacks(runnable);
        buff = 0L ;
        millisec_time = 0L ;
        start_time = 0L ;
        update_time = 0L ;
        secs = 0 ;
        mins = 0 ;
        millisecs = 0 ;

        timer.setText("0:00:000");
    }

    public Runnable runnable = new Runnable() {

        @SuppressLint("DefaultLocale")
        public void run() {

            millisec_time = SystemClock.uptimeMillis() - start_time;
            update_time = buff + millisec_time;
            secs = (int) (update_time / 1000);
            mins = secs / 60;
            secs = secs % 60;
            millisecs = (int) (update_time % 1000);
            timer.setText(String.format("%d:%02d:%03d" , mins , secs , millisecs));

            handler.postDelayed(this, 0);

        }

    };

    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("prefs" , MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("save_text" , start.getText().toString());
        editor.putLong("saved_time" , SystemClock.uptimeMillis());

        if(start.getText().equals("STOP"))
            buff += millisec_time;

        editor.putLong("saved_buff",buff);
        editor.apply();
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        save_text = prefs.getString("save_text","START");
        buff = prefs.getLong("saved_buff", 0);
        saved_time = prefs.getLong("saved_time", 0);

        start.setText(save_text);

        start_time = SystemClock.uptimeMillis();
        millisec_time = SystemClock.uptimeMillis() - start_time;

        if (save_text.equals("STOP"))
        {
            buff += SystemClock.uptimeMillis() - saved_time;
            handler.postDelayed(runnable, 0);
        }

        update_time = buff + millisec_time;
        secs = (int) (update_time / 1000);
        mins = secs / 60;
        secs = secs % 60;
        millisecs = (int) (update_time % 1000);
        timer.setText(String.format("%d:%02d:%03d" , mins , secs , millisecs));
    }

}

