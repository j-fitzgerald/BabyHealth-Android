package com.example.clyste.jf_dl_final_babyhealth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.clyste.jf_dl_final_babyhealth.MainActivity.TAG;
import static com.example.clyste.jf_dl_final_babyhealth.MainActivity.FEEDING_EVENT;
import static com.example.clyste.jf_dl_final_babyhealth.MainActivity.DIAPER_EVENT;
import static com.example.clyste.jf_dl_final_babyhealth.MainActivity.eventTypes;
import static com.example.clyste.jf_dl_final_babyhealth.UserPreferences.JSON_FILENAME;
import static com.example.clyste.jf_dl_final_babyhealth.UserPreferences.VOLUME_PREFERENCE;

public class CreateNewEvent extends Fragment {

    public static final String BABY_NAME = "BABY_NAME";
    public static final String EVENT_KEY = "EVENT_KEY";
    public static final String VOLUME_KEY = "VOLUME_KEY";
    public static final long MAX_TIME = 100*60000;

    private TextView duration;
    private TextView eventTitle;
    private TextView headerText;
    private EditText editVolume;
    private FeedingBabyEvents feedingEvent;
    private DiaperEvent diaperEvent;
    private Button startButton;
    private Button endButton;
    private Button homeButton;
    private Button toggleButton;
    private String babyNode;
    private String userNode = StaticFirebase.uid;
    private String eventName;
    private ConstraintLayout feedingLayout;
    private ConstraintLayout diaperLayout;
    private LinearLayout poopColorsLayout;
    private ArrayList<CheckBox>colorBoxesList;
    private CheckBox peeBox;
    private CheckBox poopBox;
    private CreateNewEventListener listener;


    public CreateNewEvent() {
        // Required empty public constructor
    }

