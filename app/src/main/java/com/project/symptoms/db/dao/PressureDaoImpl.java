package com.project.symptoms.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.project.symptoms.db.Contract;
import com.project.symptoms.db.DBHelper;
import com.project.symptoms.db.model.PressureModel;

import java.util.ArrayList;
import java.util.List;

public class PressureDaoImpl implements PressureDao {

    private DBHelper dbHelper;

    public PressureDaoImpl(Context context){
        dbHelper = new DBHelper(context);
    }


    @Override
    public long insert(PressureModel pressureModel) throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.Pressure.COLUMN_NAME_DIASTOLIC, pressureModel.getDiastolic());
        values.put(Contract.Pressure.COLUMN_NAME_SYSTOLIC, pressureModel.getSystolic());
        values.put(Contract.Pressure.COLUMN_NAME_DATE, pressureModel.getDate());
        values.put(Contract.Pressure.COLUMN_NAME_TIME, pressureModel.getTime());
        long newId = db.insert(Contract.Pressure.TABLE_NAME,null, values);
        db.close();
        return newId;

    }

    @Override
    public List<PressureModel> selectAll() throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(Contract.Pressure.TABLE_NAME, null, null, null, null, null, null);
        List<PressureModel> result = buildListFromCursor(cursor);
        db.close();
        return result;
    }

    private List<PressureModel> buildListFromCursor(Cursor cursor){
        List<PressureModel> result = new ArrayList<>();
        while(cursor.moveToNext()){
            result.add(buildModelFromCursor(cursor));
        }
        return result;
    }

    @Override
    public int delete(long id) throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = Contract.Pressure.COLUMN_NAME_ID_PK + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {Long.toString(id)};
        // Issue SQL statement.
        int affected = db.delete(Contract.Pressure.TABLE_NAME, selection, selectionArgs); //deletedRows;
        return affected;
    }

    @Override
    public int update(PressureModel model) throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Contract.Pressure.COLUMN_NAME_DIASTOLIC, model.getDiastolic());
        values.put(Contract.Pressure.COLUMN_NAME_SYSTOLIC, model.getSystolic());
        values.put(Contract.Pressure.COLUMN_NAME_DATE, model.getDate());
        values.put(Contract.Pressure.COLUMN_NAME_TIME, model.getTime());

        String selection = Contract.Pressure.COLUMN_NAME_ID_PK + " = ?";
        String[] selectionArgs = {""+model.getId()};

        int count = db.update(Contract.Pressure.TABLE_NAME, values, selection, selectionArgs);
        return count;
    }

    @Override
    public PressureModel select(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = Contract.Pressure.COLUMN_NAME_ID_PK + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {Long.toString(id)};
        // Issue SQL statement.
        Cursor cursor = db.query(Contract.Pressure.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        cursor.moveToNext();
        return buildModelFromCursor(cursor);
    }

    private PressureModel buildModelFromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(Contract.Pressure.COLUMN_NAME_ID_PK));
        int systolic = cursor.getInt(cursor.getColumnIndex(Contract.Pressure.COLUMN_NAME_SYSTOLIC));
        int diastolic = cursor.getInt(cursor.getColumnIndex(Contract.Pressure.COLUMN_NAME_DIASTOLIC));
        long date = cursor.getLong(cursor.getColumnIndex(Contract.Pressure.COLUMN_NAME_DATE));
        long time = cursor.getLong(cursor.getColumnIndex(Contract.Pressure.COLUMN_NAME_TIME));
        return new PressureModel(id, systolic, diastolic, date, time);
    }


}
