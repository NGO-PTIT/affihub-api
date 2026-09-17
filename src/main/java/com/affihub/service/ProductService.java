package com.affihub.service;

import com.affihub.object.Product;
import java.util.List;

public interface ProductService {
    List<Product> findAll();

    List<Product> findByCategory(String category);

    Product findById(Long id);

    void create(Product product);

    void update(Long id, Product product);

    void delete(Long id);
}
