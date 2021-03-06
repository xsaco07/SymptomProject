package com.project.symptoms.db.dao;

import com.project.symptoms.db.model.SymptomModel;
import com.project.symptoms.db.model.SymptomViewModel;

import java.util.List;

public interface SymptomDao {

    long insert(SymptomModel symptomModel) throws Exception;

    List<SymptomModel> selectAll() throws Exception;

    List<SymptomModel> selectAllByDateAndSide(long dateTime, int circleSide) throws Exception;

    List<SymptomModel> select(long initialDate, long finalDate) throws Exception;

    List<SymptomViewModel> selectFromView(long symptomId) throws Exception;

    int delete(long id) throws Exception;

    int update(SymptomModel symptomModel) throws Exception;

    SymptomModel select(long id) throws Exception;
}
