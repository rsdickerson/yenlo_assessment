package org.lab.sms.logic;

import org.lab.sms.domain.CommonBean;

import java.util.List;

public class Stock {

    private String productId;
    private String productName;
    private String locationCode;
    private int quantity;

    private Stock() {
    }

    public Stock(String productId, String productName, String locationCode, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.locationCode = locationCode;
        this.quantity = quantity;
    }

    public Stock(String productId, String locationCode, int quantity) {
        this.productId = productId;
        this.productName = null;
        this.locationCode = locationCode;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public int getQuantity() {
        return quantity;
    }
}
