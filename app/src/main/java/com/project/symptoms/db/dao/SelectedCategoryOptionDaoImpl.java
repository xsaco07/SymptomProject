package com.project.symptoms.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.project.symptoms.db.Contract;
import com.project.symptoms.db.DBHelper;
import com.project.symptoms.db.model.SelectedCategoryOptionModel;

import java.util.ArrayList;
import java.util.List;

public class SelectedCategoryOptionDaoImpl implements SelectedCategoryOptionDao{

    private DBHelper dbHelper;

    public SelectedCategoryOptionDaoImpl(Context context) { this.dbHelper = new DBHelper(context); }

    @Override
    public long insert(SelectedCategoryOptionModel selectedCategoryOptionModel) throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.SelectedCategoryOption.COLUMN_NAME_SYMPTOM_ID_FK, selectedCategoryOptionModel.getSymptomId());
        values.put(Contract.SelectedCategoryOption.COLUMN_NAME_CATEGORY_OPTION_ID_FK, selectedCategoryOptionModel.getCategoryOptionId());
        long newId = db.insert(Contract.SelectedCategoryOption.TABLE_NAME,null, values);
        return newId;
    }

    @Override
    public List<SelectedCategoryOptionModel> selectBySymptomId(long symptomId) throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = "symptom_id = ?";
        String[] whereArgs = new String[] {Long.toString(symptomId)};
        Cursor cursor = db.query(Contract.SelectedCategoryOption.TABLE_NAME, null, whereClause, whereArgs, null, null, null);
        List<SelectedCategoryOptionModel> result = buildListFromCursor(cursor);
        return result;
    }

    private List<SelectedCategoryOptionModel> buildListFromCursor(Cursor cursor) {
        List<SelectedCategoryOptionModel> result = new ArrayList<>();
        int symptomId, categoryId;
        while (cursor.moveToNext()) {
            symptomId = cursor.getInt(cursor.getColumnIndex(Contract.SelectedCategoryOption.COLUMN_NAME_SYMPTOM_ID_FK));
            categoryId = cursor.getInt(cursor.getColumnIndex(Contract.SelectedCategoryOption.COLUMN_NAME_CATEGORY_OPTION_ID_FK));
            result.add(new SelectedCategoryOptionModel(symptomId, categoryId));
        }
        return result;
    }

    @Override
    public List<SelectedCategoryOptionModel> selectAll() throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(Contract.Category.TABLE_NAME, null, null, null, null, null, null);
        List<SelectedCategoryOptionModel> result = buildListFromCursor(cursor);
        return result;
    }

    @Override
    public int delete(long symptomId, long categoryId) throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = Contract.SelectedCategoryOption.COLUMN_NAME_SYMPTOM_ID_FK + " = ? AND "
                + Contract.SelectedCategoryOption.COLUMN_NAME_CATEGORY_OPTION_ID_FK + " = ?";
        String[] selectionArgs = {Long.toString(symptomId), Long.toString(categoryId)};
        int rowsAffected = db.delete(Contract.SelectedCategoryOption.TABLE_NAME, selection, selectionArgs);
        return rowsAffected; // this operation should only delete 1 row in the table
    }

    @Override
    public int deleteBySymptomId(long symptomId) throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = Contract.SelectedCategoryOption.COLUMN_NAME_SYMPTOM_ID_FK + " = ?";
        String[] selectionArgs = {Long.toString(symptomId)};
        int rowsAffected = db.delete(Contract.SelectedCategoryOption.TABLE_NAME, selection, selectionArgs);
        return rowsAffected; // this operation should only delete 1 row in the table
    }

    @Override
    public int update(SelectedCategoryOptionModel newValues) throws Exception {
        return 1;
    }

    public void setDbHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
}
