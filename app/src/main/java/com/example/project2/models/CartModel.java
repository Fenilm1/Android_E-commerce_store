package com.example.project2.models;

public class CartModel {
    private String name;
    private String img_url;
    private double price;
    private int quantity;
    private double unitPrice;
    private String itemId;

    public CartModel() {

    }

    public CartModel(String name, String img_url, double price, int quantity,String itemId,double unitPrice) {
        this.name = name;
        this.img_url = img_url;
        this.price = price;
        this.quantity = quantity;
        this.itemId = itemId;
        this.unitPrice = unitPrice;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return img_url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
