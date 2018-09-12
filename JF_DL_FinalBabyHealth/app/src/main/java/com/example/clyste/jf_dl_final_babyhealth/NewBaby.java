package com.example.clyste.jf_dl_final_babyhealth;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.clyste.jf_dl_final_babyhealth.MainActivity.TAG;

public class NewBaby extends Fragment {

    private ArrayList<EditText> editFields;
    private EditText firstName;
    private EditText lastName;
    private EditText birthday;
    private RadioButton maleRadio;

    private Button submitButton;

    private JSONObject babyJSON;
    private Baby baby;
    private NewBabyListener listener;
    private String childNode = "BabyNames";
    private String userNode = StaticFirebase.uid;
    private String gender;
    private SimpleDateFormat dateFormat;

    public NewBaby() {
        // Required empty public constructor
    }

    public static NewBaby newInstance() {
        NewBaby fragment = new NewBaby();
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
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        View newBabyView = inflater.inflate(R.layout.fragment_new_baby, container, false);
        firstName = newBabyView.findViewById(R.id.editFirstName);
        lastName = newBabyView.findViewById(R.id.editLastName);
        birthday = newBabyView.findViewById(R.id.editBirthday);
        maleRadio = newBabyView.findViewById(R.id.radioMale);


        submitButton = newBabyView.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(event->submit());

        editFields = new ArrayList();
        editFields.add(firstName);
        editFields.add(lastName);
        editFields.add(birthday);

        listener = (NewBabyListener)getActivity();

        return newBabyView;
    }

    private void submit(){
        Log.i(TAG, "submit: pressed");
        if (notEmpty())
            saveToFirebase();
        else
            Toast.makeText(this.getActivity(), "Fields empty", Toast.LENGTH_SHORT).show();
    }

    private boolean notEmpty(){
        babyJSON = new JSONObject();
        baby = new Baby();
        for (EditText field:editFields){
            try {
                if (field.getText().toString().isEmpty())
                    return false;
                babyJSON.put("firstName", field.getText());
                baby.setFirstName(firstName.getText().toString());
                baby.setLastName(lastName.getText().toString());
            }
            catch(JSONException e){
                Log.i(TAG, "notEmpty: jsonException " + e);
            }
        }
        Log.i(TAG, "notEmpty: data fields not empty");
        return true;
    }

    private void saveToFirebase(){
        Date formattedBirthday = new Date();
        try {
            formattedBirthday = dateFormat.parse(birthday.getText().toString());
        }
        catch(ParseException e){
            Log.i(TAG, "saveToFirebase: Date parse exception: " + e);
        }
        if (maleRadio.isChecked())
            gender = "Male";
        else
            gender = "Female";
        StaticFirebase.databaseReference.getRef().child(userNode).child(childNode).push().setValue(new Baby(firstName.getText().toString(), lastName.getText().toString(),gender, formattedBirthday ));
        Log.i(TAG, "saveToFirebase:success " + baby.getFirstName() + "\n\t" + baby);
        listener.returnToBabyList();
    }

    public interface NewBabyListener{
        public void returnToBabyList();
    }
}