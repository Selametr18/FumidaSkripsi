package com.example.aditmail.fumida.Fumigasi;

import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.widget.Toast;

import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.SectionPageAdapter;

public class TabFumigasi extends AppCompatActivity {

    //private static final String TAG = "MainTab";
    protected SectionPageAdapter mSectionPageAdapter;
    protected ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_fumigasi);

        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentFormFumigasi(), "Form Survei");
        adapter.addFragment(new FragmentDataFumigasi(), "Data Survei");
        viewPager.setAdapter(adapter);
    }

    boolean doubleBackToExit = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExit){
            super.onBackPressed();
            return;
        }

        this.doubleBackToExit = true;
        Toast.makeText(this, "Tekan Tombol 'Back' Lagi untuk Keluar Form Survei", Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExit=false;
            }
        },2000);
    }
}
