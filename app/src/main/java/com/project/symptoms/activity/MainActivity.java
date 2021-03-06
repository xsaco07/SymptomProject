package com.project.symptoms.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.symptoms.db.controller.SelectedCategoryOptionController;
import com.project.symptoms.db.controller.SymptomController;
import com.project.symptoms.db.model.SymptomModel;
import com.project.symptoms.dialog.CircleSizeSelectionDialog;
import com.project.symptoms.dialog.DateRangeForPDFDialog;
import com.project.symptoms.fragment.MainMenuFragment;
import com.project.symptoms.R;
import com.project.symptoms.fragment.BodyFragment;
import com.project.symptoms.fragment.CalendarFragment;
import com.project.symptoms.util.DateTimeUtils;
import com.project.symptoms.util.PDFGenerator;
import com.project.symptoms.view.BodyView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        CalendarFragment.OnFragmentInteractionListener,
        BodyFragment.OnFragmentInteractionListener,
        MainMenuFragment.OnFragmentInteractionListener,
        CircleSizeSelectionDialog.OnCircleSizeSelectedListener,
        CircleSizeSelectionDialog.OnCircleSizeUpdatedListener,
        DateRangeForPDFDialog.DateRangeDialogListener{

    private static final int STORAGE_CODE = 100;
    private final long DEFAULT_SELECTED_SYMPTOM_ID_VALUE = -1;
    private BodyView bodyView;
    private Toolbar toolbar;
    private CircleSizeSelectionDialog sizeSelectionDialog;
    private BodyView.Circle currentCircle;
    private TextView dateTextView;
    private ImageView yesterdayButton;
    private ImageView tomorrowButton;
    private int currentBodySide;
    private long nearestSymptomToSelectedId; // Holds the id for the symptom the user long presses
    private float posXOnTouch, posYOnTouch;
    private int r, g, b; // Color code for the pixel where the user touched

    private final int RED_IN_3_CIRCLES_JOINT = 246;
    private final int RED_INSIDE_OF_BODY = 237;
    private final int RED_IN_2_CIRCLES_JOINT = 242;
    private final int RED_OUTSIDE_OF_BODY = 255;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        init();
        try {
            updateSymptomsInBodyView();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (getIntent().getBooleanExtra(getResources().getString(R.string.bundle_key), false)){
            showDateRangeDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // This is needed to be done again because of the context used in DateTimeUtils
        // changes when another view is also registered as picker
        // TODO FIX THIS ISSUE in DateTimeUtils
        DateTimeUtils.getInstance().registerAsDatePicker(dateTextView);
    }

    private void launchBodySelection() {
        startActivity(new Intent(this, BodySelection.class));
    }

    private void init() {
        posXOnTouch = -1;
        posYOnTouch = -1;
        r = -1;
        g = -1;
        b = -1;

        bodyView = findViewById(R.id.bodyView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String bodyTypeKey = getString(R.string.preference_selected_body_type_key);
        final String body = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(bodyTypeKey, "None");

        String male = getString(R.string.preference_selected_body_type_male);
        String female = getString(R.string.preference_selected_body_type_female);
        // Make the view match the selected body type
        if(body.equals(male))
            bodyView.setBodyType(BodyView.BodyType.MALE);
        else if(body.equals(female))
            bodyView.setBodyType(BodyView.BodyType.FEMALE);
        else
            launchBodySelection();

        // Setup the flip button
        ImageView flipButton = findViewById(R.id.flip_button);
        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bodyView.flip();
                try {
                    updateSymptomsInBodyView();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        dateTextView = findViewById(R.id.current_date);
        yesterdayButton = findViewById(R.id.left_button);
        tomorrowButton = findViewById(R.id.right_button);
        DateTimeUtils.getInstance().registerAsDatePicker(dateTextView);

        // Capture when text view for the date changes to be able to update the symptoms showed in the body view
        dateTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    updateSymptomsInBodyView();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        yesterdayButton.setOnClickListener(v -> {
            try {
                goToYesterday(v);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        tomorrowButton.setOnClickListener(v -> {
            try {
                goToTomorrow(v);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        nearestSymptomToSelectedId = DEFAULT_SELECTED_SYMPTOM_ID_VALUE;
        registerForContextMenu(bodyView);

    }

    public void goToYesterday(View view) throws ParseException {
        Date yesterdaysDate = DateTimeUtils.getInstance().getYesterdaysDateFromString(dateTextView.getText().toString());
        String yesterdayDateString = DateTimeUtils.getInstance().getStringDateFromDate(yesterdaysDate);
        dateTextView.setText(yesterdayDateString);
        updateSymptomsInBodyView();
    }

    public void goToTomorrow(View view) throws ParseException {
        Date tomorrowsDate = DateTimeUtils.getInstance().getTomorrowsDateFromString(dateTextView.getText().toString());
        String tomorrowDateString = DateTimeUtils.getInstance().getStringDateFromDate(tomorrowsDate);
        dateTextView.setText(tomorrowDateString);
        updateSymptomsInBodyView();
    }

    // Get all symptoms for current date from DB and add them to the BodyView
    private void updateSymptomsInBodyView() throws ParseException {
        currentBodySide = (bodyView.getState() == BodyView.State.BACK) ? 0 : 1;

        // Get current date from text view to filter the data in DB
        Date currentDate = DateTimeUtils.getInstance().getDateFromString(dateTextView.getText().toString());
        List<SymptomModel> symptomModels = SymptomController.getInstance(this).selectAllByDateAndSide(currentDate.getTime(), currentBodySide);

        // Instantiate new circles from DB data and replace them in the BodyView
        ArrayList<BodyView.Circle> circles = new ArrayList<>();
        for (SymptomModel symptomModel : symptomModels) {
            BodyView.Circle currentCircle = new BodyView.Circle(symptomModel.getCirclePosX(), symptomModel.getCirclePosY(), symptomModel.getCircleRadius());
            circles.add(currentCircle);
        }

        bodyView.replacePoints(circles);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                startSettingsActivity();
                break;
            case R.id.action_about:
                startAboutActivity();
                break;
            case R.id.action_pdf:
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    // Permission has not been granted, request it
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissions, STORAGE_CODE);
                }
                else {
                    // Permission already granted, call login activity
                    checkLoginActivity();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Open login activity to get user info just the first time
    // If the data was already asked just open the dialog for dates range
    private void checkLoginActivity() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userName = preferences.getString(getResources().getString(R.string.sp_user_name),
                getResources().getString(R.string.not_specified_info));
        Log.e("USER INFO", userName);
        if (userName.equals(getResources().getString(R.string.not_specified_info))) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        else {
            showDateRangeDialog();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if (isColorRed(r)) {
            try {
                getNearestSymptomToCoordinates(posXOnTouch, posYOnTouch);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (nearestSymptomToSelectedId != DEFAULT_SELECTED_SYMPTOM_ID_VALUE) {
                menu.setHeaderTitle(R.string.symptom_menu_title);
                getMenuInflater().inflate(R.menu.symptom_menu, menu);
            }
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_symptom:
                updateSymptom(nearestSymptomToSelectedId);
                break;
            case R.id.delete_symptom:
                try {
                    deleteSymptom(nearestSymptomToSelectedId);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAboutActivity() {
        startActivity(new Intent(this, About.class));
    }

    private void startSettingsActivity() {
        startActivity(new Intent(this, Settings.class));
    }

    public void launchCircleSizeSelectionDialog() {
        if (sizeSelectionDialog == null) {
            sizeSelectionDialog = new CircleSizeSelectionDialog(this, R.style.BottomSheetDialogTheme);
            sizeSelectionDialog.setOnCircleSelectedListener(this);
            sizeSelectionDialog.setOnCircleSizeUpdateListener(this);
        }
        sizeSelectionDialog.show();
    }

    private SymptomModel getNearestSymptomToCoordinates(float posX, float posY) throws ParseException {
        long today = DateTimeUtils.getInstance().getDateFromString(dateTextView.getText().toString()).getTime();
        List<SymptomModel> todaySymptoms = SymptomController.getInstance(this).selectAllByDateAndSide(today, currentBodySide);
        List<SymptomDistancePair> distances = new ArrayList<>();

        // Use pythagoras formula to calculate distances between points
        for (SymptomModel symptomModel : todaySymptoms) {
            float xDistance = Math.abs(posX - symptomModel.getCirclePosX());
            float yDistance = Math.abs(posY - symptomModel.getCirclePosY());
            double totalDistance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
            distances.add(new SymptomDistancePair(totalDistance, symptomModel.getSymptomId()));
        }

        // Using lambda expression to sort the list of pairs by distance
        Collections.sort(distances, (symptomDistancePair1, symptomDistancePair2) ->
                Double.compare(symptomDistancePair1.distance, symptomDistancePair2.distance));

        // Get the first id which is the nearest symptom id
        nearestSymptomToSelectedId = distances.get(0).symptomId;
        return SymptomController.getInstance(this).select(nearestSymptomToSelectedId);

    }

    private boolean isColorRed(int r) {
        return (r == RED_INSIDE_OF_BODY || r == RED_OUTSIDE_OF_BODY
                || r == RED_IN_2_CIRCLES_JOINT || r == RED_IN_3_CIRCLES_JOINT);
    }

    private boolean isColorWhite(int r, int g, int b) {
        return r == 255 && g == 255 && b == 255;
    }

    private boolean isColorGrey(int r, int g, int b) {
        return r == 230 && g == 230 && b == 230;
    }

    // Receives the posX and posY from the body view where the user touched
    @Override
    public void onFragmentInteraction(Uri uri) {
        posXOnTouch = Float.parseFloat(uri.getQueryParameter("x").replace(",", "."));
        posYOnTouch = Float.parseFloat(uri.getQueryParameter("y").replace(",", "."));
        r = Integer.parseInt(uri.getQueryParameter("r").replace(",", "."));
        g = Integer.parseInt(uri.getQueryParameter("g").replace(",", "."));
        b = Integer.parseInt(uri.getQueryParameter("b").replace(",", "."));

        if (isColorGrey(r, g, b)) {
            if (currentCircle == null) currentCircle = new BodyView.Circle(0, 0, 10);
            currentCircle.x = posXOnTouch;
            currentCircle.y = posYOnTouch;
            bodyView.setTemporaryPoint(currentCircle);
            launchCircleSizeSelectionDialog();
        }
    }

    @Override
    public void OnCircleSizeUpdate(float radius) {
        currentCircle.radius = radius;
        bodyView.setTemporaryPoint(currentCircle);
    }

    @Override
    public void OnCircleSizeSelected(float radius) {
        sizeSelectionDialog.dismiss();
        currentCircle.radius = radius;
        bodyView.addPoint(currentCircle);
        launchSymptomForm();
    }

    // Create new intent and send current circles information through Bundle
    private void launchSymptomForm() {
        Intent newIntent = new Intent(this, SymptomForm.class);
        Bundle data = new Bundle();
        data.putParcelable("Circle", currentCircle);
        data.putString("Date", dateTextView.getText().toString());
        data.putString("Time", DateTimeUtils.getInstance().getCurrentTimeAsString());
        data.putInt("State", currentBodySide);
        newIntent.putExtras(data);
        startActivity(newIntent);
    }

    private void updateSymptom(long symptomId) {
        Intent newIntent = new Intent(this, SymptomForm.class);
        Bundle data = new Bundle();
        data.putLong("symptom_id", symptomId);
        newIntent.putExtras(data);
        startActivity(newIntent);

        // Reset the value
        nearestSymptomToSelectedId = DEFAULT_SELECTED_SYMPTOM_ID_VALUE;
    }

    private int finishSymptom(long nearestSymptomToSelectedId) {
        SymptomModel symptomModel = SymptomController.getInstance(this).select(nearestSymptomToSelectedId);
        long startTime = symptomModel.getStartTime();
        symptomModel.setSymptomId(nearestSymptomToSelectedId);
        long currentTime = DateTimeUtils.getInstance().getCurrentDateTimeAsLong();
        symptomModel.setDuration(DateTimeUtils.getInstance().getTimeDifference(startTime, currentTime));
        return SymptomController.getInstance(this).update(symptomModel);
    }

    private void deleteSymptom(final long symptomId) throws ParseException {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_sure_about_deleting)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int symptomDeletionSuccess = SymptomController.getInstance(getApplicationContext()).delete(symptomId);
                        int categoriesDeletionSuccess = SelectedCategoryOptionController.getInstance(getApplicationContext()).deleteAllBySymptom(symptomId);
                        if (symptomDeletionSuccess != -1 && categoriesDeletionSuccess != -1){
                            try { updateSymptomsInBodyView(); }
                            catch (ParseException e) { e.printStackTrace(); }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .show();

        // Reset the value
        nearestSymptomToSelectedId = DEFAULT_SELECTED_SYMPTOM_ID_VALUE;
    }

    private void showDateRangeDialog(){
        DateRangeForPDFDialog dateRangeForPDFDialog = new DateRangeForPDFDialog();
        dateRangeForPDFDialog.show(getSupportFragmentManager(), "DATE_RANGE");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        TextView startDate = dialog.getDialog().findViewById(R.id.start_date_range);
        TextView endDate = dialog.getDialog().findViewById(R.id.end_date_range);
        try {
            generatePDF(startDate.getText().toString(), endDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_CODE) {
            // Permission was granted from popup, call generate pdf method
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLoginActivity();
            } else // Permission was denied from popup, show error message
                Toast.makeText(this, getResources().getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    private void generatePDF(String startDate, String endDate) throws ParseException {
        PDFGenerator pdfGenerator = new PDFGenerator(getApplicationContext());

        long startDateLong = DateTimeUtils.getInstance().getDateFromString(startDate).getTime();
        long endDateLong = DateTimeUtils.getInstance().getDateFromString(endDate).getTime();

        if(pdfGenerator.generateCompletePDF(startDateLong, endDateLong))
            Toast.makeText(this, getResources().getString(R.string.pdf_success), Toast.LENGTH_SHORT).show();

        else Toast.makeText(this, getResources().getString(R.string.pdf_failure), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    // Class used to hold distances between symptoms coordinates
    private static class SymptomDistancePair {
        double distance;
        long symptomId;
        SymptomDistancePair(double distance, long symptomId){
            this.distance = distance;
            this.symptomId = symptomId;
        }
    }

}
