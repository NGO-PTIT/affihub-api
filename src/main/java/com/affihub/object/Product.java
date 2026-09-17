package com.affihub.object;

import org.json.JSONObject;

public class Product {
    private long productId;
    private String name;
    private String description;
    private long categoryId;
    private long price;
    private int clickSum;
    private String imageUrl;
    private String affiliateLink;
    private long createdTime;
    private long updatedTime;

    public Product() {
    }

    public Product(long productId, String name, String description, long categoryId, long price, int clickSum, String imageUrl,
                   String affiliateLink, long createdTime, long updatedTime) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.price = price;
        this.clickSum = clickSum;
        this.imageUrl = imageUrl;
        this.affiliateLink = affiliateLink;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getClickSum() {
        return clickSum;
    }

    public void setClickSum(int clickSum) {
        this.clickSum = clickSum;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAffiliateLink() {
        return affiliateLink;
    }

    public void setAffiliateLink(String affiliateLink) {
        this.affiliateLink = affiliateLink;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public JSONObject getAsJSONObject() throws Exception {
        return new JSONObject()
                .put("productId", this.productId)
                .put("name", this.name)
                .put("description", this.description)
                .put("categoryId", this.categoryId)
                .put("price", this.price)
                .put("clickSum", this.clickSum)
                .put("imageUrl", this.imageUrl)
                .put("affiliateLink", this.affiliateLink)
                .put("createdTime", this.createdTime)
                .put("updatedTime", this.updatedTime);
    }
}
