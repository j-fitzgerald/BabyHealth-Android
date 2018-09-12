package com.example.clyste.jf_dl_final_babyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static com.example.clyste.jf_dl_final_babyhealth.MainActivity.TAG;

public class UserPreferences extends Fragment {
    public static final String JSON_FILENAME = "myPreferences";
    public static final String VOLUME_PREFERENCE = "VolumeUnits";
    public static final String WEBSITE_PREFERENCE = "Website";

    public static final String WEB_MD = "Web MD";
    public static final String BUMP = "The Bump";
    public static final String EXPECT = "What To Expect";
    public static final String KELLY = "Kelly Mom";

    private Button choose;
    public static final int GALLERY_REQUEST =123;
    ImageView photo;

    private Button save;
    private RadioButton ounces;
    private RadioButton milliliters;
    private JSONObject userJSON;
    private RadioButton webMD;
    private RadioButton bump;
    private RadioButton kelly;
    private RadioButton expect;
    private UserPreferencesListener listener;

    public UserPreferences() {
        // Required empty public constructor
    }

    public static UserPreferences newInstance() {
        UserPreferences fragment = new UserPreferences();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View preferencesView = inflater.inflate(R.layout.fragment_user_preferences, container, false);
        choose = preferencesView.findViewById(R.id.choosePhotoButton);
        choose.setOnClickListener(event->getPicture(choose));
        photo = preferencesView.findViewById(R.id.photo);
        userJSON = load();
        if (userJSON == null){
            userJSON = new JSONObject();
        }
        save = preferencesView.findViewById(R.id.save);
        save.setOnClickListener(event->saveData());
        ounces = preferencesView.findViewById(R.id.radioOunces);
        milliliters = preferencesView.findViewById(R.id.radioMilli);

        webMD = preferencesView.findViewById(R.id.webMDRadio);
        bump = preferencesView.findViewById(R.id.bumpRadio);
        kelly  = preferencesView.findViewById(R.id.kellyRadio);
        expect = preferencesView.findViewById(R.id.expectRadio);

        listener = (UserPreferencesListener)getActivity();

        return preferencesView;
    }

    public JSONObject load(){
        JSONObject userData = new JSONObject();
        try{
            BufferedReader jsonFile = new BufferedReader(new FileReader(getActivity().getFilesDir() + "/" + JSON_FILENAME));
            userData = new JSONObject(jsonFile.readLine());
            Log.i("jsf", "getUserJSON: \n" + userData.toString());
        }
        catch (FileNotFoundException e){
            Log.i("jsf", "readUserData: File Not Found: "  + JSON_FILENAME);
            return null;
        }
        catch (IOException e){
            Log.i("jsf", "readUserData: IO: " + e);
        }
        catch (Exception e){
            Log.i("jsf", "readUserData: other exception " + e);
        }
        return userData;

    }


    private void saveData(){
        Log.i("Save", "saveData: dir Before save: " + getContext().getFilesDir());
        addDataToJSON();
        try {
            FileWriter userFile = new FileWriter(getContext().getFilesDir() + "/" + JSON_FILENAME);
            userFile.write(userJSON.toString());
            userFile.close();
            Log.i(TAG, "saveData: save success\n" + getContext().getFilesDir() + JSON_FILENAME);
            Toast.makeText(getActivity(), getResources().getString(R.string.saveSuccess), Toast.LENGTH_SHORT).show();
            listener.returnHome();
        }
        catch(IOException e){
            Log.d(TAG, "saveData: IOException " + e);
        }
        catch(Exception e){
            Log.d(TAG, "saveData: Other Exceptions:" + e);
        }
    }

    private void addDataToJSON(){
        try {
            if (ounces.isChecked())
                userJSON.put(VOLUME_PREFERENCE, getResources().getString(R.string.ounces));
            else
                userJSON.put(VOLUME_PREFERENCE, getResources().getString(R.string.milliliters));
            if (webMD.isChecked())
                userJSON.put(WEBSITE_PREFERENCE, WEB_MD);
            else if(kelly.isChecked())
                userJSON.put(WEBSITE_PREFERENCE, KELLY);
            else if(expect.isChecked())
                userJSON.put(WEBSITE_PREFERENCE, EXPECT);
            else
                userJSON.put(WEBSITE_PREFERENCE, BUMP);
        }
        catch(JSONException e){
            Log.i(TAG, "addDataToJSON: " + e);
        }
        catch(Exception e){
            Log.i(TAG, "addDataToJSON: " + e);
        }
    }

    public interface UserPreferencesListener{
        public void returnHome();
    }

    public void getPicture(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        photo.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
    }
}
