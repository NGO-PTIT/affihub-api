package com.affihub.object;

import org.json.JSONObject;

/**
 * Một lần click của product. Mỗi click được lưu thành một bản ghi riêng.
 */
public class ProductClick {
    private long productId;
    private long clickedTime;
    private JSONObject sourceInfo;

    public ProductClick(long productId, long clickedTime, JSONObject sourceInfo) {
        this.productId = productId;
        this.clickedTime = clickedTime;
        this.sourceInfo = sourceInfo;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getClickedTime() {
        return clickedTime;
    }

    public void setClickedTime(long clickedTime) {
        this.clickedTime = clickedTime;
    }

    public JSONObject getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(JSONObject sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public JSONObject getAsJSONObject() {
        return new JSONObject()
                .put("productId", productId)
                .put("clickedTime", clickedTime)
                .put("sourceInfo", sourceInfo == null ? JSONObject.NULL : sourceInfo);
    }
}
