package com.project.symptoms.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.symptoms.util.DateTimeUtils;
import com.project.symptoms.fragment.MainMenuFragment;
import com.project.symptoms.R;

public class BloodPressureForm extends AppCompatActivity implements MainMenuFragment.OnFragmentInteractionListener {

    protected Toolbar toolbar;
    protected Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure_form);
        init();


        TextView hour = findViewById(R.id.hour_pressure);
        DateTimeUtils.getInstance().registerAsTimePicker(hour);

        TextView date = findViewById(R.id.date_pressure);
        DateTimeUtils.getInstance().registerAsDatePicker(date);

    }

    private void init(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    private void saveData(){

        // TODO: Save data here

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        String text = "Data registered";
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(getApplicationContext(), text, duration).show();
        startActivity(mainActivityIntent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}