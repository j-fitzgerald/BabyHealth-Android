package com.example.clyste.jf_dl_final_babyhealth;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Handler;
import java.util.List;
import java.util.ArrayList;

import static com.example.clyste.jf_dl_final_babyhealth.MainActivity.TAG;

//https://stackoverflow.com/questions/3227015/changing-android-system-clock-stops-timers-how-can-i-restart-them


public class TimerFragment extends Fragment {

    //widgets
    private TextView timeDisplay;
    private ListView recordedList;
    private Button start, pause, reset, record;

    //vars
    private long milliTime, startTime, timeBuff, updateTime = 0 ;
    private Handler handler;
    private int seconds, minutes, milliSeconds ;
    private List<String> timeList ;
    private ArrayAdapter<String> adapter;
    private Runnable runnable;

    public TimerFragment(){ }


    public static TimerFragment newInstance() {
        TimerFragment fragment = new TimerFragment();

        Bundle args = new Bundle();
        Log.i(TAG, "newInstance: selectBaby");
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_timer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View timerFragmentView = inflater.inflate(R.layout.fragment_timer, container, false);
        timeDisplay = timerFragmentView.findViewById(R.id.displayTime);
        start = timerFragmentView.findViewById(R.id.button);
        pause = timerFragmentView.findViewById(R.id.button2);
        reset = timerFragmentView.findViewById(R.id.button3);
        record = timerFragmentView.findViewById(R.id.button4) ;
        recordedList = timerFragmentView.findViewById(R.id.recordedTimes);

        handler = new Handler() ;
        timeList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, timeList);

        recordedList.setAdapter(adapter);

        runnable = new Runnable() {
            public void run() {
                milliTime = SystemClock.uptimeMillis() - startTime;
                updateTime = timeBuff + milliTime;
                seconds = (int) (updateTime / 1000);
                minutes = seconds / 60;
                seconds = seconds % 60;
                milliSeconds = (int) (updateTime % 1000);
                timeDisplay.setText("" + minutes + ":"
                        + String.format("%02d", seconds) + ":"
                        + String.format("%03d", milliSeconds));
                handler.postDelayed(this, 0);
            }
        };

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
                reset.setEnabled(false);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeBuff += milliTime;
                handler.removeCallbacks(runnable);
                reset.setEnabled(true);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seconds = 0;
                minutes = 0;
                milliSeconds = 0;
                startTime = 0;
                milliTime = 0;
                timeBuff = 0;
                updateTime = 0;
                timeDisplay.setText("00:00:00");
                timeList.clear();
                adapter.notifyDataSetChanged();
            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeList.add(timeDisplay.getText().toString());
                adapter.notifyDataSetChanged();
            }
        });
        return timerFragmentView;
    }


}