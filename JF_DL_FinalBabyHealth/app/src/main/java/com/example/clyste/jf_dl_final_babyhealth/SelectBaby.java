package com.example.clyste.jf_dl_final_babyhealth;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.clyste.jf_dl_final_babyhealth.MainActivity.TAG;

/**
 * SELECT BABY -- After login:
 * populate list of babies in the database
 * user selects one
 *  move to Home Screen
 */
public class SelectBaby extends Fragment implements AdapterView.OnItemClickListener{

    private ArrayList<String> babyNames;
    private ListView babyList;
    private Button addBabyButton;
    private SelectBabyListener listener;
    private ChildEventListener childEventListener;
    private ArrayAdapter listViewAdapter;

    private String childNode = "BabyNames";
    private String userNode = StaticFirebase.uid;

    public SelectBaby() {
        // Required empty public constructor
    }

    public static SelectBaby newInstance() {
        SelectBaby fragment = new SelectBaby();

        Bundle args = new Bundle();
        Log.i(TAG, "newInstance: selectBaby");
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: selectBaby");

        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView: selectBaby");
        View selectBabyView = inflater.inflate(R.layout.fragment_select_baby, container, false);
        babyList = selectBabyView.findViewById(R.id.babyList);
        babyList.setOnItemClickListener(this);

        addBabyButton = selectBabyView.findViewById(R.id.newBabyButton);
        addBabyButton.setOnClickListener(event->listener.launchNewBaby());

        DatabaseReference ref = StaticFirebase.databaseReference.child(userNode).child(childNode);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Baby next = dataSnapshot.getValue(Baby.class);
                babyNames.add(next.getFirstName());
                updateSpinner();
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
        return selectBabyView;
    }

    public void onResume(){
        super.onResume();
        Log.i(TAG, "onResume: ");
        babyNames = new ArrayList();
        listener = (SelectBabyListener)getActivity();
    }


    private void updateSpinner(){
        Log.i(TAG, "updateSpinner: ");
        for (String i : babyNames){
            //Toast.makeText(getActivity(), i, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "populateList: " + i);
        }
        try {
            listViewAdapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, babyNames);
            babyList.setAdapter(listViewAdapter);
        }
        catch (Exception e){
            Log.i(TAG, "updateSpinner: exception: " + e);
        }
        // low priority - clean up ui
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String selected = (String)adapterView.getItemAtPosition(i);
        Log.i(TAG, "onItemClick: " + selected);
        listener.babySelected(selected);

    }

    public interface SelectBabyListener{
        void babySelected(String name);
        void launchNewBaby();
    }
}