package com.project.symptoms.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.project.symptoms.R;
import com.project.symptoms.db.controller.SymptomCategoryController;
import com.project.symptoms.db.controller.SymptomCategoryOptionController;

public class BodySelection extends FragmentActivity{

    int selectedColor = Color.parseColor("#8DBF41");
    int normalColor = Color.parseColor("#d6d7d7");
    SymptomCategoryController symptomCategoryController;
    SymptomCategoryOptionController symptomCategoryOptionController;

    // Rows
    final int FEMALE = 0;
    final int MALE = 1;

    // Columns
    final int KEYWORD = 0;
    final int NAME = 1;


    int selectedBodyType = MALE; // Be MALE OF FEMALE

    String bodyTypes[][] = {
            {"female","Mujer"},
            {"male","Hombre"}
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.body_selection);

        final ImageButton maleButton = findViewById(R.id.male_button);
        final ImageButton femaleButton = findViewById(R.id.female_button);

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedBodyType = FEMALE;
                v.setBackground(getResources().getDrawable(R.drawable.body_selection_background1, getTheme()));
                maleButton.setBackground(getResources().getDrawable(R.drawable.gradient_background, getTheme()));
            }
        });

        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedBodyType = MALE;
                v.setBackground(getResources().getDrawable(R.drawable.body_selection_background1, getTheme()));
                femaleButton.setBackground(getResources().getDrawable(R.drawable.gradient_background, getTheme()));
                // Swap color
                // v.setBackgroundColor(selectedColor);
                // femaleButton.setBackgroundColor(normalColor);
            }
        });

        Button continueButton = findViewById(R.id.continue_button);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor =  prefs.edit();
                editor.putString("body_type", bodyTypes[selectedBodyType][KEYWORD]);
                editor.apply();

                Intent intent = new Intent(BodySelection.this, MainActivity.class);
                startActivity(intent);
            }
        });

        initialDBInsertion();

    }

    // Perform initial DB insertion (symptom categories and symptom category options)
    // Hast to be in that order > 1. SymptomCategory 2. SymptomCategoryOption
    private void initialDBInsertion(){
        symptomCategoryController = SymptomCategoryController.getInstance(this);
        symptomCategoryController.insert();
        symptomCategoryOptionController = SymptomCategoryOptionController.getInstance(this);
        symptomCategoryOptionController.insert();
    }
}

