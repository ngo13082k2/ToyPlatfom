package com.example.toyplatform_swp_project.dto;

import java.awt.*;

public class ToyDto {
    private Long toyId;
    private Long categoryId;
    private String name;
    private String description;
    private Double price;
    private Integer amount;
    private Long supplierId;
    private String image;

    public Long getToyId() {
        return toyId;
    }
    public void setToyId(Long toyId) {
        this.toyId = toyId;
    }
    public Long getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public Integer getAmount() {
        return amount;
    }
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    public Long getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    public String getImage() {
        return image.toString();
    }
    public void setImage(String image) {
        this.image = image;
    }
}
