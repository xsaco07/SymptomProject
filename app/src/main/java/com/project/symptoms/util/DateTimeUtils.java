package com.project.symptoms.util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private  final boolean FORMAT_12H = false;

    public  final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("EEE dd MMM yyyy");

    public  final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat(" hh:mm a");

    public  final SimpleDateFormat DATE_PARSER = new SimpleDateFormat("yyyy/MM/dd");

    public final SimpleDateFormat DATE_FORMAT_FOR_NOTIFICATIONS = new SimpleDateFormat("dd/MM/yyyy");

    private  TextView dateView;
    private  TextView timeView;

    private  Context context;

    private  TimePickerDialog timePickerDialog;
    private  DatePickerDialog datePickerDialog;

    private static DateTimeUtils instance;

    private DateTimeUtils(){

    }

    public static DateTimeUtils getInstance(){
        return new DateTimeUtils();
    }

    /**
     * Set the text to current time by default and setup the picker dialog
     */
    private void initTimePicker(){
        // Init the dialog
        int current_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int current_minute = Calendar.getInstance().get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(context,this, current_hour, current_minute, FORMAT_12H);

        updateTimeView(current_hour, current_minute);
    }

    /**
     * Set the text to current date by default and setup the picker dialog
     */
    private void initDatePicker(){
        // Init the dialog
        int current_year = Calendar.getInstance().get(Calendar.YEAR);
        int current_month = Calendar.getInstance().get(Calendar.MONTH);
        int current_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(context, this, current_year, current_month, current_day);

        updateDateView(current_year, current_month, current_day);

    }

    public String getCurrentDateAsString(){
        int current_year = Calendar.getInstance().get(Calendar.YEAR);
        int current_month = Calendar.getInstance().get(Calendar.MONTH);
        int current_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String date = "";
        try{
            current_month++; // BECAUSE MONTHS START COUNTING AT ZERO
            date =  DATE_FORMATTER.format(DATE_PARSER.parse(current_year+"/"+current_month+"/"+current_day));

        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    public String getCurrentTimeAsString(){
        int current_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int current_minute = Calendar.getInstance().get(Calendar.MINUTE);
        return TIME_FORMATTER.format(new Date(0,0,0, current_hour, current_minute));
    }

    public long getCurrentDateTimeAsLong(){
        return Calendar.getInstance().getTimeInMillis();
    }

    public int getTimeDifference(long dateTime1, long dateTime2){
        long diff = dateTime1 - dateTime2;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        if(hours < 1){
            if(minutes < 1){ return (int)seconds; }
            return (int)minutes;
        }
        return (int)hours;
    }

    public void registerAsTimePicker(TextView v){
        timeView = v;

        setContext(v.getContext());

        initTimePicker();

        // Set the trigger to open the dialog
        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });
    }

    public void registerAsDatePicker(TextView v){
        dateView = v;

        setContext(v.getContext());

        initDatePicker();

        // Set the trigger to open the dialog
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

    }


    private void setContext(Context v){
        context = v;
    }

    private void updateDateView(int year, int month, int day){

        String text = "";
        try{
            month++; // BECAUSE MONTHS START COUNTING AT ZERO
            text =  DATE_FORMATTER.format(DATE_PARSER.parse(year+"/"+month+"/"+day));

        }catch (Exception e){
            e.printStackTrace();
        }
        dateView.setText(text);
    }

    private void updateTimeView(int hour, int minute){
        String text =  TIME_FORMATTER.format(new Date(0,0,0,hour,minute));
        timeView.setText(text);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        updateDateView(year, month, dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        updateTimeView(hourOfDay, minute);
    }

    public Date joinDateAndTimeFromStrings(String date, String time) throws Exception{
        Date dateDate = getDateFromString(date);
        Date timeDate = getTimeFromString(time);
        // Join the the date and time in a single Date object
        Date completeDatetime = new Date(dateDate.getTime());
        completeDatetime.setHours(timeDate.getHours());
        completeDatetime.setMinutes(timeDate.getMinutes());
        return completeDatetime;
    }

    public Date getTomorrowsDateFromString(String date) throws ParseException {
        Date currentDate = getDateFromString(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    public Date getYesterdaysDateFromString(String date) throws ParseException {
        Date currentDate = getDateFromString(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    public Date getDateFromString(String date) throws ParseException { return DATE_FORMATTER.parse(date); }

    public Date getTimeFromString(String time) throws ParseException { return TIME_FORMATTER.parse(time); }

    public String getStringDateFromLong(long date){ return DATE_FORMATTER.format(date); }

    public String getStringTimeFromLong(long time){ return TIME_FORMATTER.format(time); }

    public String getStringDateFromDate(Date date){ return DATE_FORMATTER.format(date); }

}
