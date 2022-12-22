package com.example.socialnova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {
    private SharedPreferences pref;
    private static final String FILE_NAME = "LoginActivity";
    private String PARAM_ONE = "userLogState";
    private String PARAM_TWO = "profileId";
    String log,uid;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        log = preferences.getString(PARAM_ONE,null);
        uid = preferences.getString(PARAM_TWO,null);
        if(log==null||uid==null){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PARAM_ONE,"none");
            editor.putString(PARAM_TWO,"none");
            editor.clear();
            editor.commit();
            log = preferences.getString(PARAM_ONE,null);
            uid = preferences.getString(PARAM_TWO,null);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(log.contains("logged_in")){
                    Intent i = new Intent(SplashScreen.this,MainActivity.class);
                    i.putExtra("UID",uid);
                    startActivity(i);
                    finish();
                }
                else{
                    startActivity(new Intent(SplashScreen.this,Register.class));
                    finish();
                }

            }
        }, 1000);
    }
}