package com.example.project2.models;

import java.io.Serializable;

public class CategoryModel implements Serializable {

    String img_url;
    String name;
    String type;
    double price;

    public CategoryModel() {
    }

    public CategoryModel(String img_url, String name, String type,double price) {
        this.img_url = img_url;
        this.name = name;
        this.type = type;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
