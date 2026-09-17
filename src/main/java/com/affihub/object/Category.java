package com.affihub.object;

import org.json.JSONObject;

public class Category {
    private long categoryId;
    private String name;
    private long parentId;

    public Category(long categoryId, String name, long parentId) {
        this.categoryId = categoryId;
        this.name = name;
        this.parentId = parentId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public JSONObject getAsJSONObject() {
        return new JSONObject()
                .put("categoryId", categoryId)
                .put("name", name)
                .put("parentId", parentId);
    }
}
