package com.affihub.service.mapping;

import com.affihub.object.Product;
import java.util.Map;

public class ProductMapper {
    public Product mapRow(Map<String, Object> row) {
        if (row == null) {
            return null;
        }

        return new Product(
                getLong(row, "productid"),
                getString(row, "name"),
                getString(row, "description"),
                getLong(row, "categoryid"),
                getLong(row, "price"),
                getInt(row, "clicksum"),
                getString(row, "imageurl"),
                getString(row, "affiliatelink"),
                getLong(row, "createdtime"),
                getLong(row, "updatedtime")
        );
    }

    private String getString(Map<String, Object> row, String columnName) {
        Object value = row.get(columnName);
        return value == null ? null : String.valueOf(value);
    }

    private long getLong(Map<String, Object> row, String columnName) {
        Object value = row.get(columnName);
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private int getInt(Map<String, Object> row, String columnName) {
        Object value = row.get(columnName);
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

}
