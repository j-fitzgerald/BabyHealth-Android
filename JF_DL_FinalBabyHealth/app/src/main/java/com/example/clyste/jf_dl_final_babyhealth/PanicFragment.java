package com.example.clyste.jf_dl_final_babyhealth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.clyste.jf_dl_final_babyhealth.UserPreferences.JSON_FILENAME;
import static com.example.clyste.jf_dl_final_babyhealth.UserPreferences.BUMP;
import static com.example.clyste.jf_dl_final_babyhealth.UserPreferences.KELLY;
import static com.example.clyste.jf_dl_final_babyhealth.UserPreferences.WEB_MD;
import static com.example.clyste.jf_dl_final_babyhealth.UserPreferences.EXPECT;
import static com.example.clyste.jf_dl_final_babyhealth.UserPreferences.WEBSITE_PREFERENCE;

public class PanicFragment extends Fragment{
    static final String WEB_MD_QUERY = "https://www.webmd.com/search/search_results/default.aspx?query=";
    public static final String THE_BUMP_QUERY = "https://www.thebump.com/search?q=";
    public static final String KELLY_MOM_QUERY = "https://kellymom.com/?s=";
    public static final String WHAT_TO_EXPECT_QUERY = "https://www.whattoexpect.com/search?q=fever";
    public static final String DUCK_DUCK = "https://duckduckgo.com/?q=";

    private String preferredSite;
    private TextView preferredSiteName;
    private JSONObject userJSON;
    private ListView symptomList;
    private ArrayList symptoms;
    private EditText customSearch;

    private String query;

    public PanicFragment() {
        // Required empty public constructor
    }

    public static PanicFragment newInstance() {
        PanicFragment fragment = new PanicFragment();
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
        View panicView =  inflater.inflate(R.layout.fragment_panic, container, false);
        preferredSiteName = panicView.findViewById(R.id.preferredSiteName);
        symptomList = panicView.findViewById(R.id.symptomList);
        symptomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("JSF-PANIC-SEARCH", "onItemClick: " + symptomList.getItemAtPosition(position).toString());
                query = symptomList.getItemAtPosition(position).toString();
                performQuery();
            }
        });
        customSearch = panicView.findViewById(R.id.customSearch);
        customSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    String customString = customSearch.getText().toString();
                    if (customString.matches(""))
                        return;
                    query = customString;
                    performQuery();
                }
            }
        });
        symptoms = new ArrayList();
        populateSymptoms();
        symptomList.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, symptoms));

        userJSON = load();
        try {
            if (userJSON != null)
                preferredSite = userJSON.getString(WEBSITE_PREFERENCE);
        }
        catch(JSONException e) {
            Log.i("JSF", "onCreateView: " + e);
        }
        if (preferredSite == null)
            preferredSite = WEB_MD;
        preferredSiteName.setText(preferredSite);
        return panicView;
    }

    private void populateSymptoms(){
        for (String i : getResources().getStringArray(R.array.symptoms)){
            symptoms.add(i);
        }
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

    private void performQuery(){
        String base = "";
        switch(preferredSite){
            case(WEB_MD):
                base = WEB_MD_QUERY;
                break;
            case(BUMP):
                base = THE_BUMP_QUERY;
                break;
            case(KELLY):
                base = KELLY_MOM_QUERY;
                break;
            case(EXPECT):
                base = WHAT_TO_EXPECT_QUERY;
                break;
            default:
                base = DUCK_DUCK;
        }
        query = base + query;
        //https://stackoverflow.com/questions/2201917/how-can-i-open-a-url-in-androids-web-browser-from-my-application
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(query));
        startActivity(browserIntent);

    }
}
