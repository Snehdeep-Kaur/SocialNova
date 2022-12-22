package com.example.socialnova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnv;
    FragmentContainerView fcv;
    FloatingActionButton fab;
    private String UID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        FragmentManager fm = getSupportFragmentManager();
        HomeFragment hf = new HomeFragment();
        UID = i.getStringExtra("UID");
        Bundle bundle = new Bundle();
        bundle.putString("UID",UID);
        hf.setArguments(bundle);
        CallFragment cf = new CallFragment();
        cf.setArguments(bundle);
        ProfileFragment pf = new ProfileFragment();
        pf.setArguments(bundle);
        CameraFragment cmf = new CameraFragment();
        bnv=findViewById(R.id.bottomNavigationView);
        bnv.setSelectedItemId(R.id.home);
        fab=findViewById(R.id.fab);
        fm.beginTransaction().add(R.id.fcv, hf, null).commit();

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.call:
                        fm.beginTransaction().replace(R.id.fcv, cf).commit();
                        return true;
                    case R.id.camera:
                        fm.beginTransaction().replace(R.id.fcv, cmf).commit();
                        return true;
                    case R.id.profile:
                        fm.beginTransaction().replace(R.id.fcv, pf).commit();
                        return true;
                    case R.id.home:
                        fm.beginTransaction().replace(R.id.fcv, hf).commit();
                        return true;
                }
                return false;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

    }

}
