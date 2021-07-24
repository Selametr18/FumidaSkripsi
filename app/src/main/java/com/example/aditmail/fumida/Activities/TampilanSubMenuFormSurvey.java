package com.example.aditmail.fumida.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.aditmail.fumida.Fumigasi.TabFumigasi;
import com.example.aditmail.fumida.PestControl.TabSurveiPestControl;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.TermiteControl.TabSurveiTermiteControl;

public class TampilanSubMenuFormSurvey extends AppCompatActivity {

    ImageView image_PestControl, image_TermiteControl, image_Fumigasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_sub_menu_form_survey);

        image_PestControl = findViewById(R.id.imageView_PestControl);
        image_TermiteControl = findViewById(R.id.imageView_TermiteControl);
        image_Fumigasi = findViewById(R.id.imageView_Fumigasi);

        image_PestControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_Registrasi = new Intent(TampilanSubMenuFormSurvey.this, TabSurveiPestControl.class);
                startActivity(intent_Registrasi);
            }
        });

        image_TermiteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_Registrasi = new Intent(TampilanSubMenuFormSurvey.this, TabSurveiTermiteControl.class);
                startActivity(intent_Registrasi);
            }
        });

        image_Fumigasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_Fumigasi = new Intent(TampilanSubMenuFormSurvey.this, TabFumigasi.class);
                startActivity(intent_Fumigasi);
            }
        });
    }
}