    public static CreateNewEvent newInstance(String babyName, String eventType) {
        CreateNewEvent fragment = new CreateNewEvent();
        Bundle args = new Bundle();
        args.putString(BABY_NAME, babyName);
        args.putString(EVENT_KEY, eventType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null){
            babyNode = getArguments().getString(BABY_NAME);
            eventName = getArguments().getString(EVENT_KEY);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View createNewEventView = inflater.inflate(R.layout.fragment_create_new_event, container, false);
        feedingLayout = createNewEventView.findViewById(R.id.feedingFrame);
        diaperLayout = createNewEventView.findViewById(R.id.diaperFrame);
        colorBoxesList = new ArrayList();
        peeBox = createNewEventView.findViewById(R.id.peeBox);
        poopBox = createNewEventView.findViewById(R.id.poopBox);

        listener = (CreateNewEventListener)getActivity();

        duration = createNewEventView.findViewById(R.id.textDuration);
        eventTitle = createNewEventView.findViewById(R.id.eventTitleText);
        headerText = createNewEventView.findViewById(R.id.eventHeaderText);
        editVolume = createNewEventView.findViewById(R.id.editFeedingVolume);
        startButton = createNewEventView.findViewById(R.id.feedingStartButton);
        startButton.setOnClickListener(event -> onButtonClick(startButton, eventName));
        endButton = createNewEventView.findViewById(R.id.feedingStopButton);
        endButton.setOnClickListener(event->onButtonClick(endButton, eventName));
        homeButton = createNewEventView.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(event->listener.returnHome());
        toggleButton = createNewEventView.findViewById(R.id.eventToggleButton);
        toggleButton.setOnClickListener(event->toggleEvent());
        eventTypes = getActivity().getResources().getStringArray(R.array.eventTypesArray);
        poopColorsLayout = createNewEventView.findViewById(R.id.poopColorsLayout);

        populateColors();

        setupScreen(eventName.matches(eventTypes[FEEDING_EVENT]));
        return createNewEventView;
    }

    private void populateColors(){
        String[] colors = getResources().getStringArray(R.array.colors);
        for (String color: colors){
            CheckBox nextBox = new CheckBox(getContext());
            nextBox.setText(color);
            poopColorsLayout.addView(nextBox);
            colorBoxesList.add(nextBox);
        }
    }

    private void setupScreen(boolean feeding){
        Log.i(TAG, "setupScreen: feeding==" + feeding + "\teventName: " + eventName);
        displayFeeding(feeding);
        displayDiaper(!feeding);
    }

    private void displayFeeding(boolean display){
        if (display) {
            eventName = eventTypes[FEEDING_EVENT];
            feedingLayout.setVisibility(View.VISIBLE);
            toggleButton.setText(getResources().getString(R.string.diaperTitle));
            eventTitle.setText(getResources().getString(R.string.feedingTitle));
            endButton.setEnabled(false);
            startButton.setEnabled(true);
        }
        else
            feedingLayout.setVisibility(View.INVISIBLE);
    }

    private void displayDiaper(boolean display){
        if (display) {
            eventName = eventTypes[DIAPER_EVENT];
            diaperLayout.setVisibility(View.VISIBLE);
            toggleButton.setText(getResources().getString(R.string.feedingTitle));
            eventTitle.setText(getResources().getString(R.string.diaperTitle));
            endButton.setEnabled(true);
        }
        else
            diaperLayout.setVisibility(View.INVISIBLE);
    }



    public void onButtonClick(View view, String eventName) {
        if (eventName == eventTypes[FEEDING_EVENT])
            processFeeding(view);
        else if (eventName == eventTypes[DIAPER_EVENT])
            processDiaper();
    }

    private void processFeeding(View view){
        if (view == startButton){
            Log.i(TAG, "onClick: start feeding");
            feedingEvent = new FeedingBabyEvents();
            feedingEvent.setStartTime();
            feedingEvent.setDate();
            toggleButton.setEnabled(false);
            endButton.setEnabled(true);
            startButton.setEnabled(false);
            startTimer();
        }
        else if (view == endButton){
            if (editVolume.getText().toString().matches("")){
                Toast.makeText(getActivity(), getResources().getString(R.string.enterVolume), Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i(TAG, "onClick: end feeding");
            feedingEvent.setEndTime();
            feedingEvent.setUnits(getVolumeUnits());
            Log.i(TAG, "processFeeding: " + feedingEvent.getUnits());
            if (editVolume.getText().toString().matches("")){
                Log.i(TAG, "onButtonClick: no volume");
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.invalidVolume),Toast.LENGTH_SHORT);
                return;
            }
            feedingEvent.setVolume(Integer.parseInt(editVolume.getText().toString()));
            publishEvent(FEEDING_EVENT, feedingEvent);
        }
        else{
            Log.i(TAG, "onClick: nothing " + view);
        }
    }

    /* https://stackoverflow.com/questions/17480936/how-to-display-the-timer-in-android */
    private void startTimer(){
        CountDownTimer timer = new CountDownTimer(MAX_TIME, 1000) {

            public void onTick(long millisUntilFinished) {
                duration.setText("Duration: " + new SimpleDateFormat("mm:ss").format(new Date( MAX_TIME - millisUntilFinished)));
            }

            public void onFinish() {
                duration.setText("done!");
            }
        }.start();
    }

    private void processDiaper(){
        diaperEvent= new DiaperEvent();
        diaperEvent.setPee(peeBox.isChecked());
        diaperEvent.setPoop(poopBox.isChecked());
        diaperEvent.setStartTime();
        diaperEvent.setDate();
        for (CheckBox colorBox : colorBoxesList){
            if (colorBox.isChecked())
                diaperEvent.addColor(colorBox.getText().toString());
            else
                diaperEvent.removeColor(colorBox.getText().toString());
        }
        diaperEvent.setEndTime();
        publishEvent(DIAPER_EVENT, diaperEvent);
    }

    private void publishEvent(int eventNumber, BabyEvents eventObject){
        eventObject.setRecordedBy(StaticFirebase.user.getDisplayName());
        StaticFirebase.databaseReference.getRef().child(userNode).child(babyNode).child(eventTypes[eventNumber]).push().setValue(eventObject);
        Log.i(TAG, "saveToFirebase:success ");
        listener.returnHome();
    }



    private void toggleEvent(){
        setupScreen(eventName != eventTypes[FEEDING_EVENT]);
    }

    public interface CreateNewEventListener{
        public void returnHome();
    }


    public String getVolumeUnits(){
        JSONObject user = load();
        if (user != null){
            try{
                Log.i(TAG, "getVolumeUnits: read user --> " + user.getString(VOLUME_PREFERENCE));
                return user.getString(VOLUME_PREFERENCE);
            }
            catch(JSONException e){
                Log.i(TAG, "getVolumeUnits: " + e);
            }
        }
        return getResources().getString(R.string.ounces);
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
}
