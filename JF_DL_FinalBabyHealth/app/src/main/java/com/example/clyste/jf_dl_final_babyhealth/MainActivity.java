package com.example.clyste.jf_dl_final_babyhealth;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

/**
 * MAIN ACTIVITY -- USER SIGN IN then launch HOME
 * 1 Get Login
 * 2 Login
 * 3 Select Baby
 * 4 GridHome
 */
public class MainActivity extends AppCompatActivity implements SelectBaby.SelectBabyListener, NewBaby.NewBabyListener, CreateNewEvent.CreateNewEventListener, AddUser.AddUserListener, HomeGrid.HomeGridListener, UserPreferences.UserPreferencesListener{

    public static final String TAG = "JF_DL_LOG";
    public static final int SIGN_IN_RESULT = 1;
    private static final int RC_SIGN_IN = 123;
    public static final int FEEDING_EVENT =  0;
    public static final int DIAPER_EVENT = 1;
    public static String[] eventTypes;




    private String selectedBabyName;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        eventTypes = getResources().getStringArray(R.array.eventTypesArray);
        if (savedInstanceState == null)
            launchFirebaseSignIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // firebase
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                StaticFirebase.initialize();
                Log.i(TAG, "onActivityResult: firebase login success " + StaticFirebase.user.toString());
                launchSelection();
            } else {
                // Sign in failed, check response for error code
                Log.i(TAG, "onActivityResult: firebase login fail");
            }
        }

    }

    private void launchFirebaseSignIn(){
        // Create and launch sign-in intent
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
                RC_SIGN_IN);
    }

    private void launchSelection(){
        SelectBaby select = SelectBaby.newInstance();
        Log.i(TAG, "launchSelection: launch select baby");
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, select).commit();
    }

    @Override
    public void babySelected(String name) {
        selectedBabyName = name;
        HomeGrid grid = HomeGrid.newInstance();
        launchFragment(grid);
    }

    @Override
    public void launchNewBaby() {
        NewBaby newBaby = NewBaby.newInstance();
        launchFragment(newBaby);
    }

    @Override
    public void returnToBabyList() {
        launchSelection();
    }

    @Override
    public void returnHome() {
        babySelected(selectedBabyName);
    }

    @Override
    public void launchEvents() {
        CreateNewEvent createNewEvent = CreateNewEvent.newInstance(selectedBabyName, eventTypes[FEEDING_EVENT]);
        launchFragment(createNewEvent);
    }

    @Override
    public void launchGraph() {
        GraphFragment graphFragment = GraphFragment.newInstance(selectedBabyName, "feeding");
        launchFragment(graphFragment);
    }

    @Override
    public void launchTimer() {
        TimerFragment timerFragment = TimerFragment.newInstance();
        launchFragment(timerFragment);
    }

    @Override
    public void launchPreferences() {
        UserPreferences userPreferences = UserPreferences.newInstance();
        launchFragment(userPreferences);
    }

    public void launchFragment(Fragment fragment){
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }

    public void launchPanic(){
        PanicFragment panicFragment = PanicFragment.newInstance();
        launchFragment(panicFragment);
    }

}
