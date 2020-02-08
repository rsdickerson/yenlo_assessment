package org.lab.sms.domain;

public class Item extends CommonBean<Item> {

    @Key
    private String productId;
    @Key
    private String locationCode;
    @Required

    private int quantity;

    public Item(String productId, String locationCode, int quantity) {
        this.productId = productId;
        this.locationCode = locationCode;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
