package com.affihub.service.mapping;

import com.affihub.object.Product;
import java.util.Map;

public class ProductMapper {
    public Product mapRow(Map<String, Object> row) {
        if (row == null) {
            return null;
        }

        return new Product(
                getLong(row, "id"),
                getString(row, "name"),
                getString(row, "description"),
                getString(row, "category"),
                getLong(row, "price"),
                getInt(row, "click_sum"),
                getString(row, "image_url"),
                getString(row, "affiliate_link"),
                getLong(row, "created_time"),
                getLong(row, "updated_time")
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
