package com.example.aditmail.fumida.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.aditmail.fumida.R;


public class Tampilan_Awal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_awal);

        int TIME_OUT = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Tampilan_Awal.this, TampilanLogin.class);
                startActivity(i);
                finish();
            }
        }, TIME_OUT);
    }
}
