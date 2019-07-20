package com.example.front_ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class SubSearchPage extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private FragmentStore fragmentStore = new FragmentStore();
    private FragmentRegion fragmentRegion = new FragmentRegion();
    private FragmentTag fragmentTag = new FragmentTag();
    private FragmentPeople fragmentPeople = new FragmentPeople();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_search_page);

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_bar);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragmentStore).commitAllowingStateLoss();

        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.nav_store: {
                        transaction.replace(R.id.frame_layout, fragmentStore).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.nav_region: {
                        transaction.replace(R.id.frame_layout, fragmentRegion).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.nav_tag: {
                        transaction.replace(R.id.frame_layout, fragmentTag).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.nav_people: {
                        transaction.replace(R.id.frame_layout, fragmentPeople).commitAllowingStateLoss();
                        break;
                    }
                }

                return true;
            }
        });
    }
}
