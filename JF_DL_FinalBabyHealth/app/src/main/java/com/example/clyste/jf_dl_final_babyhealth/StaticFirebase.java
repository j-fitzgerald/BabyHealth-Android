package com.example.clyste.jf_dl_final_babyhealth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Clyste on 4/17/2018.
 */

public class StaticFirebase {
    //public static com.google.firebase.auth.StaticFirebase user;
    public static FirebaseUser user;
    public static FirebaseDatabase database;
    public static DatabaseReference databaseReference;
    public static String uid;

    public static void initialize(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
    }

    public static void setUser(FirebaseUser setUser){
        user = setUser;
        uid = user.getUid();
    }

    public static void setDatabase(FirebaseDatabase setDatabase){
        database = setDatabase;
    }

    public static void setReference(DatabaseReference setReference){
        databaseReference = setReference;
    }


}
