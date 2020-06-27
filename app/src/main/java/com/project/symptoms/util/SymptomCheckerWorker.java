package com.project.symptoms.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.project.symptoms.R;

public class SymptomCheckerWorker extends Worker {
    String notificationMessage;
    int symptomId;

    // TODO remove this
    public static boolean tmp = true;

    public SymptomCheckerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.notificationMessage = workerParams.getInputData().getString("message");
        this.symptomId = workerParams.getInputData().getInt("symptom_id",-1);
    }

    @NonNull
    @Override
    public Result doWork() {
        if(symptomStillOpen()){
            Log.i("#","Still open");
            tmp = false;
            NotificationWrapper.getInstance(getApplicationContext()).notify(buildTitle(), buildMessageFor(symptomId));
            NotificationWrapper.getInstance(getApplicationContext()).startReminderFor(symptomId);
        }
        else{
            Log.i("#","Symptom finished");
        }

        return Result.success();

    }

    // TODO check from to the database
    private boolean symptomStillOpen(){
        return tmp;
    }

    private String buildMessageFor(int symtomId){
        String format = getApplicationContext().getResources().getString(R.string.symptom_reminder_format);
        return String.format(format,"X","DATE");
    }

    private String buildTitle(){
        return getApplicationContext().getResources().getString(R.string.app_name);
    }
}