package com.affihub.service;

import com.affihub.object.ProductClick;

public interface ProductClickService {
    void create(ProductClick productClick);

    long countByProductAndTime(long productId, long from, long to);
}
