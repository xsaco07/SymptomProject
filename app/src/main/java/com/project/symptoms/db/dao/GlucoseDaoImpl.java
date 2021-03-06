package com.project.symptoms.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.project.symptoms.db.Contract;
import com.project.symptoms.db.DBHelper;
import com.project.symptoms.db.model.GlucoseModel;

import java.util.ArrayList;
import java.util.List;

public class GlucoseDaoImpl implements GlucoseDao {
    private DBHelper dbHelper;

    public GlucoseDaoImpl(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long insert(GlucoseModel glucoseModel) throws Exception {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Contract.Glucose.COLUMN_NAME_VALUE, glucoseModel.getValue());
        values.put(Contract.Glucose.COLUMN_NAME_DATE, glucoseModel.getDate());
        values.put(Contract.Glucose.COLUMN_NAME_TIME, glucoseModel.getTime());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(Contract.Glucose.TABLE_NAME, null, values);
        return newRowId;
    }

    public int update(GlucoseModel glucoseModel) throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //New value for one column
        ContentValues values = new ContentValues();
        values.put(Contract.Glucose.COLUMN_NAME_VALUE, glucoseModel.getValue());
        values.put(Contract.Glucose.COLUMN_NAME_DATE, glucoseModel.getDate());
            values.put(Contract.Glucose.COLUMN_NAME_TIME, glucoseModel.getTime());

        String selection = Contract.Glucose.COLUMN_NAME_ID_PK + " = ?";
        String[] selectionArgs = {Long.toString(glucoseModel.getId())};

        int count = db.update(Contract.Glucose.TABLE_NAME, values, selection, selectionArgs);
        return count;
    }

    public int delete(long id) throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = Contract.Glucose.COLUMN_NAME_ID_PK + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {Long.toString(id)};
        // Issue SQL statement.
        return db.delete(Contract.Glucose.TABLE_NAME, selection, selectionArgs); //deletedRows;
    }

    public GlucoseModel select(long id) throws Exception {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
                Contract.Glucose.COLUMN_NAME_ID_PK,
                Contract.Glucose.COLUMN_NAME_VALUE,
                Contract.Glucose.COLUMN_NAME_DATE,
                Contract.Glucose.COLUMN_NAME_TIME
        };

        String selection = Contract.Glucose.COLUMN_NAME_ID_PK + " = ?";
        String[] selectionArgs = {"" + id};

        Cursor cursor = db.query(
                Contract.Glucose.TABLE_NAME,   // The table to query
                columns,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                  // don't filter by row groups
                null               // The sort order
        );

        cursor.moveToFirst();
        return buildModelFromCursor(cursor);
    }

    @Override
    public List<GlucoseModel> listAll() throws Exception {
        Cursor cursor = queryAll();
        ArrayList<GlucoseModel> result = new ArrayList<>();
        while(cursor.moveToNext()){
            result.add(buildModelFromCursor(cursor));
        }
        cursor.close();
        return result;
    }

    @Override
    public List<GlucoseModel> select(long initialDate, long finalDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.e("LOG", "Glucose dates: " + initialDate + ", " + finalDate);
        String whereClause = Contract.Glucose.COLUMN_NAME_DATE + " BETWEEN ? AND ?";
        String[] whereArgs = new String[] {Long.toString(initialDate), Long.toString(finalDate)};
        Cursor cursor = db.query(Contract.Glucose.TABLE_NAME, null, whereClause, whereArgs, null, null, null);
        List<GlucoseModel> result = new ArrayList<>();
        while(cursor.moveToNext()){
            result.add(buildModelFromCursor(cursor));
        }
        cursor.close();
        db.close();
        return result;
    }

    private GlucoseModel buildModelFromCursor(Cursor cursor){
        long id = cursor.getLong(cursor.getColumnIndex(Contract.Glucose.COLUMN_NAME_ID_PK));
        int value = cursor.getInt(cursor.getColumnIndex(Contract.Glucose.COLUMN_NAME_VALUE));
        long date = cursor.getLong(cursor.getColumnIndex(Contract.Glucose.COLUMN_NAME_DATE));
        long time = cursor.getLong(cursor.getColumnIndex(Contract.Glucose.COLUMN_NAME_TIME));
        return new GlucoseModel(id, value, date, time);
    }

    private Cursor queryAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
                Contract.Glucose.COLUMN_NAME_ID_PK,
                Contract.Glucose.COLUMN_NAME_VALUE,
                Contract.Glucose.COLUMN_NAME_DATE,
                Contract.Glucose.COLUMN_NAME_TIME
        };

        String sortOrder =
                Contract.Glucose.COLUMN_NAME_DATE + "+"+
                Contract.Glucose.COLUMN_NAME_TIME + " DESC";

        Cursor cursor = db.query(
                Contract.Glucose.TABLE_NAME,   // The table to query
                columns,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                  // don't filter by row groups
                sortOrder               // The sort order
        );
        return cursor;
    }

    public void setDbHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
}
