package com.example.clyste.jf_dl_final_babyhealth;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


//https://github.com/PhilJay/MPAndroidChart/wiki/Getting-Started
//https://github.com/PhilJay/MPAndroidChart/wiki/Setting-Data

public class GraphFragment extends Fragment {

    public static final String NAME_KEY = "NAME_KEY";
    public static final String EVENT_KEY = "EVENT_KEY";
    private ArrayList<String> labels;
    private ArrayList<String> values;
    private ArrayList<BarEntry> entries;
    private String firstName;
    private String eventName;
    private ChildEventListener childEventListener;
    private BarChart chart;

    public GraphFragment(){}

    public static GraphFragment newInstance(String firstName, String eventName){
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putString(NAME_KEY, firstName);
        args.putString(EVENT_KEY, eventName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null){
            firstName = getArguments().getString(NAME_KEY);
            eventName = getArguments().getString(EVENT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View graphView = inflater.inflate(R.layout.fragment_graph, container, false);
        // in this example, a barchart is initialized from xml
        chart = (BarChart)graphView.findViewById(R.id.chart);

        //data points need to be wrapped by BarEntry object
        entries = new ArrayList<>();
        values = new ArrayList<>();
        labels = new ArrayList<>();
        getDataFromDB();
        return graphView;
    }

    private void getDataFromDB(){
        DatabaseReference ref = StaticFirebase.databaseReference.child(StaticFirebase.uid).child(firstName).child(eventName);
        Log.i("JSF", "getDataFromDB: reading from DB: " + firstName + eventName);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("JSF", "onChildAdded: " + dataSnapshot.getChildren());
                for (DataSnapshot i: dataSnapshot.getChildren()){
                    Log.i("JSF-snap", "snapshot key" + i.getKey());
                    if (i.getKey().toString().matches("formattedDate")){
                        String data = i.getValue().toString();
                        labels.add(data);
                        Log.i("JSF-ADD", "added label: " + data);
                    }
                    else if (i.getKey().toString().matches("volume")){
                        String data = i.getValue().toString();
                        values.add(data);
                        Log.i("JSF-ADD", "addedValue: " + data);
                        generateEntries();
                        generateGraph();
                    }
                    Log.i("JSF", "onChildAdded getChildren: " + i.getValue());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(childEventListener);

    }

    private void generateEntries(){
        float counter = 0;
        for (String i:values){
            //BarEntry(Xposition, Value)
            entries.add(new BarEntry(counter, Float.valueOf(i)));
            counter ++;
        }
        for (int i =0; i < labels.size(); i++){
            Log.i("JSF-data", "label: " + labels.get(i));
            Log.i("JSF-data", "value: " + values.get(i));
        }
        printData(labels);
        printData(values);
    }

    private void generateGraph(){
        BarDataSet dataset = new BarDataSet(entries, "Label");
        dataset.setColors(ColorTemplate.MATERIAL_COLORS);

        //adding labels to axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int)value);
            }
        });


        //Add bardata to chart
        BarData data = new BarData(dataset);
//        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
//        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh

    }

    private void printData(ArrayList<String> data){
        Log.i("JSF-PRINT", "printData: called");
        for (String i: data){
            Log.i("JSF-PRINT", "printData: " + i);
        }
    }
}