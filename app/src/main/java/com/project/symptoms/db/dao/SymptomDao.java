package com.project.symptoms.db.dao;

import com.project.symptoms.db.model.SymptomModel;

import java.util.List;

public interface SymptomDao {

    long insert(SymptomModel symptomModel) throws Exception;

    List<SymptomModel> selectAll() throws Exception;

    List<SymptomModel> selectAll(long dateTime, int circleSide) throws Exception;

    int delete(long id) throws Exception;

    int update(SymptomModel model) throws Exception;

    SymptomModel select(long id) throws Exception;
}
