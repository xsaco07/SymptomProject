package com.project.symptoms.db.controller;

import android.content.Context;

import com.project.symptoms.db.dao.PressureDao;
import com.project.symptoms.db.dao.PressureDaoImpl;
import com.project.symptoms.db.model.PressureModel;
import com.project.symptoms.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PressureController {

    private static PressureController instance;
    private static PressureDao pressureDao;

    private PressureController(){

    }

    public static PressureController getInstance(Context context){
        if(instance == null){
            instance = new PressureController();
            pressureDao = new PressureDaoImpl(context);
        }
        return instance;
    }


    // Return the id of the new row
    public long insert(int systolic, int diastolic, String date, String time){

        long newId = -1;
        try{
            Date completeDatetime = DateTimeUtils.getInstance().joinDateAndTimeFromStrings(date, time);
            PressureModel pressureModel = new PressureModel(systolic, diastolic, completeDatetime.getTime());
            newId = pressureDao.insert(pressureModel);
        }catch (Exception e){
            e.printStackTrace();
        }
        return newId;
    }

    // Return if whether succeeded or not
    public boolean delete(long pressureId){
        try {
            pressureDao.delete(pressureId);
            return true;
        }catch (Exception e ){
            e.printStackTrace();
            return false;
        }

    }

    // Return if whether succeeded or not
    public boolean update(long pressureId, int systolic, int diastolic, long datetime){
        try {
            PressureModel newPressureModel = new PressureModel(systolic, diastolic, datetime);
            pressureDao.update(pressureId, newPressureModel);
            return true;
        }catch (Exception e ){
            e.printStackTrace();
            return false;
        }
    }

    public List<PressureModel> listAll(){
        List<PressureModel> result = new ArrayList<>();
        try{
            result = pressureDao.listAll();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }
}