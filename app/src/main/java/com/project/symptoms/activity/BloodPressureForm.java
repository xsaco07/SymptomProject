package com.project.symptoms.activity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.project.symptoms.db.controller.PressureController;
import com.project.symptoms.db.controller.BloodPressureLevelsController;
import com.project.symptoms.db.model.BloodPressureLevels;
import com.project.symptoms.db.model.PressureModel;
import com.project.symptoms.util.DateTimeUtils;
import com.project.symptoms.fragment.MainMenuFragment;
import com.project.symptoms.R;

import java.util.List;

public class BloodPressureForm extends AppCompatActivity implements MainMenuFragment.OnFragmentInteractionListener {

    protected Button saveButton;

    private long pressureId; // When called for edit
    private final long NO_ID = -1;
    private final long FAILURE = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure_form);

        TextView hour = findViewById(R.id.hour_pressure);
        DateTimeUtils.getInstance().registerAsTimePicker(hour);

        TextView date = findViewById(R.id.date_pressure);
        DateTimeUtils.getInstance().registerAsDatePicker(date);

        init();

        pressureId = getIntent().getLongExtra(getString(R.string.intent_key_pressure_id), NO_ID);
        if(pressureId != NO_ID){
            populateForEdit(pressureId);
        }
    }

    private void init(){

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    private void onSaveButtonClicked() {
        try{
            saveData();
        }catch (Exception e){
            String message = getString(R.string.value_saving_failed)
                    + "\n" + getString(R.string.check_input_values);
            Toast.makeText(this, message, Toast.LENGTH_LONG).
                    show();
        }
    }

    private void populateForEdit(long pressureId) {
        PressureModel model = PressureController.getInstance(this).select(pressureId);
        EditText systolicView = findViewById(R.id.systolic);
        EditText diastolicView = findViewById(R.id.diastolic);
        TextView timeView =  findViewById(R.id.hour_pressure);
        TextView dateView = findViewById(R.id.date_pressure);

        systolicView.setText(""+model.getSystolic());
        diastolicView.setText(""+model.getDiastolic());
        dateView.setText(DateTimeUtils.getInstance().DATE_FORMATTER.format(model.getDate()));
        timeView.setText(DateTimeUtils.getInstance().TIME_FORMATTER.format(model.getTime()));
    }

    private void saveData() throws Exception{
        // Gather all the data fields
        EditText systolicView = findViewById(R.id.systolic);
        EditText diastolicView = findViewById(R.id.diastolic);
        TextView hourView =  findViewById(R.id.hour_pressure);
        TextView dateView = findViewById(R.id.date_pressure);

        int systolicValue = Integer.parseInt(systolicView.getText().toString());
        int diastolicValue = Integer.parseInt(diastolicView.getText().toString());
        checkValues(systolicValue, diastolicValue);
        String time = hourView.getText().toString();
        String date = dateView.getText().toString();

        String messageToShow = "";
        long result = FAILURE;
        if(pressureId == NO_ID) {
            result = PressureController.getInstance(this).insert(systolicValue, diastolicValue, date, time);
            messageToShow = getString(R.string.value_successfully_saved);
        }
        else{
            result = PressureController.getInstance(this).update(pressureId, systolicValue, diastolicValue, date, time );
            messageToShow = getString(R.string.value_successfully_updated);
        }

        if(result == FAILURE)
            throw new Exception(getString(R.string.value_saving_failed));

        Toast.makeText(this, messageToShow, Toast.LENGTH_SHORT).show();

    }

    private boolean isBetweenBounds(int value, int min, int max){
        return min <= value && value <= max;
    }

    private boolean isPressureBetweenBothRanges(int systolicValue, int diastolicValue, BloodPressureLevels level){
        return isBetweenBounds(systolicValue, level.getSystolicMinimum(), level.getSystolicMaximum())
                && isBetweenBounds(diastolicValue, level.getDiastolicMinimum(), level.getDiastolicMaximum());
    }

    private boolean isPressureBetweenAnyRange(int systolicValue, int diastolicValue, BloodPressureLevels level){
        return isBetweenBounds(systolicValue, level.getSystolicMinimum(), level.getSystolicMaximum())
                || isBetweenBounds(diastolicValue, level.getDiastolicMinimum(), level.getDiastolicMaximum());
    }

    private BloodPressureLevels getCategoryFor(int systolicValue, int diastolicValue){
        List<BloodPressureLevels> allLevels = BloodPressureLevelsController.getInstance(BloodPressureForm.this).listAll();
        BloodPressureLevels lastLevelThatMatched = null;
        boolean matches = false;
        for(BloodPressureLevels level : allLevels){
            if(level.getCategory().equals(getString(R.string.blood_pressure_category_hypotension)))
                matches = isPressureBetweenAnyRange(systolicValue, diastolicValue, level);

            else if(level.getCategory().equals(getString(R.string.blood_pressure_category_normal)))
                matches = isPressureBetweenBothRanges(systolicValue, diastolicValue, level);

            else if(level.getCategory().equals(getString(R.string.blood_pressure_category_elevated)))
                matches = isPressureBetweenBothRanges(systolicValue, diastolicValue, level);

            else if(level.getCategory().equals(getString(R.string.blood_pressure_category_hypertension_stage_1)))
                matches = isPressureBetweenAnyRange(systolicValue, diastolicValue, level);

            else if(level.getCategory().equals(getString(R.string.blood_pressure_category_hypertension_stage_2)))
                matches = isPressureBetweenAnyRange(systolicValue, diastolicValue, level);

            if (matches)
                lastLevelThatMatched = level;
        }
        return lastLevelThatMatched;
    }

    private void checkValues(int systolicValue, int diastolicValue) {
        BloodPressureLevels levelMatched = getCategoryFor(systolicValue, diastolicValue);
        if(levelMatched == null)
            return;

        String category = levelMatched.getCategory();
        String normal = getString(R.string.blood_pressure_category_normal);

        if(! category.equals(normal)){
            String message = String.format(
                    getString(R.string.blood_pressure_alert_message),
                    levelMatched.getCategory());
            showAlertDialogWith(message);
        }
    }

    private void showAlertDialogWith(String message) {
        AlertDialog.Builder pressureAlert  = new AlertDialog.Builder(BloodPressureForm.this);
        pressureAlert.setMessage(message);
        pressureAlert.setCancelable(false)
                .setNeutralButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = pressureAlert.create();
        alertDialog.setTitle(R.string.warning);
        alertDialog.show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
