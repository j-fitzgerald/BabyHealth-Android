package com.example.clyste.jf_dl_final_babyhealth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.example.clyste.jf_dl_final_babyhealth.MainActivity.SIGN_IN_RESULT;
import static com.example.clyste.jf_dl_final_babyhealth.MainActivity.TAG;

/**
 * SIGN IN WITH GOOGLE ACCOUNT
 * google login -- https://developers.google.com/identity/sign-in/android/start-integrating
 * firebase     -- https://firebase.google.com/docs/auth/android/google-signin
 * SHA-1 B1:7A:23:DB:A7:F4:FB:08:52:B5:5F:FC:57:F9:FA:05:E8:FE:C5:D7
 */
public class SignIn extends AppCompatActivity implements View.OnClickListener {
    private GoogleSignInClient googleSignInClient;
    public static final int RC_SIGN_IN = 0;
    public static final String USER_ACCOUNT = "userAccount";
    public static final String USER_NAME = "firstName";
    public static final String USER_LAST_NAME = "lastName";
    public static final String USER_EMAIL = "email";
    public static final String USER_ID = "id";
    public static final String USER_PHOTO = "photo";

    private TextView userName;
    private ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
        userName = findViewById(R.id.userName);
        userImage = findViewById(R.id.userImage);
        googleSignIn();
    }


    private void googleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null)
            Log.i(TAG, "Main->googleSignIn: user has logged in before");
        else
            updateUI(account);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "Main->HandleSignIn->signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            try {
                JSONObject userJSON = new JSONObject();
                String personName = account.getDisplayName();
                userJSON.put(USER_NAME, personName);
                String personGivenName = account.getGivenName();
                String personFamilyName = account.getFamilyName();
                userJSON.put(USER_LAST_NAME, personFamilyName);
                String personEmail = account.getEmail();
                userJSON.put(USER_EMAIL, personEmail);
                String personId = account.getId();
                userJSON.put(USER_ID, personId);
                Uri personPhoto = account.getPhotoUrl();
                userJSON.put(USER_PHOTO, personPhoto);
                userName.setText(USER_NAME + "/t" + USER_LAST_NAME);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), personPhoto);
                userImage.setImageBitmap(bitmap);
            }
            catch (JSONException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void revokeAccess() {
        googleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void returnUser(JSONObject account){
        Intent result = getIntent();
        Bundle bundle = new Bundle();
        bundle.putString(USER_ACCOUNT, account.toString());
        result.putExtras(bundle);
        setResult(SIGN_IN_RESULT, result);
        finish();
    }
}
