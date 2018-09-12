package com.example.clyste.jf_dl_final_babyhealth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;


/**
 * HOME SCREEN - Grid View
 */
public class HomeGrid extends Fragment {

    // https://www.raywenderlich.com/127544/android-gridview-getting-started


    //widgets
    private View homeGridView;

    private HomeGridListener listener;

    public HomeGrid() {
        // Required empty public constructor
    }

    public static HomeGrid newInstance() {
        HomeGrid fragment = new HomeGrid();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private GridCell[] categories = new GridCell[]{

            new GridCell("Baby", R.drawable.carriage),
            new GridCell("Data", R.drawable.graph),
            new GridCell("Timer", R.drawable.timer),
            //new GridCell("Photos", R.drawable.camera),
            new GridCell("Panic", R.drawable.warning),
            new GridCell("Music", R.drawable.music),
            new GridCell("Settings", R.drawable.gear),
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeGridView =  inflater.inflate(R.layout.fragment_home_grid, container, false);
        //menuGridView = homeGridView.findViewById(R.id.gridMenu);
        listener = (HomeGridListener)getActivity();
        GridView gridView = homeGridView.findViewById(R.id.gridview);
        GridAdapter grid = new GridAdapter(getContext(), categories);
        gridView.setVerticalScrollBarEnabled(false);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (position == 0){
                    listener.launchEvents();
                } else if (position == 1) {
                    listener.launchGraph();
                }else if (position == 2) {
                    listener.launchTimer();
                }else if (position == 3) {
                    //Intent intent = new Intent(getActivity(), PictureActivity.class);
                    //startActivity(intent);
                    listener.launchPanic();
                }else if (position == 4) {
                    Intent intent = new Intent(getActivity(), MusicActivity.class);
                    startActivity(intent);
                }else if (position == 5) {
                    listener.launchPreferences();
                }
            }
        });

        gridView.setAdapter(grid);

        return homeGridView;
    }

    public interface HomeGridListener{
        public void launchEvents();
        public void launchGraph();
        public void launchTimer();
        public void launchPreferences();
        public void launchPanic();
    }
}