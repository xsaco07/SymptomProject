package com.project.symptoms.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.project.symptoms.fragment.MainMenuFragment;
import com.project.symptoms.R;
import com.project.symptoms.fragment.BodyFragment;
import com.project.symptoms.fragment.CalendarFragment;
import com.project.symptoms.view.BodyView;

public class MainActivity extends AppCompatActivity implements
        CalendarFragment.OnFragmentInteractionListener,
        BodyFragment.OnFragmentInteractionListener,
        MainMenuFragment.OnFragmentInteractionListener {

    BodyView bodyView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }


    private void launchBodySelection(){
        startActivity(new Intent(this, BodySelection.class));
    }

    private void init() {
        bodyView = findViewById(R.id.bodyView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String body = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("body_type", "None");

        if (body.equals("None")) {
            launchBodySelection();
        }

        // Make the view match the selected body type
        if(body.equals("male")) bodyView.setBodyType(BodyView.BodyType.MALE);
        else if(body.equals("female")) bodyView.setBodyType(BodyView.BodyType.FEMALE);

        // Setup the flip button
        ImageView flipButton = findViewById(R.id.flip_button);
        flipButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                bodyView.flip();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}