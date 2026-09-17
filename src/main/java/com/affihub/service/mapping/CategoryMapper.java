package com.affihub.service.mapping;

import com.affihub.object.Category;

import java.util.Map;

public class CategoryMapper {
    public Category mapRow(Map<String, Object> row) {
        return new Category(
                getLong(row, "categoryid"),
                String.valueOf(row.get("name")),
                getLong(row, "parentid")
        );
    }

    private long getLong(Map<String, Object> row, String columnName) {
        Object value = row.get(columnName);
        if (value == null) {
            return 0;
        }
        return value instanceof Number
                ? ((Number) value).longValue()
                : Long.parseLong(String.valueOf(value));
    }
}
