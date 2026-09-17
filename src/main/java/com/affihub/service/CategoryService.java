package com.affihub.service;

import com.affihub.object.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();

    List<Category> findByParentId(long parentId);

    Category findById(long id);
}
