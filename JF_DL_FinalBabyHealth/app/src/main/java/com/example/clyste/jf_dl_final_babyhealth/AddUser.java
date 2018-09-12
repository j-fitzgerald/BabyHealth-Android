package com.example.clyste.jf_dl_final_babyhealth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class AddUser extends Fragment {

    private Button backButton;
    private Button submitButton;
    private EditText emailText;
    private AddUserListener listener;
    private String childNode = "BabyNames";
    private String userNode = StaticFirebase.uid;
    private ChildEventListener childEventListener;
    private ArrayList<String> babyKeys;


    public AddUser() {
        // Required empty public constructor
    }

    public static AddUser newInstance() {
        AddUser fragment = new AddUser();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View addUserView =  inflater.inflate(R.layout.fragment_add_user, container, false);
        listener = (AddUserListener)getActivity();
        backButton = addUserView.findViewById(R.id.backButton);
        backButton.setOnClickListener(event->listener.returnHome());
        submitButton = addUserView.findViewById(R.id.addUserSubmitButton);
        submitButton.setOnClickListener(event->submitEmail());
        emailText = addUserView.findViewById(R.id.editEmail);
        babyKeys = new ArrayList();

        DatabaseReference ref = StaticFirebase.databaseReference.child(userNode).child(childNode);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                babyKeys.add(dataSnapshot.getKey());
                Log.i("JSF", "onChildAdded: added to array:\n\t " + dataSnapshot.getKey() + "\n\t"+dataSnapshot.getValue());


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(childEventListener);
        return addUserView;
    }

    private void submitEmail(){
        String email = emailText.getText().toString();
        if (email.matches("") || !(email.contains("@"))) {
            Toast.makeText(getActivity(), getResources().getString(R.string.invalidEmail), Toast.LENGTH_SHORT).show();
            return;
        }

        email = email.replaceAll("\\W", "");

        StaticFirebase.databaseReference.getRef().child(email).push().setValue(StaticFirebase.user.getUid());
        listener.returnHome();

    }

    public interface AddUserListener{
        public void returnHome();
    }

}
