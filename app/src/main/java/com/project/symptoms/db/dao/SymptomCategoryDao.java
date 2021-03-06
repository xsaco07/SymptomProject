package com.project.symptoms.db.dao;

import com.project.symptoms.db.model.SymptomCategoryModel;

import java.util.List;

public interface SymptomCategoryDao {

    long insert(SymptomCategoryModel symptomModel) throws Exception;

    SymptomCategoryModel selectSymptomCategory(String name) throws Exception;

    List<SymptomCategoryModel> listAll() throws Exception;

    boolean delete(long id) throws Exception;

    boolean update(long id, SymptomCategoryModel newValues) throws Exception;
}
