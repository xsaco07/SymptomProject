package com.project.symptoms.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.project.symptoms.db.DBHelper;
import com.project.symptoms.db.model.SymptomModel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SymptomTest {
    private Context appContext;
    private SymptomDao symptomDao;
    private SQLiteDatabase db;

    @Before
    public void setUp(){
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SymptomDaoImpl symptomDaoImpl = new SymptomDaoImpl(appContext);
        DBHelper dbHelper = new DBHelper(appContext);
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        symptomDaoImpl.setDbHelper(dbHelper);
        symptomDao = symptomDaoImpl;
    }

    @Test
    public void givenSymptom_whenInsert_thenReturnGeneratedId() {
        //given
        long generatedId = -1;
        SymptomModel newSymptom = new SymptomModel();
        newSymptom.setCirclePosX(0);
        newSymptom.setCirclePosY(0);
        newSymptom.setDuration(0);
        newSymptom.setCircleRadius(10);
        newSymptom.setDescription("Description");
        newSymptom.setCausingDrug("causingDrug");
        newSymptom.setCausingFood("causingFood");
        newSymptom.setIntensity("low");
        newSymptom.setIntermittence(1);
        newSymptom.setCircleSide(1);
        newSymptom.setStartDate(1000);
        newSymptom.setStartTime(2000);

        //when
        try {
            generatedId = symptomDao.insert(newSymptom);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //then
        assertThat(generatedId).isNotEqualTo(-1);
    }

    @Test
    public void givenExistedSymptoms_whenListAll_thenReturnedList() {
        //given
        List<SymptomModel> symptomModelList = null;
        SymptomModel symptom1 = new SymptomModel();
        symptom1.setCirclePosX(0);
        symptom1.setCirclePosY(0);
        symptom1.setDuration(0);
        symptom1.setCircleRadius(10);
        symptom1.setDescription("Description");
        symptom1.setCausingDrug("causingDrug");
        symptom1.setCausingFood("causingFood");
        symptom1.setIntensity("low");
        symptom1.setIntermittence(1);
        symptom1.setCircleSide(1);
        symptom1.setStartDate(1000);
        symptom1.setStartTime(2000);

        SymptomModel symptom2 = new SymptomModel();
        symptom2.setCirclePosX(10);
        symptom2.setCirclePosY(10);
        symptom2.setDuration(0);
        symptom2.setCircleRadius(10);
        symptom2.setDescription("Description");
        symptom2.setCausingDrug("causingDrug");
        symptom2.setCausingFood("causingFood");
        symptom2.setIntensity("low");
        symptom2.setIntermittence(1);
        symptom2.setCircleSide(1);
        symptom2.setStartDate(1000);
        symptom2.setStartTime(2000);

        try {
            symptomDao.insert(symptom1);
            symptomDao.insert(symptom2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //when
        try {
            symptomModelList = symptomDao.selectAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //then
        assertThat(symptomModelList).isNotNull();
        assertThat(symptomModelList.size()).isEqualTo(2);
    }

    @Test
    public void givenExistedSymptoms_whenListAllByDateAndSide_thenReturnedList() {
        //given
        List<SymptomModel> symptomModelList = null;
        SymptomModel symptom1 = new SymptomModel();
        symptom1.setCirclePosX(0);
        symptom1.setCirclePosY(0);
        symptom1.setDuration(0);
        symptom1.setCircleRadius(10);
        symptom1.setDescription("Description");
        symptom1.setCausingDrug("causingDrug");
        symptom1.setCausingFood("causingFood");
        symptom1.setIntensity("low");
        symptom1.setIntermittence(1);
        symptom1.setCircleSide(1);
        symptom1.setStartDate(1000);
        symptom1.setStartTime(2000);

        SymptomModel symptom2 = new SymptomModel();
        symptom2.setCirclePosX(10);
        symptom2.setCirclePosY(10);
        symptom2.setDuration(0);
        symptom2.setCircleRadius(10);
        symptom2.setDescription("Description");
        symptom2.setCausingDrug("causingDrug");
        symptom2.setCausingFood("causingFood");
        symptom2.setIntensity("low");
        symptom2.setIntermittence(1);
        symptom2.setCircleSide(1);
        symptom2.setStartDate(0);
        symptom2.setStartTime(0);

        try {
            symptomDao.insert(symptom1);
            symptomDao.insert(symptom2);

            //when
            symptomModelList = symptomDao.selectAllByDateAndSide(0, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //then
        assertThat(symptomModelList).isNotNull();
        assertThat(symptomModelList.size()).isEqualTo(1);
    }

    @Test
    public void givenExistedSymptomId_whenDeleted_thenReturnTotalRowsAffected() {
        //given
        long generatedId = -1;
        int totalRowsAffected = -1;
        SymptomModel symptom = new SymptomModel();
        symptom.setCirclePosX(0);
        symptom.setCirclePosY(0);
        symptom.setDuration(0);
        symptom.setCircleRadius(10);
        symptom.setDescription("Description");
        symptom.setCausingDrug("causingDrug");
        symptom.setCausingFood("causingFood");
        symptom.setIntensity("low");
        symptom.setIntermittence(1);
        symptom.setCircleSide(1);
        symptom.setStartDate(1000);
        symptom.setStartTime(2000);

        //when
        try {
            generatedId = symptomDao.insert(symptom);
            totalRowsAffected = symptomDao.delete(generatedId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //then
        assertThat(totalRowsAffected).isEqualTo(1);
    }

    @Test
    public void givenExitedSymptom_whenUpdate_thenReturnTotalRowsAffected() {
        //given
        long generatedId = -1;
        int totalRowsAffected = -1;
        String descriptionUpdated = "descriptionUpdated";
        SymptomModel symptom = new SymptomModel();
        symptom.setCirclePosX(0);
        symptom.setCirclePosY(0);
        symptom.setDuration(0);
        symptom.setCircleRadius(10);
        symptom.setCausingDrug("causingDrug");
        symptom.setCausingFood("causingFood");
        symptom.setIntensity("low");
        symptom.setIntermittence(1);
        symptom.setCircleSide(1);
        symptom.setStartDate(1000);
        symptom.setStartTime(2000);

        //when
        try {
            generatedId = symptomDao.insert(symptom);
            symptom.setSymptomId(generatedId);
            symptom.setDescription(descriptionUpdated);

            totalRowsAffected = symptomDao.update(symptom);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //then
        assertThat(totalRowsAffected).isEqualTo(1);
    }

    @Test
    public void givenExistedSymptomId_whenSelectById_thenReturnSymptom() {
        //given
        long generatedId = -1;
        SymptomModel selectedSymptom = null;
        SymptomModel symptom = new SymptomModel();
        symptom.setCirclePosX(0);
        symptom.setCirclePosY(0);
        symptom.setDuration(0);
        symptom.setCircleRadius(10);
        symptom.setDescription("description");
        symptom.setCausingDrug("causingDrug");
        symptom.setCausingFood("causingFood");
        symptom.setIntensity("low");
        symptom.setIntermittence(1);
        symptom.setCircleSide(1);
        symptom.setStartDate(1000);
        symptom.setStartTime(2000);

        //when
        try {
            generatedId = symptomDao.insert(symptom);

            selectedSymptom = symptomDao.select(generatedId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //then
        assertThat(selectedSymptom).isNotNull();
    }

    @After
    public void cleanUp() {
        db.endTransaction();
    }
}
